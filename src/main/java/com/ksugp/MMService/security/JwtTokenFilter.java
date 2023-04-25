package com.ksugp.MMService.security;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class JwtTokenFilter extends GenericFilterBean {
    @Autowired//не было в видео
    private final JwtTokenProvider jwtTokenProvider;
    @Value("${jwt.cookieName}")
    private String cookieName;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        try {
            try {
                if (token != null && jwtTokenProvider.validateToken(token)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    if (authentication != null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }catch(UsernameNotFoundException exception){
                Cookie cookie = new Cookie(cookieName, null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                ((HttpServletResponse)response).addCookie(cookie);
                throw new UsernameNotFoundException("User not found. Token was deleted");
            }
        }catch(JwtAuthenticationException e){
            Cookie cookie = new Cookie(cookieName, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            ((HttpServletResponse)response).addCookie(cookie);

            SecurityContextHolder.clearContext();
            ((HttpServletResponse)response).sendError(e.getHttpStatus().value());
            throw new JwtAuthenticationException("JWT token is expired or invalid =)");
        }
        chain.doFilter(request,response);
    }
}
