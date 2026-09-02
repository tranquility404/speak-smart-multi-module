package com.tranquility.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogFilter extends OncePerRequestFilter {

    private final String REQUEST_ID = "Request-Id";
    private final ObjectMapper objectMapper;    // Spring auto-configures this. No need to add in BeanConfig

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();

        String requestID = UUID.randomUUID().toString();
        response.setHeader(REQUEST_ID, requestID);
        MDC.put(REQUEST_ID, requestID);

        log.info("Received {} {}", request.getMethod(), request.getRequestURI());
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String message = extractMessage(wrappedResponse);

            log.info("Completed {} {} with status {} in {}ms. Response: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    message
            );
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }

    private String extractMessage(ContentCachingResponseWrapper response) {
        try {
            byte[] content = response.getContentAsByteArray();

            if (content.length == 0) {
                return "";
            }

            JsonNode json = objectMapper.readTree(content);
            return json.path("message").asText("");

        } catch (Exception e) {
            return "";
        }
    }
}
