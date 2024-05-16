package com.tasktracker.userservice.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {

	@Autowired
	private JwtAuthenticationFilter JwtAuthenticationFilter;

	@Autowired
	private AuthenticationProvider AuthenticationProvider;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/user/**").authenticated()
						.requestMatchers("/userAuth/login/**","/userAuth/otpVerification/**").permitAll())
				.formLogin(Customizer.withDefaults())
				.oauth2Login(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(AuthenticationProvider)
				.addFilterBefore(JwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

}








//http.csrf(csrf->csrf.disable())
//.cors(cors->cors.disable())
//.authorizeHttpRequests(auth->auth
//        .requestMatchers("/user/**").authenticated()
//        .requestMatchers("/userAuth/singUp").permitAll()
//        .requestMatchers("/userAuth/login").permitAll()
//        .requestMatchers("/userAuth/loginJwt").permitAll()
//        .requestMatchers("/userAuth/admincanAccesss").hasAnyAuthority(com.example.UserService.Entity.Role.ADMIN.name())
//        .requestMatchers("/userAuth/supperadmincanAccesss").hasAnyAuthority(com.example.UserService.Entity.Role.SUPERADMIN.name())
//        .requestMatchers("/userAuth/staffadmincanAccesss").hasAnyAuthority(com.example.UserService.Entity.Role.MANAGER.name())
//        .requestMatchers("/userAuth/manageradmincanAccesss").hasAnyAuthority(com.example.UserService.Entity.Role.MANAGER.name())
//        .requestMatchers("/userAuth/usercanAccess").hasAnyAuthority(Role.USER.name())
//        .requestMatchers("/userAuth/signupverification").permitAll()
//        .requestMatchers("/userAuth/twostepverification").permitAll()
//        .anyRequest().authenticated())
//.exceptionHandling(ex->ex.authenticationEntryPoint(point))
//.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//http.addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class);
//return http.build();
//}