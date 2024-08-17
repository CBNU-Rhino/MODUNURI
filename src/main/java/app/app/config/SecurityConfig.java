package app.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll() // 회원가입, 로그인 경로를 허용
                        .requestMatchers("/api/tourist-info").permitAll() // 관광지 정보 조회 경로를 허용
                        .requestMatchers("/api/tourist-info-by-name").permitAll() // 지역명으로 관광지 정보 조회 경로를 허용
                        .requestMatchers("/api/save-tourist-info").permitAll() // saveTouristInfo 경로를 허용
                        .requestMatchers("/api/tourist-info/**").permitAll() // 관광지 정보와 무장애 정보 조회 경로를 허용
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                );

        return http.build();
    }
}
