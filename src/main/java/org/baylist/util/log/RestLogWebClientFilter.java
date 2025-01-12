package org.baylist.util.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RestLogWebClientFilter implements ExchangeFilterFunction {

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        logRequestDetails(request);


        ClientRequest.Builder requestBuilder = ClientRequest.from(request);
        return next.exchange(requestBuilder.build());
//                .flatMap(response -> logResponseDetails(response));
    }

    private void logRequestDetails(ClientRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("<-- REQUEST -->\n")
                .append(request.method()).append(" ").append(request.url()).append("\n")
                .append("HEADERS =>\n");
        request.headers().forEach((key, values) ->
                values.forEach(value -> sb.append(key).append(" - ").append(value).append("\n"))
        );
        log.info(sb.toString());
    }

    @Override
    public ExchangeFilterFunction andThen(ExchangeFilterFunction afterFilter) {
        return ExchangeFilterFunction.super.andThen(afterFilter);
    }

    @Override
    public ExchangeFunction apply(ExchangeFunction exchange) {
        return ExchangeFilterFunction.super.apply(exchange);
    }
}
