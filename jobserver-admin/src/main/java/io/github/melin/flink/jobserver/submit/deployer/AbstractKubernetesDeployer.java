package io.github.melin.flink.jobserver.submit.deployer;

abstract public class AbstractKubernetesDeployer extends AbstractDriverDeployer {

    protected void buildDockerImage(String clusterId) {
        String expectedImageTag = "flink-jobserver-job-" + clusterId;
    }
}
