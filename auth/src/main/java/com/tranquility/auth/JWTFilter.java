package com.tranquility.auth;

import com.tranquility.auth.model.AuthenticatedUser;
import com.tranquility.auth.service.AuthenticatedUserService;
import com.tranquility.common.auth.AuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final AuthenticatedUserService authenticatedUserService;
    private final JWTUtil jwtUtil;

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) { // Should I try to AUTHENTICATE you?
        String uri = request.getRequestURI();

        return uri.startsWith("/swagger-ui/")
                || uri.equals("/swagger-ui.html")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/auth/")
                || uri.startsWith("/login/oauth2/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("Unauthenticated request!");
            chain.doFilter(request, response);
            return;
        }
        String jwt = authorizationHeader.substring(7);

        try {
            String username = jwtUtil.extractUsername(jwt);
            log.info("Authenticating user {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                AuthenticatedUser user = authenticatedUserService.loadUserByUsername(username);
                log.info("Authentication Successful! Request moves forward for {}", username);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            resolver.resolveException(request, response, null, new AuthException("Token expired. Please log in again."));
            return;
        } catch (JwtException e) {
            log.error("Invalid Token: {}", e.getMessage());
            resolver.resolveException(request, response, null, new AuthException("Invalid Token."));
            return;
        }

        chain.doFilter(request, response);
    }
}
