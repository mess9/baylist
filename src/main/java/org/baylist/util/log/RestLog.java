package org.baylist.util.log;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.baylist.config.AppConfig.RESPONSE_LOG;
import static org.baylist.util.convert.InputStreamConverter.inputStreamToString;
import static org.baylist.util.log.LogUtil.reduceEmptyLines;

@Slf4j
public class RestLog implements ClientHttpRequestInterceptor {


    public static String responseLog(ClientHttpResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<-- RESPONSE -->\n");
        sb.append("Status Code => ").append(response.getStatusCode().value()).append("\n");
        sb.append("HEADERS =>\n").append(parseResponseHeaders(response.getHeaders().toString()));
        String body = inputStreamToString(response.getBody());
        if (!body.isEmpty()) {
            sb.append("Response body => \n").append(body).append("\n");
        }
        return sb.toString();
    }

    private static String parseResponseHeaders(String headerString) {
        System.out.println(headerString);
        Map<String, String> headers = new HashMap<>();
        String[] pairs = headerString.split(", ");
        for (String pair : pairs) {
            String[] entry = pair.split(":");
            if (entry.length == 2) {
                headers.put(entry[0].trim(), entry[1].trim().replaceAll("^\"|\"$", ""));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    private static String parseRequestHeaders(String headerString) {
        String trimmed = headerString.substring(1, headerString.length() - 1);
        String[] parts = trimmed.split(", ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            String[] keyValue = part.split(":");
            String key = keyValue[0];
            String value = keyValue[1].replaceAll("^\"|\"$", "");
            sb.append(key).append(" - ").append(value).append("\n");
        }
        return sb.toString();
    }

    @NotNull
    @Override
    public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        responseToLog(responseWrapper);
        return responseWrapper;
    }

    public void responseToLog(ClientHttpResponse response) throws IOException {
        if (RESPONSE_LOG) {
            log.info(responseLog(response));
        }
    }

    private void logRequestDetails(HttpRequest request, byte[] body) {
        StringBuilder sb = new StringBuilder();
        sb.append("<-- REQUEST -->\n");
        sb.append(request.getMethod().name()).append(" ").append(request.getURI()).append("\n");
        sb.append("HEADERS =>\n").append(parseRequestHeaders(request.getHeaders().toString())).append("\n");
        String requestBody = new String(body, StandardCharsets.UTF_8);
        if (!requestBody.isEmpty()) {
            sb.append("Request Body =>\n").append(requestBody);
        }
        log.info(reduceEmptyLines(sb));
    }

}
