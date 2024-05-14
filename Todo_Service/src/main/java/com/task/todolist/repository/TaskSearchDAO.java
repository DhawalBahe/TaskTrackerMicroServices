package com.task.todolist.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.task.todolist.entity.RatingsEnum;
import com.task.todolist.entity.Status;
import com.task.todolist.entity.Task;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class TaskSearchDAO {

	@Autowired
	private EntityManager entityManager;

	public List<Task> findByTitleName(String title) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);

		// Select * from task
		Root<Task> tRoot = criteriaQuery.from(Task.class);

		// prepare WHERE clause
		Predicate titlepPredicate = criteriaBuilder.equal(tRoot.get("title"), title);

		criteriaQuery.where(titlepPredicate);

		TypedQuery<Task> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}
}
