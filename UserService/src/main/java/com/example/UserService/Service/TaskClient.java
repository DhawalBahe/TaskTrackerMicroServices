package com.example.UserService.Service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.UserService.Entity.Task;

@FeignClient(url = "http://localhost:8084", value = "TaskClient")
public interface TaskClient {
	
	@GetMapping("/task/user/{userId}")
	List<Task> getTasksOfUser(@PathVariable Long userId);
	
//	@PostMapping("/task")
//	Task addTask(@RequestBody Task task);
}
