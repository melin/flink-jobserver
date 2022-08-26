package io.github.melin.flink.jobserver.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/img/**", "/error", "/metrics", "/jobserver.ico",
                        "/ok", "/profile", "/gitInfo", "/doc/**", "/driver/**").permitAll()
                .anyRequest()
                .authenticated();
    }
}
