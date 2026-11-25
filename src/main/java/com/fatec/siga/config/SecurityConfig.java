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
						.requestMatchers(
								"/login",
								"/css/**",
								"/js/**",
								"/images/**",
								"/webjars/**",
								"/favicon.ico",
								"/error").permitAll()
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
						.requestMatchers(
								"/api/cursos",
								"/api/cursos/**",
								"/api/disciplinas",
								"/api/disciplinas/**",
								"/api/turmas",
								"/api/turmas/**",
								"/api/enderecos",
								"/api/enderecos/**",
								"/h2-console",
								"/h2-console/**").hasRole("ADMIN")
						.requestMatchers("/professor", "/professor/**").hasRole("PROFESSOR")
						.requestMatchers("/aluno", "/aluno/**").hasRole("ALUNO")
						.requestMatchers("/", "/inicio").authenticated()
						.anyRequest().denyAll())
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
