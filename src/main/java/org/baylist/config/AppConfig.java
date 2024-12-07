package org.baylist.config;

import org.baylist.util.log.RestLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestClient;

@EnableAspectJAutoProxy
@Configuration
public class AppConfig {


    @Bean
    public RestClient todoistRestClient(@Value("${todoist.baseUrl}") String baseUrl,
                                        @Value("${todoist.token}") String token) {
        //todo прикрутить логгирование ответов
        return RestClient
                .builder()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> {
                    h.add("Authorization", "Bearer " + token);
                    h.add("Content-Type", "application/json");
                })
                .requestInterceptor(new RestLog())
                .build();
    }


}
