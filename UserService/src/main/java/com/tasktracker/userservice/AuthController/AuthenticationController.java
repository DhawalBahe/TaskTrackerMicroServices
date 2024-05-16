package com.tasktracker.userservice.AuthController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.tasktracker.userservice.Entity.JwtRequest;
import com.tasktracker.userservice.Entity.JwtResponse;
import com.tasktracker.userservice.Entity.User;
import com.tasktracker.userservice.response.UserResponse;



public interface AuthenticationController {

    @PostMapping("/createUser")
    public ResponseEntity<UserResponse<User>> createUser(@RequestBody User user);

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody JwtRequest jwtRequest);

	@PostMapping("/otpVerification")
	public ResponseEntity<JwtResponse> verifyOTP(@RequestParam Integer OTP, @RequestParam String email);

}
