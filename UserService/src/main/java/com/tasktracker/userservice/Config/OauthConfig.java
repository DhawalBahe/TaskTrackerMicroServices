package com.tasktracker.userservice.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@EnableWebSecurity
public class OauthConfig {

//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(
//				auth -> auth.requestMatchers("/user/**").permitAll().anyRequest().authenticated())
////				.formLogin(Customizer.withDefaults())
//				.oauth2Login(Customizer.withDefaults());
//
//		return http.build();
//	}
}
