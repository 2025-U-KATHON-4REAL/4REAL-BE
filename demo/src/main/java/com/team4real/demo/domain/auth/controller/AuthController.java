package com.team4real.demo.domain.auth.controller;

import com.team4real.demo.domain.auth.dto.*;
import com.team4real.demo.domain.auth.service.AuthService;
import com.team4real.demo.domain.auth.service.AuthUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthUserService authUserService;

    @Operation(summary = "이메일 중복 확인")
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmailAvailability(@RequestParam final String email) {
        authService.validateEmailAvailability(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<TokenResponseDto> signUp(@RequestBody @Valid AuthSignUpRequestDto requestDto) {
        return ResponseEntity.ok(authService.signUp(requestDto));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid AuthLoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    @Operation(summary = "액세스 토큰과 리프레시 토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(@RequestParam final String refreshToken) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }

    @Operation(summary = "내 정보 확인")
    @GetMapping("/me")
    public ResponseEntity<AuthMeResponseDto> getCurrentMe() {
        return ResponseEntity.ok(authUserService.getCurrentMe());
    }

    @Operation(summary = "내 프로필 확인")
    @GetMapping("/profile")
    public ResponseEntity<AuthProfileResponseDto> getCurrentProfile() {
        return ResponseEntity.ok(authUserService.getCurrentProfile());
    }
}
