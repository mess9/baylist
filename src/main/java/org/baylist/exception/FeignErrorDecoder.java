package org.baylist.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

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
				default -> new TodoistApiException("Ошибка API: статус = " + status);
			};
		}
		return new RuntimeException("Неизвестная ошибка");
	}
}
