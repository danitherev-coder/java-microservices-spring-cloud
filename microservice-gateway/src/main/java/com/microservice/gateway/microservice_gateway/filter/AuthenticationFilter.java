package com.microservice.gateway.microservice_gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.gateway.microservice_gateway.exception.GatewayExceptionResponse;
import com.microservice.gateway.microservice_gateway.util.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.List;
import java.util.Objects;
import com.microservice.gateway.microservice_gateway.util.JwtServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            if (validator.isSecured.test(request)) {
                // Verificar si hay Authorization Header
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return handleError(exchange.getResponse(), 
                            HttpStatus.UNAUTHORIZED, 
                            "Falta el encabezado de autorización", 
                            request.getPath().value());
                }

                // Extraer el token del header
                String authHeaders = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
                if (authHeaders == null || !authHeaders.startsWith("Bearer ")) {
                    return handleError(exchange.getResponse(), 
                            HttpStatus.UNAUTHORIZED, 
                            "El formato del token es inválido. Debe ser 'Bearer [token]'", 
                            request.getPath().value());
                }
                
                String token = authHeaders.substring(7);

                try {
                    // Validar el token
                    jwtService.validateToken(token);
                    // Extraer roles y adjuntarlos como header para downstream services
                    byte[] keyBytes = Decoders.BASE64.decode(JwtServiceImpl.SECRET);
                    Key signingKey = Keys.hmacShaKeyFor(keyBytes);
                    var claims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
                    List<String> roles = claims.get("roles", List.class);
                    if (roles != null && !roles.isEmpty()) {
                        String csv = String.join(",", roles);
                        ServerHttpRequest newReq = request.mutate().header("X-User-Roles", csv).build();
                        return chain.filter(exchange.mutate().request(newReq).build());
                    }
                } catch (ExpiredJwtException e) {
                    log.error("Token expirado: {}", e.getMessage());
                    return handleError(exchange.getResponse(), 
                            HttpStatus.UNAUTHORIZED, 
                            "El token ha expirado", 
                            request.getPath().value());
                } catch (MalformedJwtException | SignatureException e) {
                    log.error("Token inválido: {}", e.getMessage());
                    return handleError(exchange.getResponse(), 
                            HttpStatus.UNAUTHORIZED, 
                            "El token es inválido", 
                            request.getPath().value());
                } catch (Exception e) {
                    log.error("Error al validar el token: {}", e.getMessage());
                    return handleError(exchange.getResponse(), 
                            HttpStatus.UNAUTHORIZED, 
                            "Error de autenticación: " + e.getMessage(), 
                            request.getPath().value());
                }
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> handleError(ServerHttpResponse response, HttpStatus status, String message, String path) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        GatewayExceptionResponse errorResponse = GatewayExceptionResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error al serializar la respuesta de error: {}", e.getMessage());
            byte[] bytes = ("Error de autenticación: " + message).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
    }

    public static class Config {
    }
}
