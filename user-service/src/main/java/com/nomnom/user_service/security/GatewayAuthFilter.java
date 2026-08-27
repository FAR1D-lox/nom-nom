package com.nomnom.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class GatewayAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        log.info("GatewayAuthFilter: X-User-Id={}, X-User-Role={}", userId, role);

        if (role != null && !role.isBlank() && !"GUEST".equalsIgnoreCase(role)) {
            UserDetails user = User.builder()
                    .username(userId)
                    .password("")
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + role)))
                    .build();

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        }

        filterChain.doFilter(request, response);
    }
}
