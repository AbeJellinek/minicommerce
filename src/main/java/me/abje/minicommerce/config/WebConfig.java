package me.abje.minicommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

public class WebConfig extends DelegatingWebMvcConfiguration {
    @Bean
    public DomainClassConverter<?> domainClassConverter() {
        return new DomainClassConverter<>(mvcConversionService());
    }
}
