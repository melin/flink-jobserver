package io.github.melin.flink.jobserver.driver.task;

import com.github.melin.superior.sql.parser.job.JobTaskHelper;
import com.github.melin.superior.sql.parser.model.JobData;
import com.github.melin.superior.sql.parser.model.Statement;
import com.github.melin.superior.sql.parser.model.StatementData;
import io.github.melin.flink.jobserver.api.FlinkJob;
import io.github.melin.flink.jobserver.api.FlinkJobServerException;
import io.github.melin.flink.jobserver.core.dto.InstanceDto;
import io.github.melin.flink.jobserver.core.util.CommonUtils;
import io.github.melin.flink.jobserver.driver.FlinkDriverEnv;
import io.github.melin.flink.jobserver.driver.InstanceContext;
import io.github.melin.flink.jobserver.driver.support.FlinkClassLoader;
import io.github.melin.flink.jobserver.driver.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@Service
public class FlinkAppTask extends AbstractFlinkTask {
    private static final Logger LOG = LoggerFactory.getLogger(FlinkAppTask.class);

    @Override
    protected void executeJobText(InstanceDto instanceDto) throws Exception {
        String jarHdfsPath = null;
        try {
            final String defaultFs = FlinkDriverEnv.getFlinkConfig().getString("flink.hadoop.fs.defaultFS", "");
            LogUtils.info("fs.defaultFs : {}", defaultFs);
            String noCommentJobText = CommonUtils.cleanSqlComment(instanceDto.getJobText());
            List<StatementData> statementDatas = JobTaskHelper.getStatementData(noCommentJobText);
            boolean executeJar = false;
            for (StatementData statementData : statementDatas) {
                Statement statement = statementData.getStatement();
                if (statement instanceof JobData) {
                    JobData data = (JobData) statement;
                    String filePath = data.getResourceName();
                    String className = data.getClassName();
                    List<String> params = data.getParams();
                    InstanceContext.setJobClassName(className);

                    FlinkClassLoader loader = new FlinkClassLoader(new URL[]{}, this.getClass().getClassLoader());
                    Thread.currentThread().setContextClassLoader(loader);

                    LogUtils.info("load jar: " + filePath);

                    //String destPath = createTempHdfsFile(hadoopConf, instanceDto, filePath);
                    jarHdfsPath = defaultFs + filePath;
                    loader.addJar(jarHdfsPath);
                    Class<?> clazz = loader.loadClass(className);
                    String groupId = "sg-" + UUID.randomUUID();

                    Object job = clazz.newInstance();
                    LOG.info("exec job classname: {}", className);
                    if (job instanceof FlinkJob) {
                        FlinkJob sparkJob = (FlinkJob) job;

                        sparkJob.runJob(
                                FlinkDriverEnv.getStreamExecutionEnvironment(),
                                FlinkDriverEnv.getTableEnvironment(), params.toArray(new String[0]));
                    } else {
                        throw new FlinkJobServerException(className + " 不是 FlinkJob 的实例");
                    }

                    executeJar = true;
                }
            }

            if (!executeJar) {
                LogUtils.warn("no job has executed!");
            }
        } finally {
            InstanceContext.setJobClassName("");
            //deleteJarHdfsPath(jarHdfsPath);
        }
    }
}
