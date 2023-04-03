package org.baylist.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {


	@Override
	public void customize(TomcatServletWebServerFactory factory) {
		ErrorPage errorPage = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
		factory.addErrorPages(errorPage);
	}
}
