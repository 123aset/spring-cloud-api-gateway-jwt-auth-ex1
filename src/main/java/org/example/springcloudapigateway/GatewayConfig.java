package org.example.springcloudapigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Autowired
    private AuthFilter authFilter;
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("whitelist_service_1", r -> r.path("/register-legalservice/whitelist")
                        .uri("http://localhost:8070/register-legalservice/whitelist"))
                .route("register-legalservice", r -> r.path("/register-legalservice/**")
                        .filters(f -> f.filter(
                                authFilter.apply(new AuthFilterConfig()))
                        )
                        .uri("http://localhost:8070/register-legalservice")
                ).route("register-legalservice", r -> r.path("/ip-registration/**")
                        .filters(f -> f.filter(
                                authFilter.apply(new AuthFilterConfig()))
                        )
                        .uri("http://localhost:8070/ip-registration")
                )
                .route("first_service_route", r -> r.path("/test/hello")
                        .filters(f -> f.modifyResponseBody(String.class, String.class,
                                        (exchange, originalResponse) -> {
                                            if (exchange.getResponse().getStatusCode().value() == HttpStatus.OK.value()) {
                                                exchange.getAttributes().put("useSecondService", true);
                                                return Mono.just(originalResponse);
                                            }
                                            return Mono.just(originalResponse);
                                        }
                                )
                        )
                        .uri("http://localhost:8070/auth/login")
                )
                .route("second_service_route", r -> r.path("/test2/hello")
                        .and().predicate((exchange) -> {
                            Object useSecondService = exchange.getAttribute("useSecondService");
                            return useSecondService != null && (Boolean) useSecondService;
                        })
                        .uri("http://localhost:8060/test2/hello")
                )
                .build();
    }
}