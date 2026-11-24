package com.fatec.siga.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()
						.requestMatchers("/h2-console/**").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/cursos/**").hasRole("ADMIN")
						.requestMatchers("/api/disciplinas/**").hasRole("ADMIN")
						.requestMatchers("/api/turmas/**").hasRole("ADMIN")
						.requestMatchers("/professor/**").hasRole("PROFESSOR")
						.requestMatchers("/aluno/**").hasRole("ALUNO")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/", true)
						.failureUrl("/login?error")
						.permitAll())
				.logout(logout -> logout
						.logoutSuccessUrl("/login?logout")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll())
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
				.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
