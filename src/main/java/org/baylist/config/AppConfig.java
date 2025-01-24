package org.baylist.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.baylist.util.log.ControllerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

import static org.baylist.util.convert.ToJson.getObjectMapper;

@EnableAspectJAutoProxy
@Configuration
@EnableCaching
@EnableScheduling
public class AppConfig implements WebMvcConfigurer {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriverClassName;

    @Autowired
    private ControllerLog controllerLog;

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

    @Bean
    public ObjectMapper objectMapper() {
        return getObjectMapper();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(controllerLog);
    }


}



