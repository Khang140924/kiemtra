package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Báo cho Spring biết mình dùng chuẩn mã hóa BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/images/**").permitAll() // Ai cũng vào được
                .requestMatchers("/", "/home", "/courses").permitAll() // Câu 4: /courses cho tất cả
                .requestMatchers("/admin/**").hasAuthority("ADMIN") // Câu 4: /admin/** chỉ ADMIN
                .requestMatchers("/enroll/**").hasAuthority("STUDENT") // Câu 4: /enroll/** chỉ STUDENT
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Câu 5: Dùng trang login tự chế
                .defaultSuccessUrl("/home", true) // Câu 5: Đăng nhập thành công tới /home
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/home")
                .permitAll()
            );

        return http.build();
    }
}