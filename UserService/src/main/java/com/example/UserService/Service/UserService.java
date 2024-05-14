package com.example.UserService.Service;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.UserService.Entity.JwtRequest;
import com.example.UserService.Entity.JwtResponse;
import com.example.UserService.Entity.User;

@Service
public interface UserService {

	String createUser(User user);

	String deleteUser(Long id);

	Object getUserById(Long id);

	List<User> getAllUsers(@RequestParam int pageNumber, @RequestParam int pageSize);

	User updateUser(Long id, User updateUser) throws Exception;

	List<User> getUsersBetweenDates(Date startDate, Date endDate);

	String uploadImage(String path, MultipartFile file);

	// String login(String email, String password);

	ResponseEntity<JwtResponse> OTPVerifier(String email, Integer OTP);

	String ChangePassword(String email, String oldPassword, String newPassword);

	String emailVerification(String email, Integer oTP);

	String sendEmailWithAttachment(String email, String Cc, String Bcc, String subject, String message, MultipartFile attatchent);

	ResponseEntity<String> login(JwtRequest jwtRequest);

}
