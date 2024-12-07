package org.baylist.util.log;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RestLog implements ClientHttpRequestInterceptor {

    @NotNull
    @Override
    public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request, body);
        return execution.execute(request, body);
    }

    private void logRequestDetails(HttpRequest request, byte[] body) {
        log.info("URI: {}", request.getURI());
        log.info("HTTP Method: {}", request.getMethod());
        log.info("HTTP Headers: {}", request.getHeaders());
        log.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
    }

}
