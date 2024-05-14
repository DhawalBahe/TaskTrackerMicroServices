package com.example.UserService.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.UserService.Entity.Role;
import com.example.UserService.Entity.User;

public interface RoleController {

	@PostMapping("/createRole")
	public ResponseEntity<List<Role>> createRole(@RequestBody Role role);

}