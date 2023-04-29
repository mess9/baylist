package org.baylist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.baylist.db.repository")
public class MongoClient {

	// на случай подключения - если не резолвятся днс (см. application.yml)
	// способ лечения - https://stackoverflow.com/a/76076181
//	@Value("${spring.data.mongodb.uri}")
//	String mongoUri;
//	@Value("${spring.data.mongodb.username}")
//	String username;
//	@Value("${spring.data.mongodb.password}")
//	String password;
//
//
//	@Bean
//	public MongoClientSettingsBuilderCustomizer customizer() {
//		ConnectionString connection = new ConnectionString(uriBuilder());
//		return settings -> settings.applyConnectionString(connection);
//	}
//
//	private String uriBuilder(){
//		System.out.println(mongoUri);
//		System.out.println(username);
//		System.out.println(password);
//		return "mongodb://" + username + ":" + password + "@" + mongoUri.substring(10);
//	}

}
