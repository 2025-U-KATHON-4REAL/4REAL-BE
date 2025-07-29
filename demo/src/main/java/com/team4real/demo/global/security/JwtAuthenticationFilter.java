package com.team4real.demo.global.security;

import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String accessToken = authorizationHeader.substring(BEARER_PREFIX.length());
            try {
                jwtProvider.validateAccessToken(accessToken); // 엑세스 토큰 유효성을 검증
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication); // 인증된 사용자 정보를 SecurityContext에 저장
            } catch (CustomException e) {
                SecurityContextHolder.clearContext();
                if (e.getErrorCode().equals(ErrorCode.EXPIRED_ACCESS_TOKEN)) {
                    String refreshToken = cookieUtil.getCookieValue(request, "refreshToken");
                    if (refreshToken != null) {
                        try {
                            jwtProvider.validateRefreshToken(refreshToken);
                            ErrorResponseUtil.writeErrorResponse(response, e.getErrorCode(), request.getRequestURI());
                            return;
                        } catch (CustomException ex) {
                            ErrorResponseUtil.writeErrorResponse(response, ex.getErrorCode(), request.getRequestURI());
                            return;
                        }
                    } else {
                        ErrorResponseUtil.writeErrorResponse(response, ErrorCode.NO_COOKIE, request.getRequestURI());
                        return;
                    }
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}

