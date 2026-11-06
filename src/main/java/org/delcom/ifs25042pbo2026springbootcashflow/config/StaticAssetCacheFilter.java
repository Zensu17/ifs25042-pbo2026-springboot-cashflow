package org.delcom.ifs25042pbo2026springbootcashflow.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class StaticAssetCacheFilter extends OncePerRequestFilter {

    private static final String IMMUTABLE_CACHE = "public, max-age=31536000, immutable";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (isImmutableAsset(request.getRequestURI())) {
            filterChain.doFilter(request, new ImmutableCacheResponse(response));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isImmutableAsset(String path) {
        return path.startsWith("/VAADIN/build/") || path.startsWith("/aura/fonts/");
    }

    private static final class ImmutableCacheResponse extends HttpServletResponseWrapper {

        private ImmutableCacheResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
            super.setHeader(name, isCacheControl(name) ? IMMUTABLE_CACHE : value);
        }

        @Override
        public void addHeader(String name, String value) {
            if (isCacheControl(name)) {
                super.setHeader(name, IMMUTABLE_CACHE);
                return;
            }
            super.addHeader(name, value);
        }

        private boolean isCacheControl(String name) {
            return "Cache-Control".equalsIgnoreCase(name);
        }
    }
}