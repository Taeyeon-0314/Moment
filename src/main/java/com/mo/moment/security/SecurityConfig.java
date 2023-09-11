package com.mo.moment.security;

import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private  final AuthTokenProvider authTokenprovider;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**");
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authTokenprovider);

        http
                // HTTP OPTIONS 매서드 요청은 모두 허용(cors 구성을 위해)
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                // 경로에 대한 요청은 모두 허용
                .antMatchers("/login/**").permitAll()
                // 다른 모든 요청은 인증을 필요로 함
                .anyRequest().authenticated().and()
                // CORS 구성 활성화
                .cors().and()
                .headers()
                .frameOptions()
                // X-Frame-Options를 같은 출처에서만 허용
                .sameOrigin().and()
                // CSRF(Cross-Site Request Forgery) 보안 비활성화
                .csrf().disable()
                .sessionManagement()
                // 세션 관리를 STATELESS로 설정 (JWT 기반 인증)
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//                .and()
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용할 오리진(도메인)을 설정합니다.
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:8080");
        // 허용할 HTTP 메서드를 설정합니다.
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("OPTIONS");
        // 허용할 HTTP 헤더를 설정합니다.
        configuration.addAllowedHeader("*");
        // 자격 증명(cookie, HTTP 인증 등) 사용 허용 여부를 설정합니다.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
