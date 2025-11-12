package ru.itis.expensetracker.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class ExceptionFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Unhandled exception in filter chain for URI: {}", request.getRequestURI(), e);
            
            if (!response.isCommitted()) {
                request.setAttribute("javax.servlet.error.exception", e);
                request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
                request.getRequestDispatcher("/WEB-INF/jsp/error/500.jsp").forward(request, response);
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

}

