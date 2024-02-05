package org.example.springcloudapigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilterConfig> {
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @SuppressWarnings("deprecation")
    @Override
    public GatewayFilter apply(final AuthFilterConfig config) {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();

            HttpHeaders headers = request.getHeaders();
            final boolean authorization = headers.containsKey("Authorization");
            if (!authorization)
                return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);

            String token = headers.get("Authorization").get(0);
            if (StringUtils.isEmpty(token))
                return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            boolean validateToken = jwtTokenProvider.validateToken(token);
            if (!validateToken)
                return this.onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(final ServerWebExchange exchange, final String err, final HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        String body = "Authentication failed: " + err;
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

}