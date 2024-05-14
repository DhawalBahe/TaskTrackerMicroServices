package com.example.UserService.Controller;

import java.sql.Date;
import java.util.List;

import org.junit.platform.commons.function.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.UserService.Entity.JwtRequest;
import com.example.UserService.Entity.JwtResponse;
import com.example.UserService.Entity.User;
import com.example.UserService.Service.UserService;

@RestController
@RequestMapping("/user")
public class UserControllerImpl implements UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserControllerImpl.class);

	@Autowired
	private UserService userService;

	@Value("${project.image}")
	private String path;

	List<User> getAllUserAndTasks(@PathVariable Long userId) {
		return null;
	}

	@Override
	public ResponseEntity<?> getUsers(@RequestParam Long id) {
		if (id != 0) {
			logger.info("get user by {}", id);
			Object existEmployee = userService.getUserById(id);
			return ResponseEntity.status(HttpStatus.CREATED).body(existEmployee);
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body("please enter the user id");
		}
	}

	@Override
	public List<User> getAllUsers(@RequestParam int pageNumber, @RequestParam int pageSize) {
		return userService.getAllUsers(pageNumber, pageSize);
	}

	@Override
	public ResponseEntity<String> deleteUser(@RequestParam Long id) {
		try {
			if (id != 0) {
				logger.info("Deleting User with id: {}", id);
				String existEmployee = userService.deleteUser(id);
				return ResponseEntity.status(HttpStatus.CREATED).body(existEmployee);
			} else {
				logger.warn("please enter the id");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return null;
	}

	@Override
	public User updateUser(@RequestParam Long id, @RequestBody User updateUser) throws Exception {
		if (id != 0 && updateUser != null) {
			logger.info("User updation called");
			return userService.updateUser(id, updateUser);
		} else {
			logger.info("User must not be null or id not equal to zero");
			return null;
		}

	}

	@Override
	public List<User> getUsersBetweenDates(Date startDate, Date endDate) {
		if (startDate != null & endDate != null) {
			logger.info("get users present between dates");
			return userService.getUsersBetweenDates(startDate, endDate);
		} else {
			logger.info("start and end dates must not be null");
			return null;
		}
	}

	public ResponseEntity<User> fileUpload(MultipartFile image) {

		try {
			userService.uploadImage(path, image);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

//	@Override
//	public String login(String email, String password) {
//		try {
//			if (email != null && password != null || !email.contains("") && !password.contains(""))
//				return userService.login(email, password);
//		} catch (Exception e) {
//			logger.error(e.toString());
//		}
//		return null;
//
//	}

	@Override
	public String ChangePassword(String email, String oldPassword, String newPassword) {
		try {
			if (email != null && oldPassword != null && newPassword != null
					|| !email.contains("") && !oldPassword.contains("") && !newPassword.contains(""))
				return userService.ChangePassword(email, oldPassword, newPassword);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
	}

	@Override
	public String emailVerification(Integer OTP, String email) {
		try {
			if (email != null && OTP != null || !email.contains("") && OTP != 0)
				return userService.emailVerification(email, OTP);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
	}

	@Override
	public String sendEmailWithAttachment(String email, String Cc, String Bcc, String subject, String message,
			MultipartFile attatchent) {
		try {
			if (email != null && Cc != null && Bcc != null && subject != null && message != null && attatchent != null
					|| !email.contains("") && !message.contains(""))
				return userService.sendEmailWithAttachment(email, Cc, Bcc, subject, message, attatchent);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
	}
}

//	@Override
//	public String sendEmail(String email, String subject, String message) {
//		try {
//			if (email != null && subject != null && message != null 
//					|| !email.contains("") && !message.contains(""))
//				return userService.sendingEmail(email,subject, message);
//		} catch (Exception e) {
//			logger.error(e.toString());
//		}
//		return null;
//	}
