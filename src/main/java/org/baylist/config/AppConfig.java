package org.baylist.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.baylist.util.log.RestLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.baylist.util.convert.ToJson.getObjectMapper;

@EnableAspectJAutoProxy
@Configuration
public class AppConfig {

    public static final boolean RESPONSE_LOG = false;
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriverClassName;


    @Bean //todo добавить реализацию на RestTemplate
    public RestClient todoistRestClient(@Value("${todoist.baseUrl}") String baseUrl,
                                        @Value("${todoist.token}") String token) {
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

    @Bean
    public RestTemplate todoostRestTemplate(@Value("${todoist.baseUrl}") String baseUrl,
                                            @Value("${todoist.token}") String token) {

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl + "/"));

        ClientHttpRequestInterceptor headerInterceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + token);
//            request.getHeaders().add("Content-Type", "application/json");
            request.getHeaders().setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Arrays.asList(headerInterceptor, new RestLog()));

        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(getObjectMapper());
        messageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        restTemplate.getMessageConverters().add(0,messageConverter);

        return restTemplate;
    }

    @Bean
    public WebClient todoistWebClient(@Value("${todoist.baseUrl}") String baseUrl,
                                      @Value("${todoist.token}") String token) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add("Authorization", "Bearer " + token);
                    httpHeaders.add("Content-Type", "application/json");
                })
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return getObjectMapper();
    }

    @Bean
    public DataSource dataSource() {
        String environment = System.getenv("ENVIRONMENT");
        if (environment != null && environment.equals("cloud")) {
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl("jdbc:postgresql:///buylistdb");
            config.setUsername(datasourceUsername);
            config.setPassword(datasourcePassword);
            config.setDriverClassName(datasourceDriverClassName);
            config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory");
            config.addDataSourceProperty("cloudSqlInstance", System.getenv("INSTANCE_CONNECTION_NAME"));
            config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");

            return new HikariDataSource(config);
        } else {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();

            dataSource.setUrl(datasourceUrl);
            dataSource.setDriverClassName(datasourceDriverClassName);
            dataSource.setUsername(datasourceUsername);
            dataSource.setPassword(datasourcePassword);

            return dataSource;
        }
    }
}



