package com.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class TenantDomainResolutionFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(TenantDomainResolutionFilter.class);

    public static final String HEADER_TENANT_DOMAIN = "X-Tenant-Domain";
    public static final String HEADER_TENANT_SUBDOMAIN = "X-Tenant-Subdomain";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. If client or upstream already provided X-Tenant-Subdomain, keep it
        String existingSubdomain = request.getHeaders().getFirst(HEADER_TENANT_SUBDOMAIN);
        if (existingSubdomain != null && !existingSubdomain.isBlank()) {
            return chain.filter(exchange);
        }

        // 2. Extract host from X-Forwarded-Host, Host, Origin, or Referer
        String hostCandidate = request.getHeaders().getFirst("X-Forwarded-Host");
        if (hostCandidate == null || hostCandidate.isBlank()) {
            hostCandidate = request.getHeaders().getFirst("Host");
        }
        if (hostCandidate == null || hostCandidate.isBlank()) {
            hostCandidate = request.getHeaders().getFirst("Origin");
        }
        if (hostCandidate == null || hostCandidate.isBlank()) {
            hostCandidate = request.getHeaders().getFirst("Referer");
        }
        if (hostCandidate == null || hostCandidate.isBlank()) {
            hostCandidate = request.getURI().getHost();
        }

        String domain = null;
        String subdomain = null;

        if (hostCandidate != null && !hostCandidate.isBlank()) {
            String cleanHost = hostCandidate.toLowerCase().trim();
            if (cleanHost.contains("://")) {
                cleanHost = cleanHost.substring(cleanHost.indexOf("://") + 3);
            }
            if (cleanHost.contains("/")) {
                cleanHost = cleanHost.substring(0, cleanHost.indexOf("/"));
            }
            if (cleanHost.contains(":")) {
                cleanHost = cleanHost.substring(0, cleanHost.indexOf(":"));
            }

            domain = cleanHost;

            // Extract subdomain:
            // "localhost" or "127.0.0.1" -> "localhost"
            // "hnt.ai" -> "hnt"
            // "acme.hrms.com" -> "acme"
            if (cleanHost.equalsIgnoreCase("localhost") || cleanHost.equals("127.0.0.1")) {
                subdomain = "localhost";
            } else if (cleanHost.matches("^\\d{1,3}(\\.\\d{1,3}){3}$")) {
                // Raw IP address like 172.20.1.57 - do NOT treat "172" as a subdomain
                subdomain = null;
            } else if (cleanHost.contains(".")) {
                subdomain = cleanHost.substring(0, cleanHost.indexOf('.'));
            } else {
                subdomain = cleanHost;
            }
        }

        ServerHttpRequest.Builder requestBuilder = request.mutate();
        if (domain != null) {
            requestBuilder.header(HEADER_TENANT_DOMAIN, domain);
        }
        if (subdomain != null) {
            log.info("Resolved domain [{}] -> subdomain [{}] from request header [{}]", domain, subdomain, hostCandidate);
            requestBuilder.header(HEADER_TENANT_SUBDOMAIN, subdomain);
        }

        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
