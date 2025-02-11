package org.baylist.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import oracle.jdbc.pool.OracleDataSource;
import org.baylist.util.log.ControllerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

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

    @Autowired
    private ControllerLog controllerLog;


    @Bean
    public DataSource dataSource() throws SQLException { //oracle
        OracleDataSource dataSource = new OracleDataSource();
        Properties properties = new Properties();
        properties.put("user", datasourceUsername);
        properties.put("password", datasourcePassword);
        dataSource.setConnectionProperties(properties);
        dataSource.setURL(datasourceUrl);
        return dataSource;
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



