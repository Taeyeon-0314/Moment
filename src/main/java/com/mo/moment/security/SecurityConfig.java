package com.mo.moment.security;

import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.jwt.JwtAuthenticationFilter;
import com.mo.moment.jwt.exception.CustomAccessDeniedHandler;
import com.mo.moment.jwt.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthTokenProvider authTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs*/**", "/configuration/**", "/swagger*/**", "/webjars/**", "/**/favicon.ico", "/favicon.ico", "/error**");
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // HTTP Basic 인증 비활성화
                .httpBasic().disable()
                // CSRF보호 비활성화
                .csrf().disable()
                // CORS 활성화
                .cors().and()
                // 세션관리설정
                .sessionManagement()
                // 세션 생성안하고 JWT 토큰만을 이용하여 인증
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // HttpServletRequest에 따라 접근제한
                .authorizeRequests()
                // 여기있는 모든 경로는 인증 없이 접근이 가능
                .antMatchers("/login*/**", "/reissue**","/","/login/" , "/guest/host**").permitAll()
                // /exception/** GET요청은 인증없이 접근 가능
                .antMatchers(HttpMethod.GET, "/exception/**").permitAll()
                .antMatchers("/guest/image**").hasRole("GUEST")
                // 그 외의 모든 요청은 user역할을 가진 사용자만 접근 가능
                .anyRequest().hasRole("USER")
                .and()
                // 예외 처리 설정
                .exceptionHandling()
                // 인증되지 않은 사용자가 보호된 자원에 접근하려 할 때 실행할 entrypoint
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                // 접근이 거부된 사용자가 접근하려 할때 실행할 핸들러
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()
                // 필터를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(new JwtAuthenticationFilter(authTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용할 오리진(도메인)을 설정합니다.
        configuration.addAllowedOrigin("https://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("http://www.moment.r-e.kr");
        configuration.addAllowedOrigin("https://www.moment.r-e.kr");
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
