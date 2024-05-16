package com.tasktracker.userservice.ServiceImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.tasktracker.userservice.Config.JwtService;
import com.tasktracker.userservice.Entity.JwtRequest;
import com.tasktracker.userservice.Entity.JwtResponse;
import com.tasktracker.userservice.Entity.Task;
import com.tasktracker.userservice.Entity.TaskResponse;
import com.tasktracker.userservice.Entity.User;
import com.tasktracker.userservice.Repository.UserRepository;
import com.tasktracker.userservice.Service.UserService;
import com.tasktracker.userservice.feignclient.TaskClient;
import com.tasktracker.userservice.response.UserResponse;

import jakarta.mail.internet.MimeMessage;

@Service
public class UserSeviceImpl implements UserService {

	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TaskClient taskClient;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TemplateEngine templateEngine;

	private static final Logger logger = LoggerFactory.getLogger(UserSeviceImpl.class);
	private static final String ERROR_MESSAGE = "Something went wrong";

	@Override
	public UserResponse<User> createUser(User user) {
		List<User> userList = new ArrayList<User>();

		try {
			if (!Objects.equals(user.getEmail(), "") && !Objects.equals(user.getName(), "")) {
				if (user.getEmail() != null && user.getName() != null) {
					Optional<User> existUser = userRepository.findByemail(user.getEmail());

					if (existUser.isPresent()) {
						userList.add(existUser.get());
						return new UserResponse<>("User already exists", userList, false);
					} else {
						if (user.getPassword().matches("[a-zA-Z0-9@$]{8,}+")) {
							String encoded = new BCryptPasswordEncoder().encode(user.getPassword());
							Random random = new Random();
								Integer otp = random.nextInt(10000, 99999);
								String subject = "This is for e-mail verification";
								String message = "This is for e-mail verification & your one time password is :" + otp;
								sendingEmail(message, subject, user.getEmail());
								user.setOneTimePassword(otp);
								user.setEmail(user.getEmail());
								user.setOtpRequestedTime(LocalDateTime.now());
							user.setPassword(encoded);
							userRepository.save(user);
							return new UserResponse<>("User saved successfully", userList, true);
						} else {
							return new UserResponse<>(
									"Password should contain uppercase, lowercase, digit, and special character and length must be 8 characters",
									userList, false);
						}
					}
				} else {
					return new UserResponse<>("User cannot be null", userList, false);
				}
			} else {
				return new UserResponse<>("User cannot be empty", userList, false);
			}
		} catch (Exception e) {
			return new UserResponse<>(ERROR_MESSAGE, userList, false);
		}
	}

	@Override
	public UserResponse<List<User>> getAllUsers(Pageable paging) {
		List<User> newUserList = new ArrayList<User>();
		try {
			Page<User> userPage = userRepository.findAll(paging);
			List<User> userList = userPage.getContent();

//			newUserList=userList.stream().map(user->{
//				ResponseEntity<TaskResponse<List<Task>>> taskByUserId = taskClient.getTaskByUserId(user.getId());
//				System.out.println(taskByUserId);
//				TaskResponse<List<Task>> body = taskByUserId.getBody();
//				List<Task> taskList = body.getData(); 
//				System.out.print(taskList);
//				user.setTask(taskList);
//				System.out.println(user);
//				return user;
//			}).collect(Collectors.toList());

			for (User user : userList) {
				ResponseEntity<TaskResponse<List<Task>>> taskByUserId = taskClient.getTaskByUserId(user.getId());
				System.out.println(taskByUserId);
				TaskResponse<List<Task>> body = taskByUserId.getBody();
				List<Task> taskList = body.getData();
				System.out.println(taskList);
				user.setTask(taskList);
				newUserList.add(user);

			}
			System.out.println(newUserList);
			return new UserResponse<List<User>>("Users fetched successfully", newUserList, true);
		} catch (Exception e) {
			return new UserResponse<List<User>>(ERROR_MESSAGE, newUserList, false);
		}
	}

	@Override
	public UserResponse<List<User>> getUserById(Long id) {
		List<User> userList = new ArrayList<User>();
		try {
			Optional<User> getUser = userRepository.findById(id);
			if (getUser.isPresent()) {
				User user = getUser.get();
				// fetch task by userId
				ResponseEntity<TaskResponse<List<Task>>> taskByUserId = taskClient
						.getTaskByUserId(getUser.get().getId());
				TaskResponse<List<Task>> body = taskByUserId.getBody();
				List<Task> taskList = body.getData();
				System.out.println(taskList);
				user.setTask(taskList);
				userList.add(user);

			}

			return new UserResponse<List<User>>("Users fetched successfully", userList, true);
		} catch (Exception e) {
			return new UserResponse<List<User>>(ERROR_MESSAGE, userList, false);
		}
	}

	@Override
	public UserResponse<List<User>> getUsersBetweenDates(Date startDate, Date endDate) {
		List<User> userList = new ArrayList<User>();
		try {
			userList = userRepository.findByCreationDateBetween(startDate, endDate).orElse(null);
			if (userList != null && !userList.isEmpty()) {
				return new UserResponse<>("Users fetched successfully", userList, true);
			} else {
				return new UserResponse<>("No users found between the given dates", userList, false);
			}
		} catch (Exception e) {
			return new UserResponse<>(ERROR_MESSAGE, userList, false);
		}
	}

	@Override
	public UserResponse<List<User>> updateUser(Long id, User updateUser) {

		List<User> userList = new ArrayList<User>();
		try {
			if (id == null || updateUser == null) {
				throw new IllegalArgumentException("User id and updated user cannot be null");
			}

			User user = userRepository.findById(id).orElse(null);
			if (user == null) {
				return (new UserResponse<>("User not found with id: " + id, userList, false));
			}

			if (updateUser.getName() != null && !updateUser.getName().isEmpty()) {
				user.setName(updateUser.getName());
			}
			if (updateUser.getEmail() != null && !updateUser.getEmail().isEmpty()) {
				user.setEmail(updateUser.getEmail());
			}
			userList.add(user);
			userRepository.save(user);
			return new UserResponse<>("User updated successfully", userList, false);
		} catch (Exception e) {
			return (new UserResponse<>(ERROR_MESSAGE, userList, false));
		}
	}

	@Override
	public String deleteUser(Long id) {
		logger.info("finding the id of the User for getUserById");
		Optional<User> userOptional = userRepository.findById(id);
		if (!userOptional.isPresent()) {
			logger.error("User ID is  not present=" + id);
			return "user is not present";
		} else {
			userRepository.deleteById(id);
			logger.info("Uesr with ID " + id + " deleted successfully.");
			return "user deleted sucessfully";
		}
	}

	@Override
	public String uploadImage(String path, MultipartFile file) {
		// TODO Auto-generated method stub

		// File name

		String name = file.getOriginalFilename();
		// File Path

		String filePath = path + File.separator + name;

		// create folder if not created

		File f = new File(path);
		if (!f.exists()) {
			f.mkdir();
		}

		// file copy

		try {

			File files = new File("images/" + file.getOriginalFilename());
			if (files.exists()) {
				System.out.println("file already exist");
			} else {
				Files.copy(file.getInputStream(), Paths.get(filePath));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	@Override
	public void sendSimpleMessage(String email, String password) {
		try {
			Random random = new Random();
			long otp = random.nextInt(1000, 9999);
			Optional<User> user = userRepository.findByemail(email);
			if (user.isPresent() && user.get().getPassword().equals(password)) {
				
				userRepository.save(user.get());
				String body = "your OTP is " + otp;
				String subject = "otp verification";
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(email);
				message.setSubject(subject);
				message.setText(body);
				javaMailSender.send(message);

			}

		} catch (Exception e) {
			logger.error("UserServiceImpl {}", e);
		}
	}

	@Override
	public Boolean checkOtp(String email, Long otp) {

		Optional<User> user = userRepository.findByemail(email);
		if (user.get().getOneTimePassword().equals(otp)) {
			user.get().setOneTimePassword(null);
			userRepository.save(user.get());
			return true;
		}
		user.get().setOneTimePassword(null);
		userRepository.save(user.get());
		return false;

	}

	@Override
	public ResponseEntity<String> login(JwtRequest jwtRequest) {
		try {
			User user = userRepository.findByemail(jwtRequest.getEmail())
					.orElseThrow(() -> new Exception("User not found"));

			this.doAuthenticate(jwtRequest.getEmail(), jwtRequest.getPassword());

			Random random = new Random();
			Integer otp = random.nextInt(10000, 99999);
			String subject = "This Mail for OTP verification";
			String message = "Your one-time password is: " + otp
					+ " and the link is: http://localhost:8084/swagger-ui/index.html#/task-controller-impl/addTask";

			sendingEmail(message, subject, user.getEmail());
			user.setOneTimePassword(otp);
			user.setOtpRequestedTime(LocalDateTime.now());
			userRepository.save(user);

			return new ResponseEntity<String>("OTP sent successfully", HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}}

	private void sendingEmail(String message, String subject, String email) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			
			Context context = new Context();
			
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setSubject(subject);
			
            context.setVariable("content", message);
            String processedString = templateEngine.process("EmailTemplateforOTP", context);

            mimeMessageHelper.setText(processedString, true);

			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			logger.error(e.toString());
		}}

	private void doAuthenticate(String email, String password) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				email, password);
		try {
			authenticationManager.authenticate(usernamePasswordAuthenticationToken);
		} catch (Exception e) {
			logger.error(e.toString());
		}}

	@Override
	public ResponseEntity<JwtResponse> OTPVerifier(String email, Integer oTP) {
		try {
			User user = userRepository.findByemail(email).orElseThrow(() -> new Exception("User not found"));
			if (user.getOneTimePassword().equals(oTP) && (user.getOtpRequestedTime().isBefore(LocalDateTime.now())
					&& (user.getOtpRequestedTime().plusMinutes(5).isAfter(LocalDateTime.now())))) {

				// generate token after OTP verification
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				String token = jwtService.generateToken(userDetails);

				// generate response after OTP verification
				JwtResponse response = JwtResponse.builder().Token(token).email(userDetails.getUsername()).build();
				return new ResponseEntity<JwtResponse>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<JwtResponse>(HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			logger.error(e.toString());
			return new ResponseEntity<JwtResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
		}}

}
