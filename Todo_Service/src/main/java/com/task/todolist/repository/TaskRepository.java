package com.task.todolist.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.task.todolist.entity.Status;
import com.task.todolist.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

	public Optional<Task> findByTitle(String title);

	@Query("select t from Task t where t.completionDate >= :presentDate")
	List<Task> getAllRemainningTask(@Param("presentDate") LocalDate presentDate);

    //Named Query
	@Query(name = "findByCreationDate")
	public List<Task> findTaskByCreationDate(LocalDate creationDate);

	// Native Query
	@Query(nativeQuery = true, value = "SELECT * FROM Task WHERE Task.completion_date=:completionDate")
	public List<Task> findTaskByCompletionDate(LocalDateTime completionDate);

	public List<Task> findByuserId(Long userId);

	public Optional<Task> findTaskByTitleAndUserId(String title, Long id);

	public Optional<List<Task>> findByCompletionDateBetween(LocalDateTime startTime, LocalDateTime endTime);

	public Optional<List<Task>> findByCompletionDateBetweenAndStatus(LocalDateTime startTime, LocalDateTime endTime,
			Status status);

}
