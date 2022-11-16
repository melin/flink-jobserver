package io.github.melin.flink.jobserver.driver;

import com.gitee.melin.bee.core.support.Result;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import io.github.melin.flink.jobserver.driver.task.FlinkJarTask;
import io.github.melin.flink.jobserver.driver.task.FlinkSqlTask;
import io.github.melin.flink.jobserver.core.dto.InstanceDto;
import io.github.melin.flink.jobserver.core.enums.JobType;
import io.github.melin.flink.jobserver.core.util.LogRecord;
import io.github.melin.flink.jobserver.core.util.TaskStatusFlag;
import io.github.melin.flink.jobserver.driver.util.LogUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * huaixin 2022/4/11 18:22
 */
@RestController
public class FlinkDriverRest {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkDriverRest.class);

    @Autowired
    private FlinkDriverContext flinkDriverContext;

    @Autowired
    private FlinkSqlTask flinkSqlTask;

    @Autowired
    private FlinkJarTask flinkJarTask;

    @RequestMapping(value = "/ok", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String ok() {
        return "ok";
    }

    /**
     * 查询job 是否在运行
     *
     * @param instanceCode
     * @return
     */
    @RequestMapping("/flinkDriver/isJobRunning")
    public boolean isJobRunning(String instanceCode) {
        LOG.info("isJobRunning, instance status: {}, code: {}",
                flinkDriverContext.getStatus(), InstanceContext.getInstanceCode());

        if (flinkDriverContext.getStatus() == DriverStatus.RUNNING
                && instanceCode.equalsIgnoreCase(InstanceContext.getInstanceCode())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * control端抽取日志接口
     */
    @RequestMapping("/flinkDriver/getServerLog")
    public List<LogRecord> getServerLog(String instanceCode) {
        return LogUtils.getMessage(instanceCode);
    }

    /**
     * 提交spark任务的接口
     */
    @PostMapping("/flinkDriver/runSparkJob")
    public Result<String> runSparkJob(@RequestBody InstanceDto instanceDto) {
        LogUtils.clearLog(instanceDto.getInstanceCode());

        InstanceContext.setInstanceType(instanceDto.getInstanceType());
        InstanceContext.setAccessKey(instanceDto.getAccessKey());
        InstanceContext.setInstanceCode(instanceDto.getInstanceCode());
        InstanceContext.setAccessKey(instanceDto.getAccessKey());
        LOG.info("spark dirver received job");

        LogUtils.info("当前 yarn queue: {}, ApplicationId: {}, shareDriver: {}",
                instanceDto.getYarnQueue(), FlinkEnv.getApplicationId(), String.valueOf(instanceDto.isShareDriver()));

        LOG.info("Spark task: {} begined, submit from {}",
                instanceDto.getInstanceCode(), instanceDto.getSparkJobServerUrl());

        JobType jobType = instanceDto.getJobType();
        if (JobType.FLINK_BATCH_SQL == jobType || JobType.FLINK_STREAM_SQL == jobType) {
            flinkDriverContext.startDriver();
            return flinkSqlTask.runTask(instanceDto);
        } else if (JobType.FLINK_BATCH_JAR == jobType || JobType.FLINK_STREAM_JAR == jobType) {
            flinkDriverContext.startDriver();
            return flinkJarTask.runTask(instanceDto);
        } else {
            return Result.failureResult("不支持的 jobType: " + jobType);
        }
    }

    @RequestMapping("/flinkDriver/killJob")
    public Result<String> killJob(String instanceCode) {
        if (flinkDriverContext.getStatus() == DriverStatus.IDLE) {
            return Result.successMessageResult("jobserver idle status");
        }

        String currrentInstanceCode = InstanceContext.getInstanceCode();
        if (StringUtils.isNotBlank(currrentInstanceCode)
                && !currrentInstanceCode.equals(instanceCode)) {
            return Result.successMessageResult("current instanceCode: " + currrentInstanceCode);
        }

        LOG.info("prepare to kill job {}", instanceCode);
        LogUtils.warn("task {} was canceled", instanceCode);

        LogUtils.clearLog(instanceCode);
        LogUtils.sendTaskStatusFlag(TaskStatusFlag.TASK_STOP_FLAG);

        try {
            flinkDriverContext.setUserStopTask(true);

            JobType jobType = InstanceContext.getJobType();
            if (JobType.FLINK_BATCH_SQL == jobType || JobType.FLINK_STREAM_SQL == jobType) {
                flinkSqlTask.killJob(instanceCode);
            } else if (JobType.FLINK_BATCH_JAR == jobType || JobType.FLINK_STREAM_JAR == jobType) {
                flinkJarTask.killJob(instanceCode);
            }

            LOG.info("driver has been reset!");
            return Result.successMessageResult("driver has been reset!");
        } catch (Exception e) {
            LOG.error("reset driver error: " + e.getMessage());
            return Result.failureResult("reset driver error: " + e.getMessage());
        }
    }

    @RequestMapping("/flinkDriver/downloadYarnLog")
    public void downloadYanLog(HttpServletResponse response) {
        FileInputStream inputStream = null;
        BufferedOutputStream out = null;
        try {
            String localLogDir = System.getProperty("spark.yarn.app.container.log.dir");
            String filePath = localLogDir + "/stderr";
            File logFile = new File(filePath);
            if (logFile.exists()) {
                inputStream = new FileInputStream(logFile);

                response.reset();
                response.setContentType("application/x-download");
                response.addHeader("Content-Disposition", "attachment;filename=spark.log");
                response.addHeader("Content-Length", "" + logFile.length());
                out = new BufferedOutputStream(response.getOutputStream());

                IOUtils.copyLarge(inputStream, out);
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(out);
        }
    }

    @RequestMapping(value = "/gitInfo", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String gitInfo() throws IOException {
        ClassPathResource hadoopResource = new ClassPathResource("git.properties");
        return IOUtils.toString(hadoopResource.getInputStream(), Charset.defaultCharset());
    }
}
