package org.baylist.util.log;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RestLog implements ClientHttpRequestInterceptor, ClientHttpResponse {
    @Override
    public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request, body);
        return execution.execute(request, body);
    }

    private void logRequestDetails(HttpRequest request, byte[] body) {
        System.out.println("URI: " + request.getURI());
        System.out.println("HTTP Method: " + request.getMethod());
        System.out.println("HTTP Headers: " + request.getHeaders());
        System.out.println("Request Body: " + new String(body, StandardCharsets.UTF_8));
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return ;
    }

    @Override
    public String getStatusText() throws IOException {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getBody() throws IOException {
        return null;
    }

    @Override
    public HttpHeaders getHeaders() {
        return null;
    }
}
