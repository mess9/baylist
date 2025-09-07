package org.baylist.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeans implements ApplicationContextAware {

	private static ApplicationContext ctx;

	@Override
	public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}

	public static <T> T getBean(Class<T> type) {
		return ctx.getBean(type);
	}
}
