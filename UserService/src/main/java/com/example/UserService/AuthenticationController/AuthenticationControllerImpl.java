package com.example.UserService.AuthenticationController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.UserService.Entity.JwtRequest;
import com.example.UserService.Entity.JwtResponse;
import com.example.UserService.Entity.User;
import com.example.UserService.Service.UserService;

@RestController
@RequestMapping("/userAuth")
public class AuthenticationControllerImpl implements AuthenticationController {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationControllerImpl.class);

	@Autowired
	private UserService userService;

	@Override
	public String createUser(@RequestBody User user) {
		String existEmployee = null;
		if (user != null) {
			try {
				logger.info("Creating User: {}", user.getName());
				existEmployee = userService.createUser(user);
			} catch (Exception e) {
				logger.error(e.toString());
			}
		} else {
			logger.info("User is null");
		}
		return existEmployee;
	}

	@Override
	public ResponseEntity<String> login(JwtRequest jwtRequest) {
		try {
			if (jwtRequest != null) {
				return userService.login(jwtRequest);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseEntity<JwtResponse> verifyOTP(Integer OTP, String email) {
		try {
			if (email != null && OTP != null || !email.contains("") && OTP != 0)
				return userService.OTPVerifier(email, OTP);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
	}

}
