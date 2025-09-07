package org.baylist.util.extension;

import org.baylist.service.UserService;
import org.baylist.util.config.GetStatic;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.ModifierSupport;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotatedFields;

@Component
public class DefaultToken implements BeforeAllCallback, TestInstancePostProcessor, ParameterResolver {

	@Override
	public void beforeAll(ExtensionContext context) {
		Class<?> testClass = context.getRequiredTestClass();
		injectFields(testClass, null, ModifierSupport::isStatic);
	}

	private void injectFields(Class<?> testClass, Object testInstance,
	                          Predicate<Field> predicate) {

		predicate = predicate.and(field -> isString(field.getType()));
		findAnnotatedFields(testClass, FilToken.class, predicate)
				.forEach(field -> {
					try {
						field.setAccessible(true);
						field.set(testInstance, getToken());
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				});
	}

	private boolean isString(Class<?> type) {
		return String.class.isAssignableFrom(type);
	}

	private String getToken() {
		UserService userService = GetStatic.getBean(UserService.class);
		String token;

		var user = userService.getFil();
		token = "Bearer " + user.todoistToken();

		return token;
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.isAnnotated(FilToken.class) && isString(parameterContext.getParameter().getType());
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return getToken();
	}

	@Override
	public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
		Class<?> testClass = extensionContext.getRequiredTestClass();
		injectFields(testClass, testInstance, ModifierSupport::isNotStatic);
	}
}
