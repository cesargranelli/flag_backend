//package br.com.flagplatform.security;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpMethod;
//import org.springframework.stereotype.Component;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//import org.springframework.web.util.ContentCachingResponseWrapper;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Set;
//
///**
// * Filtro de logging estruturado para requisições e respostas HTTP.
// * <p>
// * Só loga endpoints sob {@code /api/v1/} e apenas quando o nível
// * INFO está habilitado. O traceId/spanId são propagados pelo MDC
// * via Micrometer Tracing (Brave/Braave).
// * <p>
// * Usam um logger dedicado ({@code RequestResponseLoggingFilter}) que
// * escreve JSON estruturado via LogstashEncoder, separado do console
// * padrão Spring Boot. Body é truncado para 500 chars.
// */
//@Slf4j
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class RequestResponseLoggingFilter implements Filter {
//
//    private static final Set<String> LOGGED_PATHS = Set.of("/api/v1/");
//    private static final int MAX_BODY_LENGTH = 500;
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String path = httpRequest.getRequestURI();
//
//        boolean shouldLog = LOGGED_PATHS.stream().anyMatch(path::startsWith);
//        if (!shouldLog) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        ContentCachingRequestWrapper wrappedRequest =
//                new ContentCachingRequestWrapper(httpRequest, 64 * 1024);
//        ContentCachingResponseWrapper wrappedResponse =
//                new ContentCachingResponseWrapper((HttpServletResponse) response);
//
//        String method = httpRequest.getMethod();
//
//        // Log request (separate line)
//        String requestBody = extractRequestBody(wrappedRequest, method);
//        String truncatedBody = formatBody(requestBody);
//        MDC.put("request_body", truncatedBody != null ? truncatedBody : "");
//        log.info("request.start method={} uri={} remote_addr={}",
//                method, path, httpRequest.getRemoteAddr());
//        MDC.remove("request_body");
//
//        long start = System.nanoTime();
//        try {
//            chain.doFilter(wrappedRequest, wrappedResponse);
//        } finally {
//            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
//            int status = wrappedResponse.getStatus();
//            String responseBody = extractResponseBody(wrappedResponse);
//            String truncatedRespBody = formatBody(responseBody);
//            MDC.put("response_body", truncatedRespBody != null ? truncatedRespBody : "");
//            log.info("request.end method={} uri={} status={} duration_ms={}",
//                    method, path, status, elapsedMs);
//            MDC.remove("response_body");
//
//            wrappedResponse.copyBodyToResponse();
//        }
//    }
//
//    /**
//     * Trunca o body para no máximo MAX_BODY_LENGTH caracteres, evitando
//     * logs excessivamente longos com payloads grandes (ex: uploads, JSONs aninhados).
//     */
//    private String formatBody(String body) {
//        if (body == null) return null;
//        if (body.length() <= MAX_BODY_LENGTH) return body;
//        return body.substring(0, MAX_BODY_LENGTH) + "...[truncated " + (body.length() - MAX_BODY_LENGTH) + " chars]";
//    }
//
//    private String extractRequestBody(ContentCachingRequestWrapper request, String method) {
//        if (HttpMethod.GET.matches(method) || HttpMethod.DELETE.matches(method)) {
//            return null;
//        }
//        try {
//            byte[] content = request.getContentAsByteArray();
//            if (content.length == 0) return null;
//            return new String(content, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            log.warn("request.body.read_error reason={}", e.getMessage());
//            return null;
//        }
//    }
//
//    private String extractResponseBody(ContentCachingResponseWrapper response) {
//        try {
//            byte[] content = response.getContentAsByteArray();
//            if (content.length == 0) return null;
//            return new String(content, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}