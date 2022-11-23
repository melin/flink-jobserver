package io.github.melin.flink.jobserver.driver.task;

import io.github.melin.flink.jobserver.core.dto.InstanceDto;
import io.github.melin.flink.jobserver.core.util.CommonUtils;
import io.github.melin.flink.jobserver.driver.FlinkDriverEnv;
import io.github.melin.flink.jobserver.driver.util.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.api.internal.TableResultImpl;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.utils.print.PrintStyle;
import org.apache.flink.table.utils.print.TableauStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

@Service
public class FlinkSqlTask extends AbstractFlinkTask {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkSqlTask.class);

    @Override
    protected void executeJobText(InstanceDto instanceDto) throws Exception {
        String noCommentJobText = CommonUtils.cleanSqlComment(instanceDto.getJobText());
        List<String> sqls = CommonUtils.splitMultiSql(noCommentJobText);

        for (String row : sqls) {
            String sql = StringUtils.trim(row);
            if (StringUtils.isNotBlank(sql)) {
                LogUtils.stdout("execute sql: " + sql);
                if (StringUtils.startsWithIgnoreCase(sql, "select")) {
                    TableResultImpl tableResult = (TableResultImpl) FlinkDriverEnv.getTableEnvironment().executeSql(sql);
                    Iterator<RowData> it = tableResult.collectInternal();
                    TableauStyle printStyle = PrintStyle.tableauWithDataInferredColumnWidths(
                            tableResult.getResolvedSchema(), tableResult.getRowDataToStringConverter());
                    StringWriter stringWriter = new StringWriter();
                    printStyle.print(it, new PrintWriter(stringWriter));
                    LogUtils.stdout("query result:\n" + stringWriter);
                } else {
                    FlinkDriverEnv.getTableEnvironment().executeSql(sql);
                }
            }
        }
    }
}
