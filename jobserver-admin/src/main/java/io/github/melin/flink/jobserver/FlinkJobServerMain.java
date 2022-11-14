package io.github.melin.flink.jobserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * huaixin 2022/3/29 6:44 PM
 */
@ImportResource(locations = {"classpath*:jobserver-context.xml"})
@EnableScheduling
@EnableAspectJAutoProxy
@ServletComponentScan
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class,
        EmbeddedWebServerFactoryCustomizerAutoConfiguration.class})
public class FlinkJobServerMain extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }

    public static void main(String[] args) {
        SpringApplication.run(FlinkJobServerMain.class, args);
    }
}
