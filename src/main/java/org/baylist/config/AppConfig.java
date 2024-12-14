package org.baylist.config;

import org.baylist.util.log.RestLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestClient;

import javax.sql.DataSource;

@EnableAspectJAutoProxy
@Configuration
public class AppConfig {

    public static final boolean RESPONSE_LOG = false;
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    @Value("${spring.datasource.urlCloud}")
    private String datasourceUrlCloud;
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
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        System.out.println("коннект к базе данных");
        System.out.println(datasourceUrl);
        System.out.println(datasourceUrlCloud);
        String environment = System.getenv("ENVIRONMENT");
        if (environment != null && environment.equals("cloud")) {
            System.out.println("cloud");
            String googleConnect = "jdbc:postgresql://" +
                    System.getenv("INSTANCE_HOST") + ":" +
                    System.getenv("DB_PORT") + "/buylistdb";
            System.out.println(googleConnect);
            dataSource.setUrl(googleConnect);
        } else {
            System.out.println("local");
            dataSource.setUrl(datasourceUrl);
        }

        dataSource.setDriverClassName(datasourceDriverClassName);
        dataSource.setUsername(datasourceUsername);
        dataSource.setPassword(datasourcePassword);
        return dataSource;
    }

}



