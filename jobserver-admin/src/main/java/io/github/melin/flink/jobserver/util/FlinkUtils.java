package io.github.melin.flink.jobserver.util;

import com.gitee.melin.bee.util.MapperUtils;
import lombok.Data;
import org.apache.flink.configuration.*;
import org.apache.flink.configuration.description.BlockElement;
import org.apache.flink.configuration.description.TextElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.LinkedList;

public class FlinkUtils {

    private static final Logger logger = LoggerFactory.getLogger(FlinkUtils.class);

    private static LinkedList<Config> configs = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        /*parseConfig(JobManagerOptions.class, 1000);
        parseConfig(TaskManagerOptions.class, 950);
        parseConfig(CoreOptions.class, 940);
        parseConfig(ExecutionConfigOptions.class, 930);
        parseConfig(TableConfigOptions.class, 920);
        parseConfig(CoreOptions.class, 900);
        parseConfig(RestOptions.class, 850);
        parseConfig(YarnConfigOptions.class, 800);
        parseConfig(KubernetesConfigOptions.class, 750);
        parseConfig(RestartStrategyOptions.class, 700);
        parseConfig(CleanupOptions.class, 650);
        parseConfig(CheckpointingOptions.class, 640);
        parseConfig(HighAvailabilityOptions.class, 630);

        parseConfig(RocksDBNativeMetricOptions.class, 620);
        parseConfig(RocksDBOptions.class, 600);
        parseConfig(QueryableStateOptions.class, 550);
        parseConfig(ExecutionCheckpointingOptions.class, 550);
        parseConfig(PipelineOptions.class, 450);
        parseConfig(MetricOptions.class, 400);

        parseConfig(AkkaOptions.class, 350);
        parseConfig(SecurityOptions.class, 300);*/
        System.out.println(MapperUtils.toJSONString(configs, true));
    }

    public static void parseConfig(Class<?> clazz, Integer score) throws Exception {
        for (Field field : clazz.getFields()) {
            field.setAccessible(true);
            Class<?> fieldClass = field.getType();
            if (fieldClass.isAssignableFrom(ConfigOption.class)) {
                Config config = new Config();
                ConfigOption entry = (ConfigOption) field.get(null);
                config.setCaption(entry.key());
                if (entry.hasDefaultValue()) {
                    config.setMeta("default: " + entry.defaultValue());
                    config.setValue(entry.key() + " = " + entry.defaultValue());
                }
                if (entry.description().getBlocks().size() == 1) {
                    BlockElement element = entry.description().getBlocks().get(0);
                    if (element instanceof TextElement) {
                        config.setDocHTML(((TextElement) element).getFormat());
                    }
                }
                configs.add(config);
            }
        }
    }

    // {caption:"spark.sql.test", value: "spark.sql.test: test", score: 78, meta: "default: test"},
    @Data
    private static class Config {
        private String caption;

        private String value;

        private String score;

        private String meta;

        private String version;

        private String docHTML;
    }
}
