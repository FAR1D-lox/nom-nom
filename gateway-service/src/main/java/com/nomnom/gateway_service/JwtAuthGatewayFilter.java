package com.nomnom.gateway_service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthGatewayFilter implements GlobalFilter, Ordered {

    private final GatewayJwtService jwtService;

    private static final List<String> PUBLIC = List.of(
            "auth/register", "auth/login"
    );

    private static final List<String> GUEST = List.of(
            "recipes", "search", "ingredients"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1) публичные отправляем в auth сервис
        if (PUBLIC.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");

        // 2) нет токена - либо гость, либо 401
        if (auth == null || !auth.startsWith("Bearer ")) {
            if (GUEST.stream().anyMatch(path::startsWith)) {
                return chain.filter(withHeaders(exchange, null, "GUEST"));
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 3) есть токен - проверка
        String token = auth.substring(7);
        if (!jwtService.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String userId = jwtService.extractUserId(token);
        String role = jwtService.extractUserRole(token);
        return chain.filter(withHeaders(exchange, userId, role));
    }

    private ServerWebExchange withHeaders(
            ServerWebExchange ex,
            String userId,
            String role)
    {
        ServerHttpRequest req = ex.getRequest().mutate()
                .headers(h -> {
                    h.remove("X-User-Id");
                    h.remove("X-User-Role");
                })
                .header("X-User-Role", role)
                .header("X-User-Id", userId != null ? userId : "")
                .build();
        return ex.mutate().request(req).build();
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
