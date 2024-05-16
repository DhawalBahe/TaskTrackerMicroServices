package com.tasktracker.userservice.AuthController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tasktracker.userservice.Entity.JwtRequest;
import com.tasktracker.userservice.Entity.JwtResponse;
import com.tasktracker.userservice.Entity.User;
import com.tasktracker.userservice.Service.UserService;
import com.tasktracker.userservice.response.UserResponse;


@RestController
@RequestMapping("/userAuth")
public class AuthenticationControllerImpl implements AuthenticationController {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationControllerImpl.class);

	@Autowired
	private UserService userService;

	@Override
	public ResponseEntity<UserResponse<User>> createUser(User user) {
		UserResponse<User> userResponse = new UserResponse<User>();
		try {
			userResponse = userService.createUser(user);
			if (userResponse.isStatus()) {
				return new ResponseEntity<UserResponse<User>>(userResponse, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<UserResponse<User>>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<UserResponse<User>>(HttpStatus.NOT_FOUND);
		}
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
