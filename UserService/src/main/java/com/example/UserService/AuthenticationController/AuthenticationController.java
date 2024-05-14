package com.example.UserService.AuthenticationController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.UserService.Entity.JwtRequest;
import com.example.UserService.Entity.JwtResponse;
import com.example.UserService.Entity.User;

public interface AuthenticationController {

//	@PostMapping("/login")
//	public String login(@RequestParam String email, @RequestParam String password);

	@PostMapping("/createUser")
	public String createUser(@RequestBody User user);

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody JwtRequest jwtRequest);

	@PostMapping("/otpVerification")
	public ResponseEntity<JwtResponse> verifyOTP(@RequestParam Integer OTP, @RequestParam String email);

}
