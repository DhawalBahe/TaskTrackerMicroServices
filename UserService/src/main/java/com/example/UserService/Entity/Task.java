package com.example.UserService.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Task {
	private Integer id;

	private String title;

	private String description;


	private LocalDate creationDate;

	private LocalDate completionDate;

	private priorityEnum priority;

	private Integer userid;
	
	
	private Status status;
	
	
	private List<String> tags;
	
	
	private TodoTypeEnum todoType;
	
	
	
	private  RatingsEnum rating;
	
	
	List<LocalDate> completionDateHistory = new ArrayList<LocalDate>();
	



}
