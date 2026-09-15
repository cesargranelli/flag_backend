package br.com.flagplatform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Filtro de acesso que imprime duas linhas JSON por request HTTP no console:
 *
 * <pre>
 * {"type":"request","method":"POST","uri":"/api/v1/teams","remoteAddr":"127.0.0.1","body":"{...}"}
 * {"type":"response","method":"POST","uri":"/api/v1/teams","status":201,"durationMs":42,"body":"{...}"}
 * </pre>
 *
 * <p>Só loga endpoints sob {@code /api/v1/}. Body é truncado em 500 caracteres.
 * O traceId/spanId são injetados automaticamente no MDC pelo Micrometer Tracing (Brave)
 * e aparecem no padrão de console Spring Boot ({@code ${CONSOLE_LOG_PATTERN}}).</p>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter implements Filter {

    private static final String ACCESS_LOGGER = "http.access";
    private static final Set<String> LOGGED_PREFIXES = Set.of("/api/v1/");
    private static final int MAX_BODY_LENGTH = 500;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        if (LOGGED_PREFIXES.stream().noneMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(httpRequest, 64 * 1024);
        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper((HttpServletResponse) response);

        String method = httpRequest.getMethod();

        // --- REQUEST line ---
        String requestBody = extractBody(wrappedRequest.getContentAsByteArray());
        ObjectNode reqNode = MAPPER.createObjectNode();
        reqNode.put("type", "request");
        reqNode.put("method", method);
        reqNode.put("uri", path);
        reqNode.put("remoteAddr", httpRequest.getRemoteAddr());
        if (requestBody != null) {
            reqNode.put("body", truncate(requestBody));
        }
        logAtInfo(ACCESS_LOGGER, reqNode);

        // --- invoke downstream ---
        long start = System.nanoTime();
        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            // --- RESPONSE line ---
            String responseBody = extractBody(wrappedResponse.getContentAsByteArray());
            ObjectNode resNode = MAPPER.createObjectNode();
            resNode.put("type", "response");
            resNode.put("method", method);
            resNode.put("uri", path);
            resNode.put("status", wrappedResponse.getStatus());
            resNode.put("durationMs", elapsedMs);
            if (responseBody != null) {
                resNode.put("body", truncate(responseBody));
            }
            logAtInfo(ACCESS_LOGGER, resNode);

            wrappedResponse.copyBodyToResponse();
        }
    }

    /** Loga usando um logger dedicado para não poluir os logs da aplicação. */
    private void logAtInfo(String loggerName, ObjectNode node) {
        org.slf4j.LoggerFactory.getLogger(loggerName).info("{}", node.toString());
    }

    private String extractBody(byte[] content) {
        if (content.length == 0) return null;
        return new String(content, StandardCharsets.UTF_8);
    }

    private String truncate(String body) {
        if (body.length() <= MAX_BODY_LENGTH) return body;
        return body.substring(0, MAX_BODY_LENGTH) + "...[truncated]";
    }
}
