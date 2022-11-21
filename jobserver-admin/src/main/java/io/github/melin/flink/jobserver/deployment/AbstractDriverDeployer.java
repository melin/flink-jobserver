package io.github.melin.flink.jobserver.deployment;

import com.gitee.melin.bee.util.NetUtils;
import com.google.common.collect.Lists;
import io.github.melin.flink.jobserver.ConfigProperties;
import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import io.github.melin.flink.jobserver.core.exception.ResouceLimitException;
import io.github.melin.flink.jobserver.core.service.FlinkDriverService;
import io.github.melin.flink.jobserver.core.util.CommonUtils;
import io.github.melin.flink.jobserver.deployment.dto.JobInstanceInfo;
import io.github.melin.flink.jobserver.support.ClusterConfig;
import io.github.melin.flink.jobserver.support.ClusterManager;
import io.github.melin.flink.jobserver.util.FSUtils;
import io.github.melin.flink.jobserver.util.JobServerUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.*;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static io.github.melin.flink.jobserver.FlinkJobServerConf.*;
import static io.github.melin.flink.jobserver.FlinkJobServerConf.JOBSERVER_DRIVER_HIVE_ENABLED;
import static org.apache.flink.configuration.CoreOptions.FLINK_JM_JVM_OPTIONS;
import static org.apache.flink.configuration.CoreOptions.FLINK_TM_JVM_OPTIONS;
import static org.apache.flink.yarn.configuration.YarnConfigOptions.*;

abstract public class AbstractDriverDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDriverDeployer.class);

    @Autowired
    protected ConfigProperties config;

    @Autowired
    protected ClusterManager clusterManager;

    @Autowired
    private ClusterConfig clusterConfig;

    @Autowired
    protected FlinkDriverService driverService;

    @Value("${spring.profiles.active}")
    protected String profiles;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUserName;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    abstract protected String startApplication(JobInstanceInfo jobInstanceInfo, Long driverId, RuntimeMode runtimeMode) throws Exception;

    protected Configuration buildFlinkConfig(JobInstanceInfo jobInstanceInfo) throws Exception {
        String clusterCode = jobInstanceInfo.getClusterCode();
        final String confDir = clusterManager.loadYarnConfig(clusterCode);
        org.apache.hadoop.conf.Configuration hadoopConf = clusterManager.getHadoopConf(clusterCode);
        String defaultFS = hadoopConf.get("fs.defaultFS", "hdfs://dzcluster");
        String driverHome = defaultFS + clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_HOME);
        Configuration flinkConfig = GlobalConfiguration.loadConfiguration(confDir);

        // 加载core-site.xml 和 hdfs-site.xml
        flinkConfig.setString(ConfigConstants.PATH_HADOOP_CONFIG, confDir);
        // 加载 yarn-site.xml
        org.apache.hadoop.conf.Configuration yarnConf = new org.apache.hadoop.conf.Configuration(false);
        yarnConf.clear();
        yarnConf.addResource(new Path(confDir + "/yarn-site.xml"));
        for (Map.Entry<String, String> entry : yarnConf) {
            flinkConfig.setString("flink." + entry.getKey(), entry.getValue());
        }

        Properties params = addJobConfig(flinkConfig, jobInstanceInfo);
        // jm 和 tm jvm 参数
        jvmConfig(clusterCode, flinkConfig, params, driverHome);

        //设置队列
        String yarnQueue = jobInstanceInfo.getYarnQueue();
        if (StringUtils.isNotBlank(yarnQueue)) {
            flinkConfig.setString(APPLICATION_QUEUE, yarnQueue);
        } else {
            yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YARN_QUEUE_NAME);
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
        return flinkConfig;
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
    private void jvmConfig(String clusterCode, Configuration flinkConfig, Properties params, String driverHome) {

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

        boolean remoteDebug = clusterConfig.getBoolean(clusterCode, JOBSERVER_DRIVER_REMOTE_DEBUG_ENABLED);
        if (remoteDebug) {
            jobmanagerJavaOpts += " -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=30112 ";
        }

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
