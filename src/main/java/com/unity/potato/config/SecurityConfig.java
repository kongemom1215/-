package com.unity.potato.config;

import com.unity.potato.domain.login.LoginTryHistory;
import com.unity.potato.domain.login.LoginTryHistoryRepository;
import com.unity.potato.domain.login.LoginTryStatus;
import com.unity.potato.domain.url.AuthUrlRepository;
import com.unity.potato.service.user.UserService;
import com.unity.potato.util.RedisUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.unity.potato.util.constants.LoginConstants.PREIFIX_LOGIN_TRY;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final UserService userService;
    private final DataSource dataSource;
    private final RedisUtil redisUtil;
    @Autowired
    private LoginTryHistoryRepository loginTryHistoryRepository;
    @Autowired
    private AuthUrlRepository authUrlRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{


        http
                .formLogin((form) -> form
                        .loginPage("/community/login")
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler()))
                .logout((logout) -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessUrl("/community/main")
                        .deleteCookies("JSESSIONID", "remember-me")
                        .invalidateHttpSession(true))
                .rememberMe((remember) -> remember
                        .userDetailsService(userService)
                        .tokenRepository(tokenRepository()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(authUrls()).authenticated()
                        .requestMatchers(request -> request.getRequestURI().startsWith("/community/login")).anonymous()
                        .anyRequest().permitAll()
                )
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler()));

        return http.build();
    }

    @Bean
    public RequestMatcher authUrls(){
        List<String> authUrlList = authUrlRepository.findAllUrl();

        return new OrRequestMatcher(
                authUrlList.stream()
                    .map(AntPathRequestMatcher::new)
                    .collect(Collectors.toList())
        );
    }


    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                try {
                    String userEmail = request.getParameter("email");
                    String remoteIp = request.getHeader("X-Forwarded-For");
                    if (remoteIp == null || remoteIp.isEmpty()) {
                        remoteIp = request.getRemoteAddr();
                    }

                    String loginFailCnt = redisUtil.getValue(PREIFIX_LOGIN_TRY + userEmail);
                    if(redisUtil.getValue(PREIFIX_LOGIN_TRY + userEmail) != null){
                        long failCnt = Long.parseLong(loginFailCnt);
                        if(failCnt >= 5){
                            new SecurityContextLogoutHandler().logout(request, response, null);
                            response.sendRedirect("/community/login?errorCode=7000");
                            return;
                        } else {
                            redisUtil.delete(PREIFIX_LOGIN_TRY + userEmail);
                        }
                    }

                    LoginTryHistory history = LoginTryHistory.builder()
                            .userEmail(userEmail)
                            .loginTryIp(remoteIp)
                            .loginTryStatus(LoginTryStatus.SUCCESS)
                            .loginTryDt(LocalDateTime.now())
                            .build();

                    loginTryHistoryRepository.save(history);
                }catch (Exception e){
                }

                response.sendRedirect("/community/main");
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                String userEmail = request.getParameter("email"); // 로그인 시도한 이메일 가져오기
                String remoteIp = request.getHeader("X-Forwarded-For");
                if (remoteIp == null || remoteIp.isEmpty()) {
                    remoteIp = request.getRemoteAddr();
                }

                LoginTryHistory history = LoginTryHistory.builder()
                        .userEmail(userEmail)
                        .loginTryIp(remoteIp)
                        .loginTryStatus(LoginTryStatus.FAIL)
                        .loginTryDt(LocalDateTime.now())
                        .build();
                loginTryHistoryRepository.save(history);

                String errorCode;

                if(exception instanceof BadCredentialsException || exception instanceof UsernameNotFoundException){
                    String key = PREIFIX_LOGIN_TRY + userEmail;
                    Long tryCnt = redisUtil.increment(key);
                    redisUtil.add(key, tryCnt.toString());
                    if(tryCnt >= 5){
                        errorCode = "7000";
                    } else {
                        errorCode="7010";
                    }
                } else {
                    errorCode="9999";
                }

                response.sendRedirect("/community/login?errorCode="+errorCode);
            }
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                response.sendRedirect("/community/main");
            }
        };
    }


    @Bean
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

}
