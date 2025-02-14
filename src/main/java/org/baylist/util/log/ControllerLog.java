package org.baylist.util.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ControllerLog implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, @NotNull Object handler) {
		log.info("in request: {} {}", request.getMethod(), request.getRequestURI());
		log.info("out response: {}", response.getStatus());
		return true;
	}

}
