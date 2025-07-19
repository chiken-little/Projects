package com.harsha.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PermissionsPolicyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpRes = (HttpServletResponse) response;
            httpRes.setHeader(
                    "Permissions-Policy",
                    "publickey-credentials-get=(self), publickey-credentials-create=(self)"
            );
        }

        chain.doFilter(request, response);
    }
}
