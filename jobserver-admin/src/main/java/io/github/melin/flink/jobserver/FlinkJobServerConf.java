package io.github.melin.flink.jobserver;

import com.gitee.melin.bee.core.conf.BeeConf;
import com.gitee.melin.bee.core.conf.ConfigEntry;
import org.apache.commons.lang3.SystemUtils;

import java.lang.reflect.Field;

/**
 * @author melin
 */
public class FlinkJobServerConf extends BeeConf {

    public static final String DEFAULT_FLINK_VERSION = "FLINK-1.16.0-bin-hadoop2";

    public static final ConfigEntry<Integer> JOBSERVER_DRIVER_MIN_COUNT =
            buildConf("jobserver.driver.min.count")
                    .doc("driver 最小初始化数量")
                    .version("1.0.0")
                    .intConf()
                    .createWithDefault(1);

    public static final ConfigEntry<Integer> JOBSERVER_DRIVER_MAX_COUNT =
            buildConf("jobserver.driver.max.count")
                    .doc("driver 最大数量")
                    .version("1.0.0")
                    .intConf()
                    .createWithDefault(3);

    public static final ConfigEntry<Integer> JOBSERVER_DRIVER_RUN_MAX_INSTANCE_COUNT =
            buildConf("jobserver.driver.run.max.instance.count")
                    .doc("driver 运行最大作业实例数量，超过最大实例数量，关闭此Driver")
                    .version("1.0.0")
                    .intConf()
                    .createWithDefault(30);

    public static final ConfigEntry<Long> JOBSERVER_DRIVER_MIN_PRIMARY_ID =
            buildConf("jobserver.driver.min.primary.id")
                    .doc("driver 运行最大作业实例数量，超过最大实例数量，关闭此Driver")
                    .version("1.0.0")
                    .longConf()
                    .createWithDefault(0L);

    public static final ConfigEntry<Integer> JOBSERVER_DRIVER_MAX_IDLE_TIME_SECONDS =
            buildConf("jobserver.driver.max.idle.time.seconds")
                    .doc("driver 最大空闲时间, 默认10分钟")
                    .version("1.0.0")
                    .intConf()
                    .createWithDefault(60 * 10);

    public static final ConfigEntry<String> JOBSERVER_DRIVER_HADOOP_USER_NAME =
            buildConf("jobserver.driver.hadoop.user.name")
                    .doc("driver hadoop user name, 默认为 jobserver 系统账号")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault(SystemUtils.getUserName());

    public static final ConfigEntry<String> JOBSERVER_DRIVER_HOME =
            buildConf("jobserver.driver.home")
                    .doc("driver home 路径，一般为 hdfs 路径")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("/user/jobserver/flink-jobserver");

    public static final ConfigEntry<String> JOBSERVER_DRIVER_JAR_NAME =
            buildConf("jobserver.driver.jar.name")
                    .doc("driver 部署 jar 文件名")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("flink-jobserver-driver-0.1.0.jar");

    public static final ConfigEntry<String> JOBSERVER_DRIVER_YARN_QUEUE_NAME =
            buildConf("jobserver.driver.yarn.queue.name")
                    .doc("driver 运行yarn 队列名")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("default");

    public static final ConfigEntry<String> JOBSERVER_DRIVER_DATATUNNEL_JARS_DIR =
            buildConf("jobserver.driver.datatunnel.jars.dir")
                    .doc("drirver datatunnel jar 目录名")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("datatunnel-3.3.0");

    public static final ConfigEntry<Boolean> JOBSERVER_DRIVER_HIVE_ENABLED =
            buildConf("jobserver.driver.hive.enabled")
                    .doc("flink hive enabled")
                    .version("1.0.0")
                    .booleanConf()
                    .createWithDefault(true);

    public static final ConfigEntry<Boolean> JOBSERVER_DRIVER_REMOTE_DEBUG_ENABLED =
            buildConf("jobserver.driver.remote.debug.enabled")
                    .doc("flink driver 开启远程调试")
                    .version("1.0.0")
                    .booleanConf()
                    .createWithDefault(true);

    //------------------------------------------------------------------------------

    public static final ConfigEntry<Integer> JOBSERVER_SUBMIT_DRIVER_MAX_CONCURRENT_COUNT =
            buildConf("jobserver.submit.driver.max.concurrent.count")
                    .doc("Jobserver 提交driver，最大并发数量，")
                    .version("1.0.0")
                    .intConf()
                    .createWithDefault(2);

    public static final ConfigEntry<String> JOBSERVER_FLINK_HOME =
            buildConf("jobserver.flink.home")
                    .doc("jobserver 本地 flink home 路径")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault(SystemUtils.USER_HOME + "/" + DEFAULT_FLINK_VERSION);

    public static final ConfigEntry<String> JOBSERVER_FLINK_VERSION =
            buildConf("jobserver.flink.version")
                    .doc("jobserver flink verion")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("1.16.0");

    public static final ConfigEntry<Integer> JOBSERVER_YARN_MIN_MEMORY_MB =
            buildConf("jobserver.yarn.min.memory.mb")
                    .doc("yarn 最小剩余内存")
                    .version("1.0.0")
                    .intConf()
                    .createWithDefault(4096);

    public static final ConfigEntry<Integer> JOBSERVER_YARN_MIN_CPU_CORES =
            buildConf("jobserver.yarn.min.cpu.cores")
                    .doc("yarn 最小剩余cpu 数量")
                    .version("1.0.0")
                    .intConf()
                    .createWithDefault(5);

    public static final ConfigEntry<String> JOBSERVER_YARN_PROXY_URI =
            buildConf("jobserver.flink.proxy.uri")
                    .doc("yarn 代理地址，用于访问 flink 控制台")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("");

    public static final ConfigEntry<String> JOBSERVER_JOB_JOBMANAGER_JAVA_OPTS =
            buildConf("jobserver.job.jobmanager.java.opts")
                    .doc("jobmanager jvm 参数")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("");

    public static final ConfigEntry<String> JOBSERVER_JOB_TASKMANAGER_JAVA_OPTS =
            buildConf("jobserver.job.taskmanager.java.opts")
                    .doc("taskmanager jvm 参数")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("");

    public static final ConfigEntry<String> JOBSERVER_CUSTOM_FLINK_YARN_JARS =
            buildConf("jobserver.custom.flink.yarn.jars")
                    .doc("用户自定flink yarn jars")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("");

    //--------------------------------docker config-----------------------------
    public static final ConfigEntry<String> JOBSERVER_DOCKER_REGISTER_ADDRESS =
            buildConf("jobserver.docker.register.address")
                    .doc("docker register address")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("");

    public static final ConfigEntry<String> JOBSERVER_DOCKER_REGISTER_USERNAME =
            buildConf("jobserver.docker.register.username")
                    .doc("docker register username")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("");

    public static final ConfigEntry<String> JOBSERVER_DOCKER_REGISTER_PASSWORD =
            buildConf("jobserver.docker.register.password")
                    .doc("docker register password")
                    .version("1.0.0")
                    .stringConf()
                    .createWithDefault("");

    public static final ConfigEntry<Integer> JOBSERVER_DOCKER_MAX_CONNECTIONS =
            buildConf("jobserver.docker.http-client.max-connections")
                    .doc("instantiating max connections for DockerHttpClient")
                    .version("1.0.0")
                    .intConf()
                    .createWithDefault(100);

    public static final ConfigEntry<Long> JOBSERVER_DOCKER_CONNECTION_TIMEOUT_SEC =
            buildConf("jobserver.docker.http-client.connection-timeout-sec")
                    .doc("instantiating connection timeout for DockerHttpClient")
                    .version("1.0.0")
                    .longConf()
                    .createWithDefault(100L);

    public static final ConfigEntry<Long> JOBSERVER_DOCKER_RESPONSE_TIMEOUT_SEC =
            buildConf("jobserver.docker.http-client.response-timeout-sec")
                    .doc("instantiating connection timeout for DockerHttpClient")
                    .version("1.0.0")
                    .longConf()
                    .createWithDefault(120L);

    public static String printConfWithDefaultValue() throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        Field[] fields = FlinkJobServerConf.class.getDeclaredFields();
        for (Field field : fields) {
            Object result = field.get(null);
            if (result instanceof ConfigEntry) {
                ConfigEntry entry = (ConfigEntry) result;
                sb.append(entry.getKey() + " = " + entry.getDefaultValue()).append("\n");
            }
        }

        return sb.toString();
    }
}
