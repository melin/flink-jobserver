package io.github.melin.flink.jobserver.driver;

import com.beust.jcommander.JCommander;
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
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class, GsonAutoConfiguration.class})
@EnableScheduling
@EnableTransactionManagement
public class FlinkDriverApp extends SpringBootServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkDriverApp.class);

    public static void main(String[] args) throws Exception {
        DriverParam driverParam = new DriverParam();
        LOG.info("args: {}", StringUtils.join(args, ","));
        JCommander.newBuilder().addObject(driverParam).build().parse(args);

        Long driverId = driverParam.getDriverId();
        byte[] asBytes = Base64.getDecoder().decode(driverParam.getConfig());
        String configText = new String(asBytes, StandardCharsets.UTF_8);

        ApplicationContext applicationContext = SpringApplication.run(FlinkDriverApp.class);
        FlinkDriverContext sparkDriverContext = applicationContext.getBean(FlinkDriverContext.class);

        boolean kerberosEnabled = driverParam.isKerberosEnabled();
        boolean hiveEnabled = driverParam.isHiveEnable();
        Configuration flinkConf = new Configuration();
        ConfigClient.init(configText, flinkConf);
        FlinkEnv.init(flinkConf, kerberosEnabled, hiveEnabled);

        sparkDriverContext.initSparkDriver(driverId);

        FlinkEnv.waitDriver();
        LOG.info("application finished");
    }
}
