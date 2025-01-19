package org.baylist.exception;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static feign.FeignException.errorStatus;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		log.error("Ошибка вызова API: метод={}, статус={}, причина={}",
				methodKey, response.status(), response.reason());

		HttpStatus status = HttpStatus.resolve(response.status());
		if (status != null) {
			return switch (status) {
				case BAD_REQUEST -> new TodoistApiException("Неверный запрос");
				case UNAUTHORIZED -> new TodoistApiException("Не авторизован");
				case NOT_FOUND -> new TodoistApiException("Ресурс не найден");
				default -> retry(methodKey, response);
			};
		}
		return new RuntimeException("Неизвестная ошибка");
	}


	public Exception retry(String methodKey, Response response) {
		FeignException exception = errorStatus(methodKey, response);
		int status = response.status();
		if (status >= 500) {
			return new RetryableException(
					response.status(),
					exception.getMessage(),
					response.request().httpMethod(),
					exception,
					50L, // The retry interval
					response.request());
		}
		return exception;
	}
}
