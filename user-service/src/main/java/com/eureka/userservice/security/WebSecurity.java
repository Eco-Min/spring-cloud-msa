package com.eureka.userservice.security;

import com.eureka.userservice.security.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurity {
    private final static IpAddressMatcher hasIpAddress = new IpAddressMatcher("127.0.0.1");

    private final LoginService loginService;
    private Environment env;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = getAuthenticationManager(http);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeRequests) ->
                        /*authorizeRequests
                                .requestMatchers("/users/**").permitAll()
                                .anyRequest().permitAll()*/
                        authorizeRequests
                                .requestMatchers("/**")
                                .access((authentication, object) ->
                                        new AuthorizationDecision(hasIpAddress.matches(object.getRequest())))

                )

                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .addFilter(getAuthenticationFilter(authenticationManager))
                .authenticationManager(authenticationManager);
//                .apply(new CustomSecurityFilterManager());

        return http.build();
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager);
        return authenticationFilter;
    }

    private AuthenticationManager getAuthenticationManager(HttpSecurity security) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = security.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(loginService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    /*public static class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new AuthenticationFilter(authenticationManager));
            super.configure(builder);
        }
    }*/
}
