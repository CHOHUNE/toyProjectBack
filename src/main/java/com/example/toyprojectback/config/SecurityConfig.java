package com.example.toyprojectback.config;

import com.example.toyprojectback.repository.UserRepository;
import com.example.toyprojectback.security.MyAccessDeniedHandler;
import com.example.toyprojectback.security.MyLoginSuccessHandler;
import com.example.toyprojectback.security.MyLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    //로그인 하지 않은 유저들만 접근 가능한 URL
    private static final String[] anonymousUrl = {"/users/join", "/users/login"};

    private static final String[] authenticationUseUrl = {"/boards/**/**/edit", "/boards/**/**/delete", "/users/myPage/**", "/users/edit", "/users/delete"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeRequests((auth) ->
                        auth
                                .requestMatchers(anonymousUrl).permitAll()
                                .requestMatchers(authenticationUseUrl).authenticated()
                                .requestMatchers("/boards/greeting/write").hasAnyAuthority("BRONZE", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/boards/greeting").hasAnyAuthority("BRONZE", "ADMIN")
                                .requestMatchers("/board/free/write").hasAnyAuthority("SILVER", "GOLD", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/board/free").hasAnyAuthority("SILVER", "GOLD", "ADMIN")
                                .requestMatchers("/boards/gold/**").hasAnyAuthority("ADMIN", "GOLD")
                                .requestMatchers("/users/admin/**").hasAuthority("ADMIN")
                                .requestMatchers("/comments/**").hasAnyAuthority("BRONZE", "SILVER", "GOLD", "ADMIN")
                                .anyRequest().permitAll()


                );

        httpSecurity.exceptionHandling((auth) -> auth
                .accessDeniedHandler(new MyAccessDeniedHandler(userRepository))
                .authenticationEntryPoint(new MyAuthenticationEntryPoint()));


        httpSecurity.formLogin((auth) -> auth
                .loginPage("/users/login")
                .usernameParameter("loginId")
                .passwordParameter("password")
                .failureUrl("/users/login?fail") // corrected method name
                .successHandler(new MyLoginSuccessHandler(userRepository)));


        httpSecurity.logout((auth) -> auth
                .logoutUrl("/users/logout")
                .invalidateHttpSession(true) // corrected method name
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(new MyLogoutSuccessHandler()));



        return httpSecurity.build();
}
}