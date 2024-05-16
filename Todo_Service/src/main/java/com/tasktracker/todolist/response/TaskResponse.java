package com.tasktracker.todolist.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskResponse<T> {

	private Object data;

	private String message;

	private boolean status;

	public TaskResponse(String message, boolean status) {
		super();
		this.message = message;
		this.status = status;
	}

}
