package com.epam.gym.trainer_hours_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.epam.gym.trainer_hours_service.security.JwtAuthenticationEntryPoint;
import com.epam.gym.trainer_hours_service.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationEntryPoint authenticationEntryPoint;
	private final JwtAuthenticationFilter authenticationFilter;

	public SecurityConfig(JwtAuthenticationEntryPoint authenticationEntryPoint,
			JwtAuthenticationFilter authenticationFilter) {
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.authenticationFilter = authenticationFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						// H2 Console
						.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
						// Trainer workload endpoints
						.requestMatchers("/api/v1/trainer-workload/**").authenticated()
						// Swagger, health, vs
						.requestMatchers(
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/actuator/**"
						).permitAll()
						// other requests
						.anyRequest().permitAll()
				)
				.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.headers(headers -> headers.frameOptions(frame -> frame.disable()))
				.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
