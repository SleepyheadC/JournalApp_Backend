
package com.application.journalApp.config;

import com.application.journalApp.services.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
//    UserDetailsServiceImpl ek custom implementation hai userDetails Service ka jo ki database se load kr rha hai user ko for the authentication
    private UserDetailServiceImpl userDetailsService;

    @Bean
//    SecurityFilterChain hum logo ko ye bta rha hai ki application kaise secure bnega means it configures HTTP security settings
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(request -> request
                        .requestMatchers("/public/**").permitAll()

//                is request matcher ke andar waale url se jo bhi request match kr rhi hai un sbko authenticate kr do means username and password are required
//                in order to access these endpoints
                        .requestMatchers("/journal/**", "/user/**").authenticated()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())

//                enables basic authentication in which username and password are passed with the header of the request
                .httpBasic(Customizer.withDefaults())

//                disable cross site request forgery and it is not recommended in production applications
                .csrf(AbstractHttpConfigurer::disable)

//                finalize the filter chain with following settings
                .build();
    }

    @Autowired

//
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean

//  PasswordEncoder Bean:
//  Provides a BCryptPasswordEncoder instance for encoding passwords.
//  BCrypt adds a salt to the password and hashes it, improving security.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
