package org.baylist.exception;

import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.baylist.util.log.RestLog.responseLog;

public class TodoistApiException extends RuntimeException {
    public TodoistApiException(ClientHttpResponse response) {
        super("Invalid response was received from Todoist: \n" + logResponse(response));
    }

    private static String logResponse(ClientHttpResponse response) {
        try {
            return responseLog(response);
        } catch (IOException e) {
            return "Failed to log response: " + e.getMessage();
        }
    }
}
