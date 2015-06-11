package me.abje.minicommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;

@SpringBootApplication
@EnableSpringDataWebSupport
@EnableScheduling
@Configuration
public class MinicommerceApplication {
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    public static void main(String[] args) {
        SpringApplication.run(MinicommerceApplication.class, args);
    }
}
