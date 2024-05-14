package com.example.UserService.Controller;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.UserService.Entity.JwtRequest;
import com.example.UserService.Entity.JwtResponse;
import com.example.UserService.Entity.User;

public interface UserController {

	@GetMapping("/getUsers")
	public List<User> getAllUsers(@RequestParam int pageNumber, @RequestParam int pageSize);

	@PostMapping("/updateUser")
	public User updateUser(@RequestParam Long id, @RequestBody User updateUser) throws Exception;

	@GetMapping("/getUsersById")
	public ResponseEntity<?> getUsers(@RequestParam Long id);

	@GetMapping("/getUserByDate")
	public List<User> getUsersBetweenDates(@RequestParam Date startDate, @RequestParam Date endDate);

	@PostMapping("/deleteUser")
	public ResponseEntity<String> deleteUser(@RequestParam Long id);

	@PostMapping("/upload")
	public ResponseEntity<User> fileUpload(@RequestPart MultipartFile image);

	@PostMapping("/emailVerification")
	public String emailVerification(@RequestParam Integer OTP, @RequestParam String email);

	// change password
	@PostMapping("/forgetPassword")
	public String ChangePassword(@RequestParam String email, @RequestParam String oldPassword,
			@RequestParam String newPassword);

	@PostMapping("/sendingEmail")
	public String sendEmailWithAttachment (@RequestParam String email, @RequestParam String Cc,@RequestParam String Bcc,@RequestParam String subject, @RequestPart String message,@RequestPart MultipartFile attatchent);
}
