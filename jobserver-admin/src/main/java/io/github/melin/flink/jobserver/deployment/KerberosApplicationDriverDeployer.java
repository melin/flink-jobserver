package io.github.melin.flink.jobserver.deployment;

import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import io.github.melin.flink.jobserver.deployment.dto.JobInstanceInfo;

public class KerberosApplicationDriverDeployer extends AbstractDriverDeployer {
    @Override
    protected String startApplication(JobInstanceInfo jobInstanceInfo, Long driverId, RuntimeMode runtimeMode) throws Exception {
        return null;
    }
}
