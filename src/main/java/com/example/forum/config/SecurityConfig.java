package com.example.forum.config;

import com.example.forum.repository.UserRepository;
import com.example.forum.security.ForumUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Locale;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/posts/new").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/", "/posts/*", "/login", "/register",
                                "/css/**", "/error", "/error/**", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByUsername(username.trim().toLowerCase(Locale.ROOT))
                .map(ForumUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("No account for " + username));
    }
}
