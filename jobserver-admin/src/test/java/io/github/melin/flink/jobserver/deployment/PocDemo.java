package io.github.melin.flink.jobserver.deployment;

public class PocDemo {
    public static void main(String[] args) throws Exception {
        FlinkJobSubmitService submitService = new FlinkJobSubmitService();
        submitService.submitJob();
    }
}
