package com.team4real.demo.global.security;

import com.team4real.demo.domain.user.service.UserService;
import com.team4real.demo.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final CookieUtil cookieUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String accessToken = authHeader.substring(7);
                jwtProvider.validateAccessToken(accessToken);

                // Redis에서 refreshToken 삭제
                userService.deleteRefreshToken(authentication.getName());
            }
            // 쿠키 삭제
            cookieUtil.deleteCookie(response, "refreshToken");

            // 기본 로그아웃 핸들러 수행
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
        } else {
            ErrorResponseUtil.writeErrorResponse(response, ErrorCode.NOT_AUTHENTICATED, request.getRequestURI());
        }
    }
}
