package org.baylist.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.baylist.util.log.ControllerLog;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.baylist.util.convert.ToJson.getObjectMapper;

@EnableAspectJAutoProxy
@Configuration
@EnableCaching
@EnableScheduling
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    private final ControllerLog controllerLog;


    @Bean
    public ObjectMapper objectMapper() {
        return getObjectMapper();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(controllerLog);
    }


}



