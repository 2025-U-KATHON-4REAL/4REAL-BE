package com.team4real.demo.global.config;

import com.team4real.demo.global.security.CookieUtil;
import com.team4real.demo.global.security.CustomLogoutHandler;
import com.team4real.demo.global.security.JwtAuthenticationFilter;
import com.team4real.demo.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private final CustomLogoutHandler customLogoutHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, cookieUtil), LogoutFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/favicon.ico",
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/auth/**", "/public/**").permitAll()
                        .anyRequest().permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout") // POST 요청
                        .deleteCookies("refreshToken")
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, cookieUtil), LogoutFilter.class)
        ;
        return http.build();
    }
}