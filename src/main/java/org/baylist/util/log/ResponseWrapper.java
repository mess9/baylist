package org.baylist.util.log;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ResponseWrapper implements ClientHttpResponse {
    private final ClientHttpResponse response;
    private final byte[] body;

    public ResponseWrapper(ClientHttpResponse response) throws IOException {
        this.response = response;
        this.body = toByteArray(response.getBody());
    }

    @NotNull
    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(body);
    }

    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    @NotNull
    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return response.getStatusCode();
    }


    @NotNull
    @Override
    public String getStatusText() throws IOException {
        return response.getStatusText();
    }

    @Override
    public void close() {
        response.close();
    }

    @NotNull
    @Override
    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }
}