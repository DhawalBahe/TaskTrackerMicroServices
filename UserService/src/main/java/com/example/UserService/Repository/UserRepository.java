package com.example.UserService.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.UserService.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByemail(String email);
	
	public Optional<List<User>> findByCreationDateBetween(Date startDate, Date endDate);

}
