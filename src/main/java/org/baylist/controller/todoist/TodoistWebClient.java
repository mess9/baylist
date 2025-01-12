package org.baylist.controller.todoist;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;


@Slf4j
public class TodoistWebClient {

    private static final String PROJECT_METHOD = "projects";

    private final WebClient webClient;

    public TodoistWebClient(WebClient webClient) {
        this.webClient = webClient;
    }
    public <T> T get(Class<T> responseType) {
        try {
            return webClient.get()
                    .uri(UriComponentsBuilder
                            .fromPath("")
                            .pathSegment(PROJECT_METHOD)
                            .build()
                            .toUriString())
//                    .header("Authorization", "Bearer " + token) // Добавляем авторизацию
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(), // Проверка статуса
                            response -> {
                                // Логируем ошибку
                                return response.bodyToMono(String.class)
                                        .doOnNext(body -> log.error("Ошибка запроса: статус = {}, тело = {}",
                                                response.statusCode().value(), body))
                                        .flatMap(errorBody -> response.createException().flatMap(Mono::error));
                            }
                    )
                    .bodyToMono(responseType)
                    .doOnSubscribe(subscription -> log.info("Начат запрос: "))
                    .doOnSuccess(response -> log.info("Успешный ответ: {}", response))
                    .doOnError(e -> log.error("Ошибка: {}", e.getMessage(), e))
                    .block(); // Синхронное выполнение
        } catch (WebClientResponseException ex) {
            log.error("HTTP статус: {}, тело ответа: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new RuntimeException("Ошибка API: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Неизвестная ошибка", ex);
            throw new RuntimeException("Неизвестная ошибка", ex);
        }
    }

}
