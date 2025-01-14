package org.baylist.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.apache.http.protocol.HTTP;
import org.baylist.exception.FeignErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Configuration
@EnableFeignClients(basePackages = "org.baylist.controller.todoist", defaultConfiguration = FeignClientConfig.class)
public class FeignClientConfig {

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> requestTemplate.header(HTTP.CONTENT_TYPE, APPLICATION_JSON_VALUE);
	}

	@Bean
	public ErrorDecoder errorDecoder() {
		return new FeignErrorDecoder();
	}


}
