package com.civicfix.tfg.rest.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtGenerator jwtGenerator;

	public SecurityConfig(JwtGenerator jwtGenerator) {
		this.jwtGenerator = jwtGenerator;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())
				.sessionManagement(
						sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new JwtFilter(jwtGenerator), UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(HttpMethod.POST, "/users/signup").permitAll()
						.requestMatchers(HttpMethod.POST, "/users/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/users/googleLogin").permitAll()
						.requestMatchers(HttpMethod.POST, "/users/loginFromServiceToken").permitAll()
						
						.requestMatchers(HttpMethod.GET, "/users/list").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/users/deleteUser/{userId}").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/users/disableUser/{userId}").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/users/{id}").authenticated()
						.requestMatchers(HttpMethod.PUT, "/users/update").authenticated()
						.requestMatchers(HttpMethod.POST, "/users/*/changePassword").authenticated()
						
						.requestMatchers(HttpMethod.GET, "/posts/feed").permitAll()
						.requestMatchers(HttpMethod.GET, "/posts/{postId}").permitAll()
						.requestMatchers(HttpMethod.GET, "/posts/last").permitAll()

						.requestMatchers(HttpMethod.POST, "/posts/add").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/posts/delete/{postId}").authenticated()
						.requestMatchers(HttpMethod.PUT, "/posts/update/{postId}").authenticated()
						.requestMatchers(HttpMethod.GET, "/posts/myposts").authenticated()
						.requestMatchers(HttpMethod.GET, "/posts/postSelect").hasRole("ADMIN")

						.requestMatchers(HttpMethod.POST, "/posts/{postId}/vote").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/posts/{postId}/vote").authenticated()

						.requestMatchers(HttpMethod.POST, "/mail/contact").permitAll()

						.requestMatchers(HttpMethod.POST, "/surveys/create").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/surveys/answer/{surveyId}").authenticated()
						.requestMatchers(HttpMethod.GET, "/surveys/list").permitAll()

						.requestMatchers(HttpMethod.GET, "/pointSystem/history").authenticated()
						.requestMatchers(HttpMethod.POST, "/pointSystem/redeem").authenticated()
						.anyRequest().denyAll()
						)
						.headers(headers -> headers.frameOptions(frame -> frame.disable()));

		return http.build();

	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration config = new CorsConfiguration();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		config.setAllowCredentials(true);
		config.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173","https://*.vercel.app"));
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");

		source.registerCorsConfiguration("/**", config);

		return source;

	}

}
