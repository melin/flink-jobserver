package io.github.melin.flink.jobserver.deployment;

import com.gitee.melin.bee.util.NetUtils;
import com.google.common.collect.Lists;
import io.github.melin.flink.jobserver.ConfigProperties;
import io.github.melin.flink.jobserver.core.entity.FlinkDriver;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import io.github.melin.flink.jobserver.core.exception.ResouceLimitException;
import io.github.melin.flink.jobserver.core.exception.FlinkJobException;
import io.github.melin.flink.jobserver.core.service.FlinkDriverService;
import io.github.melin.flink.jobserver.core.util.CommonUtils;
import io.github.melin.flink.jobserver.deployment.dto.JobInstanceInfo;
import io.github.melin.flink.jobserver.support.ClusterConfig;
import io.github.melin.flink.jobserver.support.ClusterManager;
import io.github.melin.flink.jobserver.support.leader.RedisLeaderElection;
import io.github.melin.flink.jobserver.util.FSUtils;
import io.github.melin.flink.jobserver.core.entity.Cluster;
import io.github.melin.flink.jobserver.util.JobServerUtils;
import io.github.melin.flink.jobserver.web.controller.DriverController;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.cli.ApplicationDeployer;
import org.apache.flink.client.deployment.ClusterClientServiceLoader;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.deployment.application.cli.ApplicationClusterDeployer;
import org.apache.flink.configuration.*;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.github.melin.flink.jobserver.FlinkJobServerConf.*;
import static org.apache.flink.configuration.CoreOptions.FLINK_JM_JVM_OPTIONS;
import static org.apache.flink.configuration.CoreOptions.FLINK_TM_JVM_OPTIONS;
import static org.apache.flink.yarn.configuration.YarnConfigOptions.*;

/**
 * 参考 Flink CliFrontend 启动提交FLink Driver
 */
@Service
public class YarnFlinkDriverSubmit {

    private static final Logger LOG = LoggerFactory.getLogger(YarnFlinkDriverSubmit.class);

    private final ClusterClientServiceLoader clusterClientServiceLoader;

    @Autowired
    protected ClusterManager clusterManager;

    @Autowired
    private ClusterConfig clusterConfig;

    @Autowired
    protected FlinkDriverService driverService;

    @Autowired
    private RedisLeaderElection redisLeaderElection;

    @Autowired
    protected ConfigProperties config;

    @Value("${spring.profiles.active}")
    protected String profiles;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUserName;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    public YarnFlinkDriverSubmit() {
        this.clusterClientServiceLoader = new DefaultClusterClientServiceLoader();
    }

    public void buildJobServer(Cluster cluster, RuntimeMode runtimeMode) {
        Long driverId = null;
        String clusterCode = cluster.getCode();
        try {
            //未分配到server的请求重新申请server
            checkLocalAvailableMemory();
            checkMaxDriverCount(clusterCode);
            clusterManager.checkYarnResourceLimit(clusterCode);

            JobInstanceInfo jobInstanceInfo = new JobInstanceInfo();
            String yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YAEN_QUEUE_NAME);
            driverId = initFlinkDriver(clusterCode, true);
            LOG.info("预启动 driver Id: {}", driverId);

            long appSubmitTime = System.currentTimeMillis();
            String applicationId = startApplication(jobInstanceInfo, cluster.getCode(), driverId, yarnQueue, runtimeMode);

            Long times = (System.currentTimeMillis() - appSubmitTime) / 1000L;
            LOG.info("start share jobserver: {}, times: {}s", applicationId, times);

            if (StringUtils.isNotBlank(applicationId)) {
                FlinkDriver driver = driverService.getEntity(driverId);
                if (driver != null) {
                    driver.setApplicationId(applicationId);
                    driverService.updateEntity(driver);
                }
            }

            DriverController.flinkLauncherFailedMsg = "";
        } catch (Throwable e) {
            LOG.info("启动jobserver 失败" + e.getMessage(), e);
            if (driverId != null) {
                LOG.info("delete driverId: {}", driverId);
                driverService.deleteEntity(driverId);
            }

            DriverController.flinkLauncherFailedMsg = "启动jobserver 失败: " + e.getMessage();

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception ignored) {}
        }
    }

    protected String startApplication(JobInstanceInfo jobInstanceInfo, String clusterCode,
                                    Long driverId, String yarnQueue, RuntimeMode runtimeMode) throws Exception {

        final ApplicationDeployer deployer =
                new ApplicationClusterDeployer(clusterClientServiceLoader);

        final String confDir = clusterManager.loadYarnConfig(clusterCode);
        org.apache.hadoop.conf.Configuration hadoopConf = clusterManager.getHadoopConf(clusterCode);
        String defaultFS = hadoopConf.get("fs.defaultFS", "hdfs://dzcluster");
        String driverHome = defaultFS + clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_HOME);
        Configuration flinkConfig = GlobalConfiguration.loadConfiguration(confDir);
        flinkConfig.setString(DeploymentOptions.TARGET, "yarn-application");

        // 加载core-site.xml 和 hdfs-site.xml
        flinkConfig.setString(ConfigConstants.PATH_HADOOP_CONFIG, confDir);
        // 加载 yarn-site.xml
        org.apache.hadoop.conf.Configuration yarnConf = new org.apache.hadoop.conf.Configuration(false);
        yarnConf.clear();
        yarnConf.addResource(new Path(confDir + "/yarn-site.xml"));
        for (Map.Entry<String, String> entry : yarnConf) {
            flinkConfig.setString("flink." + entry.getKey(), entry.getValue());
        }

        LOG.info("aspectj version: {}", config.getAspectjVersion());
        String aspectjweaverJar = "aspectjweaver-" + config.getAspectjVersion() + ".jar";
        //String aspectjPath = driverHome + "/" + aspectjweaverJar;
        Properties params = addJobConfig(flinkConfig, jobInstanceInfo);
        // jm 和 tm jvm 参数
        jvmConfig(clusterCode, flinkConfig, params, driverHome, aspectjweaverJar);

        //设置队列
        if (StringUtils.isNotBlank(yarnQueue)) {
            flinkConfig.setString(APPLICATION_QUEUE, yarnQueue);
        } else {
            yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YAEN_QUEUE_NAME);
            flinkConfig.setString(APPLICATION_QUEUE, yarnQueue);
        }
        String appName = JobServerUtils.appName(profiles);
        flinkConfig.setString(APPLICATION_NAME, appName);
        flinkConfig.setString(APPLICATION_TYPE, "flink-jobserver");

        String hadoopUserName = clusterConfig.getDriverHadoopUserName(clusterCode);
        System.setProperty("HADOOP_USER_NAME", hadoopUserName);
        // Detached模式下，Flink Client创建完集群之后，可以退出命令行窗口，集群独立运行。Attached模式下，Flink Client创建完集群后，不能关闭命令行窗口，需要与集群之间维持连接
        flinkConfig.setBoolean(DeploymentOptions.ATTACHED, false);

        String flinkVersion = clusterConfig.getValue(clusterCode, JOBSERVER_FLINK_VERSION);
        String flinkYarnJarsDir = driverHome + "/flink-" + flinkVersion;
        FSUtils.checkHdfsPathExist(hadoopConf, flinkYarnJarsDir);
        flinkConfig.set(PROVIDED_LIB_DIRS, Lists.newArrayList(flinkYarnJarsDir));
        flinkConfig.set(FLINK_DIST_JAR, driverHome + "/flink-" + flinkVersion + "/flink-dist-" + flinkVersion + ".jar");

        String driverJar = driverHome + "/" + clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_JAR_NAME);
        List<String> jobJars = Lists.newArrayList(driverJar);
        ConfigUtils.encodeCollectionToConfig(flinkConfig, PipelineOptions.JARS, jobJars, Object::toString);

        final String conf = Base64.getEncoder().encodeToString("{}".getBytes(StandardCharsets.UTF_8));
        List<String> programArgs = Lists.newArrayList("-j", String.valueOf(driverId), "-conf", conf,
                "-c", clusterCode, "mode", runtimeMode.getValue());
        boolean hiveEnabled = clusterConfig.getBoolean(clusterCode, JOBSERVER_DRIVER_HIVE_ENABLED);
        if (hiveEnabled) {
            programArgs.add("-hive");
        }
        final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(
                programArgs.toArray(new String[0]), "io.github.melin.flink.jobserver.driver.FlinkDriverApp");
        deployer.run(flinkConfig, applicationConfiguration);
        return flinkConfig.get(APPLICATION_ID);
    }

    private Properties addJobConfig(Configuration flinkConfig, JobInstanceInfo jobInstanceInfo) throws IOException {
        Properties properties = new Properties();
        String jobConfig = jobInstanceInfo.getJobConfig();
        if (StringUtils.isNotBlank(jobConfig)) {
            properties.load(new StringReader(jobConfig));

            for (Object key : properties.keySet()) {
                String propKey = (String) key;
                String value = properties.getProperty(propKey);
                flinkConfig.setString(propKey, value);
                LOG.info("instance config: {} = {}", key, value);
            }
        }

        return properties;
    }

    /**
     * https://nightlies.apache.org/flink/flink-docs-master/docs/ops/debugging/application_profiling/
     */
    private void jvmConfig(String clusterCode, Configuration flinkConfig, Properties params,
                           String driverHome, String aspectjweaverJar) {

        String jobmanagerJavaOpts = getJvmOpts(clusterCode, params, "jobmanager");
        String taskmanagerJavaOpts = getJvmOpts(clusterCode, params, "taskmanager");
        String hadoopUserName = clusterConfig.getDriverHadoopUserName(clusterCode);

        jobmanagerJavaOpts = "-Dfile.encoding=UTF-8"
                + " -Dspring.profiles.active=" + profiles
                + " -DHADOOP_USER_NAME=" + hadoopUserName
                + " -Ddriver.hdfs.home=" + driverHome
                + " -Dspring.datasource.url='" + datasourceUrl + "'"
                + " -Dspring.datasource.username='" + datasourceUserName + "'"
                + " -Dspring.datasource.password='" + datasourcePassword + "'"
                + " " + jobmanagerJavaOpts;
                //+ " -javaagent:" + aspectjweaverJar + " " + jobmanagerJavaOpts;

        boolean remoteDebug = clusterConfig.getBoolean(clusterCode, JOBSERVER_DRIVER_REMOTE_DEBUG_ENABLED);
        if (remoteDebug) {
            jobmanagerJavaOpts += " -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=30112 ";
        }

        //taskmanagerJavaOpts = "-javaagent:" + aspectjweaverJar + " " + taskmanagerJavaOpts;

        taskmanagerJavaOpts += " -DHADOOP_USER_NAME=" + hadoopUserName;

        flinkConfig.set(FLINK_JM_JVM_OPTIONS, jobmanagerJavaOpts);
        flinkConfig.set(FLINK_TM_JVM_OPTIONS, taskmanagerJavaOpts);
    }

    private String getJvmOpts(String clusterCode, Properties params, String role) {
        String key = String.format("spark.job.%s.java.opts", role);
        String jvmOptions = "jobmanager".equals(role) ?
                clusterConfig.getValue(clusterCode, JOBSERVER_JOB_JOBMANAGER_JAVA_OPTS) :
                clusterConfig.getValue(clusterCode, JOBSERVER_JOB_TASKMANAGER_JAVA_OPTS);

        if (StringUtils.isNotBlank(jvmOptions)) {
            jvmOptions = jvmOptions + " " + params.getProperty(key, "");
        } else {
            jvmOptions = params.getProperty(key, "");
        }
        LOG.info("{} jvm options: {}", role, jvmOptions);
        return jvmOptions;
    }

    /**
     * 初始化jobserver实例
     */
    protected Long initFlinkDriver(String clusterCode, boolean shareDriver) {
        Long driverId;
        try {
            FlinkDriver driver = FlinkDriver.buildFlinkDriver(clusterCode, shareDriver);
            String yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YAEN_QUEUE_NAME);
            driver.setYarnQueue(yarnQueue);

            while (!redisLeaderElection.trylock()) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            LOG.info("Get redis lock");

            long initDriverCount = driverService.queryCount("status", DriverStatus.INIT);
            long maxConcurrentSubmitCount = clusterConfig.getInt(clusterCode, JOBSERVER_SUBMIT_DRIVER_MAX_CONCURRENT_COUNT);
            if (initDriverCount > maxConcurrentSubmitCount) {
                String msg = "当前正在提交jobserver数量: " + initDriverCount + ", 最大提交数量: " + maxConcurrentSubmitCount
                        + ", 可调整参数: jobserver.concurrent.submit.max.num";
                throw new ResouceLimitException(msg);
            }
            driverId = driverService.insertEntity(driver);
        } catch (FlinkJobException jobException) {
            throw jobException;
        } catch (Exception e1) {
            throw new RuntimeException(e1.getMessage());
        } finally {
            redisLeaderElection.deletelock();
        }

        if (driverId == null) {
            throw new RuntimeException("Init Flink Driver Error");
        }
        return driverId;
    }

    protected void checkLocalAvailableMemory() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();

        int kb = 1024;
        long totalMemorySize = hal.getMemory().getTotal() / kb;
        long availableMemorySize = hal.getMemory().getAvailable() / kb;
        long minAvailableMem = config.getLocalMinMemoryMb();

        String totalMemorySizeRead = CommonUtils.convertUnit(totalMemorySize);
        String availableMemorySizeRead = CommonUtils.convertUnit(availableMemorySize);

        if (availableMemorySize < (minAvailableMem * 1024)) {
            String msg = "当前系统总内存: " + totalMemorySizeRead + ", 可用内存: " + availableMemorySizeRead
                    + ", 要求最小可用内存: " + minAvailableMem + "m " + NetUtils.getLocalHost();
            msg = msg + ", 可调整参数：jobserver.local-min-memory-mb, 单位兆";
            LOG.warn(msg);
            throw new ResouceLimitException(msg);
        }
    }

    protected void checkMaxDriverCount(String clusterCode) {
        long driverCount = driverService.queryCount();
        int driverMaxCount = clusterConfig.getInt(clusterCode, JOBSERVER_DRIVER_MAX_COUNT);
        if (driverCount >= driverMaxCount) {
            String msg = "当前正在运行任务数量已达最大数量限制: " + driverMaxCount + "，请休息一会再重试！";
            throw new ResouceLimitException(msg);
        }
    }
}
