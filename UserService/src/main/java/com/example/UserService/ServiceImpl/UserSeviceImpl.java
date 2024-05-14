package com.example.UserService.ServiceImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.UserService.Config.JwtService;
import com.example.UserService.Entity.JwtRequest;
import com.example.UserService.Entity.JwtResponse;
import com.example.UserService.Entity.Task;
import com.example.UserService.Entity.User;
import com.example.UserService.Repository.UserRepository;
import com.example.UserService.Service.TaskClient;
import com.example.UserService.Service.UserService;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class UserSeviceImpl implements UserService, UserDetailsService {

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

	@Override
	public String createUser(User user) {
		try {
			if (!Objects.equals(user.getEmail(), "") && !Objects.equals(user.getName(), "")
					&& !Objects.equals(user.getPassword(), "")) {
				if (user.getEmail() != null && user.getName() != null && user.getPassword() != null) {
					Optional<User> existUser = userRepository.findByemail(user.getEmail());
					if (existUser.isPresent()) {
						logger.warn("User already exist");
						return "User already exist";
					} else {
						String emailregExpn = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
								+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
						String passwordregExpn = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
						if (user.getEmail().matches(emailregExpn)) {
							if (user.getPassword().matches(passwordregExpn)) {
								Random random = new Random();
								Integer otp = random.nextInt(10000, 99999);
								String subject = "This is for e-mail verification";
								String message = "This is for e-mail verification & your one time password is :" + otp;
								sendingEmail(message, subject, user.getEmail());
								user.setOneTimePassword(otp);
								user.setEmail(user.getEmail());
								user.setOtpRequestedTime(LocalDateTime.now());
								user.setPassword(passwordEncoder.encode(user.getPassword()));
								userRepository.save(user);
								logger.info("User saved successfully");
								return "User saved successfully";
							} else {
								return "password must contains one upper,lower,special,numbers ";
							}
						} else {
							return "emailmuct contain localname,@,domain name";
						}
					}
				} else {
					logger.warn("User can not be  null");
					return "User can not be  null";
				}
			} else {
				logger.warn("User can not be  Empty");
				return "User can not be  Empty";
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return "something went wrong";
	}

	@Override
	public String emailVerification(String email, Integer OTP) {
		try {
			User user = userRepository.findByemail(email).get();
			if (user.getOneTimePassword().equals(OTP) && (user.getOtpRequestedTime().isBefore(LocalDateTime.now())
					&& (user.getOtpRequestedTime().plusMinutes(5).isAfter(LocalDateTime.now())))) {
				userRepository.save(user);
				return "email verification sucessfull";
			} else {
				return "OTP expired,try with new OTP";
			}
		} catch (Exception e) {
			logger.error(e.toString());
			return e.toString();
		}
	}

	@Override
	public Object getUserById(Long id) {
		try {
			Optional<User> getUser = userRepository.findById(id);
			if (getUser.isPresent()) {
				User user = getUser.get();

				// fetch task by userId
				List<Task> taskList = taskClient.getTasksOfUser(user.getId()).stream()
						.sorted((task1, task2) -> task2.getPriority().compareTo(task1.getPriority()))
						.collect(Collectors.toList());
				// set list of fetched tasks to user
				user.setTask(taskList);
				logger.info("succesfully get the user");
				return user;
			} else {
				logger.warn("invaliad user id");
				return "invaliad user id";
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return "something went wrong";
	}

	@Override
	public List<User> getAllUsers(@RequestParam int pageNumber, @RequestParam int pageSize) {
		try {
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			Page<User> pageList = userRepository.findAll(pageable);
			List<User> userList = pageList.getContent();

			List<User> newUserList = userList.stream().map(user -> {
				user.setTask(taskClient.getTasksOfUser(user.getId()));
				return user;
			}).collect(Collectors.toList());
			return newUserList;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
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
	public User updateUser(Long id, User updateUser) throws Exception {

		if (id == null || updateUser == null) {
			throw new IllegalArgumentException("User id and updated user cannot be null");
		}

		User user = userRepository.findById(id).orElse(null);
		if (user == null) {
			throw new IllegalArgumentException("User not found with id: " + id);
		}

		if (updateUser.getName() != null && !updateUser.getName().isEmpty()) {
			user.setName(updateUser.getName());
		}
		if (updateUser.getEmail() != null && !updateUser.getEmail().isEmpty()) {
			user.setEmail(updateUser.getEmail());
		}

		try {
			userRepository.save(user);
			return user;
		} catch (Exception e) {
			throw new RuntimeException("Error updating user: " + e.getMessage());
		}

	}

	@Override
	public List<User> getUsersBetweenDates(Date startDate, Date endDate) {
		try {
			List<User> userList = userRepository.findByCreationDateBetween(startDate, endDate).get();
			if (!userList.isEmpty())
				logger.info("user List is not empty");
			return userList;
		} catch (Exception e) {
			logger.error("exception :{}", e);
		}
		return null;
	}

	@Override
	public String uploadImage(String path, MultipartFile file) {

		String name = file.getOriginalFilename();

		String filePath = path + File.separator + name;

		File f = new File(path);
		if (!f.exists()) {
			f.mkdir();
		}

		try {

			File files = new File("Documents/" + file.getOriginalFilename());
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

//	@Override
//	public String login(String email, String password) {
//		try {
//			User getuser = userRepository.findByemail(email).get();
//			if (passwordEncoder.matches(password, getuser.getPassword())) {
//				// this.doAuthenticate(email, password);
//				Random random = new Random();
//				Integer otp = random.nextInt(10000, 99999);
//				String subject = "This Mail for OTP verification";
//				String message = "your one time password is :" + otp
//						+ "and the link is : http://localhost:8084/swagger-ui/index.html#/task-controller-impl/addTask";
//				sendEmail(message, subject, getuser.getEmail());
//				getuser.setOneTimePassword(otp);
//				getuser.setOtpRequestedTime(LocalDateTime.now());
//				userRepository.save(getuser);
//				String token = jwtService.genrateToken(getuser);
//				return token;
//			} else {
//				return "Invalid Cridentials!!!!";
//			}
//		} catch (Exception e) {
//			logger.error(e.toString());
//			return e.toString();
//		}
//		
//	}

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
		}
	}

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
		}
	}

	private void doAuthenticate(String email, String password) {

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				email, password);
		try {
			authenticationManager.authenticate(usernamePasswordAuthenticationToken);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	// OTP verification for login
	@Override
	public ResponseEntity<JwtResponse> OTPVerifier(String email, Integer OTP) {
		try {
			User user = userRepository.findByemail(email).orElseThrow(() -> new Exception("User not found"));
			if (user.getOneTimePassword().equals(OTP) && (user.getOtpRequestedTime().isBefore(LocalDateTime.now())
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
		}
	}

	@Override
	public String ChangePassword(String email, String oldPassword, String newPassword) {
		User existUser = userRepository.findByemail(email).get();
		if (existUser != null) {
			if (existUser.getPassword().equals(passwordEncoder.encode(newPassword))) {
				return "you cant keep password same as old password";
			}
			if (existUser.getPassword().equals(passwordEncoder.encode(oldPassword))) {
				existUser.setPassword(passwordEncoder.encode(newPassword));
				userRepository.save(existUser);
				return "password change successffully";
			}
		} else {
			return "user not valid";
		}
		return "something went wrong";
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByemail(username).get();
		return user;
	}

	@Override
	public String sendEmailWithAttachment(String email, String Cc, String Bcc, String subject, String message,
			MultipartFile attatchent) {
		try {
//			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//			simpleMailMessage.setTo(email);
//			simpleMailMessage.setCc(Cc);
//			simpleMailMessage.setBcc(Bcc);
//			simpleMailMessage.setSubject(subject);
//			simpleMailMessage.setText(message);
//			
//
//			javaMailSender.send(simpleMailMessage);
//			return "mail send";

			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setCc(Cc);
			mimeMessageHelper.setBcc(Bcc);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(message);

			mimeMessageHelper.addAttachment(attatchent.getOriginalFilename(),
					new ByteArrayDataSource(attatchent.getBytes(), attatchent.getOriginalFilename()));

			javaMailSender.send(mimeMessage);
			return "mail send";
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return "Done";
	}
}

//@Override
//public String sendingEmail(String email, String subject, String message) {
//	try {
//		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//		simpleMailMessage.setTo(email);
//		simpleMailMessage.setSubject(subject);
//		simpleMailMessage.setText(message);
//
//		javaMailSender.send(simpleMailMessage);
//		return "mail Send Sucessfully!!!";
//	} catch (Exception e) {
//		logger.error(e.toString());
//		return "something went wrong!";
//	}
//}
