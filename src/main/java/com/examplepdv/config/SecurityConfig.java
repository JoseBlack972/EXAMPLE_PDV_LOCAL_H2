package com.examplepdv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isGestor = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
            if (isGestor) {
                response.sendRedirect("/gestao");
            } else {
                response.sendRedirect("/pdv");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**", "/admin/reset-app")
            )
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/manifest.json", "/sw.js", "/favicon.ico", "/h2-console/**").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/gestao/**", "/api/gestao/**").hasAnyRole("GESTOR", "ADMIN")
                .requestMatchers("/admin/**", "/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/produtos/importar-csv", "/produtos/modelo-csv").hasAnyRole("GESTOR", "ADMIN")
                .requestMatchers("/produtos/novo", "/produtos/editar/**", "/produtos/excluir/**").hasRole("ADMIN")
                .requestMatchers("/produtos", "/pdv/**", "/caixa/**", "/vendas/**", "/delivery/**", "/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/acesso-negado")
            );

        return http.build();
    }
}
