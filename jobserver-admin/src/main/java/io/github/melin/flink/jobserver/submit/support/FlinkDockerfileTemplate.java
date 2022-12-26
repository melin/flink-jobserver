package io.github.melin.flink.jobserver.submit.support;

import io.github.melin.flink.jobserver.support.ClusterConfig;

import java.io.File;

import static io.github.melin.flink.jobserver.FlinkJobServerConf.JOBSERVER_DRIVER_JAR_NAME;
import static io.github.melin.flink.jobserver.FlinkJobServerConf.JOBSERVER_FLINK_VERSION;

public class FlinkDockerfileTemplate {

    private final String clusterCode;

    private final ClusterConfig clusterConfig;

    private final String clusterConfDir;

    private final String baseImageTag;

    public FlinkDockerfileTemplate(String clusterCode, ClusterConfig clusterConfig, String clusterConfDir) {
        this.clusterCode = clusterCode;
        this.clusterConfig = clusterConfig;
        this.clusterConfDir = clusterConfDir;
        this.baseImageTag = "apache/flink:" + clusterConfig.getValue(clusterCode, JOBSERVER_FLINK_VERSION);
    }

    public String offerDockerfileContent() {
        final String driverJarName = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_JAR_NAME);

        File coreFile = new File(clusterConfDir + "core-site.xml");
        File hdfsFile = new File(clusterConfDir + "hdfs-site.xml");
        File hiveFile = new File(clusterConfDir + "hive-site.xml");

        String dockerfile = "FROM " + baseImageTag + "\n" +
                "RUN mkdir -p $FLINK_HOME/usrlib\n";

        if (coreFile.exists() && hdfsFile.exists()) {
            dockerfile += "COPY " + coreFile.getAbsoluteFile() + " /opt/hadoop-conf/core-site.xml\n";
            dockerfile += "COPY " + hdfsFile.getAbsoluteFile() + " /opt/hadoop-conf/hdfs-site.xml\n";
            dockerfile += "ENV HADOOP_CONF_DIR /opt/hadoop-conf";
        }

        if (hiveFile.exists()) {
            dockerfile += "COPY " + coreFile.getAbsoluteFile() + " /opt/hive-conf/hive-site.xml\n";
            dockerfile += "ENV HADOOP_CONF_DIR /opt/hive-conf";
        }

        dockerfile += "COPY " + driverJarName + "$FLINK_HOME/usrlib/" + driverJarName + "\n";

        return dockerfile;
    }
}
