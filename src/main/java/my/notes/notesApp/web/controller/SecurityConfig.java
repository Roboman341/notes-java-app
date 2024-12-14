package my.notes.notesApp.web.controller;

import my.notes.notesApp.biz.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomerService customerService;

    @Bean
    public PasswordEncoder passwordEncoder1() { // TODO: redo later
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customerService);
        provider.setPasswordEncoder(passwordEncoder1()); // Reusing the PasswordEncoder Bean
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()) // Consider enabling CSRF for better security
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test/student").hasAuthority("student")
                        .requestMatchers("/test/admin").hasAuthority("admin")
                        .anyRequest().permitAll() // Allow all other requests
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // Disable HTTP Basic if not needed
                .formLogin(form -> form.defaultSuccessUrl("/notes", true)); // Customize login behavior

        return httpSecurity.build();
    }
}
