package io.github.melin.flink.jobserver.driver;

import com.beust.jcommander.JCommander;
import io.github.melin.flink.jobserver.core.entity.Cluster;
import io.github.melin.flink.jobserver.core.service.ClusterService;
import io.github.melin.flink.jobserver.driver.model.DriverParam;
import io.github.melin.flink.jobserver.driver.support.ConfigClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * huaixin 2022/4/6 7:05 PM
 */
@ImportResource(locations = {"classpath*:*-context.xml"})
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class, GsonAutoConfiguration.class,
        EmbeddedWebServerFactoryCustomizerAutoConfiguration.class})
@EnableScheduling
@EnableTransactionManagement
public class FlinkDriverServer extends SpringBootServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkDriverServer.class);

    public static void main(String[] args) throws Exception {
        DriverParam driverParam = new DriverParam();
        LOG.info("flink app args: {}", StringUtils.join(args, ","));
        JCommander.newBuilder().addObject(driverParam).build().parse(args);

        byte[] asBytes = Base64.getDecoder().decode(driverParam.getConfig());
        String configText = new String(asBytes, StandardCharsets.UTF_8);

        ApplicationContext applicationContext = SpringApplication.run(FlinkDriverServer.class);
        FlinkDriverContext flinkDriverContext = applicationContext.getBean(FlinkDriverContext.class);
        ClusterService clusterService = applicationContext.getBean(ClusterService.class);
        Cluster cluster = clusterService.getClusterByCode(driverParam.getClusterCode());

        Configuration flinkConf = new Configuration();
        ConfigClient.init(configText, flinkConf);
        FlinkDriverEnv.init(flinkConf, cluster, driverParam);

        flinkDriverContext.initFlinkDriver(driverParam);

        FlinkDriverEnv.waitDriver();
        LOG.info("application finished");
    }
}
