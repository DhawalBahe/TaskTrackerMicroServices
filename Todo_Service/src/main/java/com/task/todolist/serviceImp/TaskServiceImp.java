package com.task.todolist.serviceImp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.task.todolist.entity.RatingsEnum;
import com.task.todolist.entity.Status;
import com.task.todolist.entity.Task;
import com.task.todolist.repository.TaskRepository;
import com.task.todolist.repository.TaskSearchDAO;
import com.task.todolist.service.TaskService;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskServiceImp implements TaskService {

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private TaskSearchDAO taskSearchDAO;

	public static List<Task> notificationTask;

	public TaskServiceImp(TaskRepository taskRepository) {
		super();
		this.taskRepository = taskRepository;
	}

	@Override
	public List<Task> getByUserId(Long userId) {
		try {
			List<Task> taskList = taskRepository.findByuserId(userId);
			if (!taskList.isEmpty()) {
				return taskList.stream().sorted((task1, task2) -> task2.getPriority().compareTo(task1.getPriority()))
						.collect(Collectors.toList());

			}
		} catch (Exception e) {
			log.error("exception " + e.toString());
		}
		return null;

	}

	@Override
	public List<Task> getAllTask(int pageNumber, int pageSize) {
		try {
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			Page<Task> pageList = taskRepository.findAll(pageable);
			List<Task> taskList = pageList.getContent();
			log.info("list of tasks:{} ", taskList);
			if (!taskList.isEmpty()) {
				log.info("taskList is not empty");
				return taskList.stream().sorted((task1, task2) -> task2.getPriority().compareTo(task1.getPriority()))
						.collect(Collectors.toList());

			}

		} catch (Exception e) {
			log.error("exception in getAllTask :{} ", e);
		}

		return null;

	}

	@Override
	public Task getTaskById(Integer id) {
		log.info("Id : {}", id);
		try {
			Optional<Task> task = taskRepository.findById(id);
			if (!task.isEmpty()) {
				log.info("task is not empty");
				return task.get();
			}
		} catch (Exception e) {
			log.error("exception in getTaskById : {}", e);
		}

		return null;
	}

	@Override
	public Task addTask(Task task) {
		log.info("task :{}", task);
		try {
			if (task.getTitle() != null && task.getUserId() != null) {
				Optional<Task> existingTask = taskRepository.findTaskByTitleAndUserId(task.getTitle().toLowerCase(),
						task.getUserId());
				log.info("existingTask :{}", existingTask);
				if (existingTask.isEmpty()) {
					List<Task> taskList = taskRepository.findByuserId(task.getUserId());
					for (Task checkTask : taskList) {
						if ((task.getCompletionDate()).isBefore(checkTask.getCompletionDate().plusMinutes(30))) {
							log.info("task already present on given time");
							return null;
						}
					}
					log.info("existingTask is empty we can add new task");
					Task newTask = new Task();

					newTask.setTitle(task.getTitle().toLowerCase());
					newTask.setUserId(task.getUserId());
					newTask.setDescription(task.getDescription());
					newTask.setCompletionDate(task.getCompletionDate());
					newTask.setPriority(task.getPriority());
					newTask.setStatus(task.getStatus());
					newTask.setRating(task.getRating());
					newTask.setTodoType(task.getTodoType());
					newTask.setTags(task.getTags());
					return taskRepository.save(newTask);

				}
			}
		} catch (Exception e) {
			log.error("exception in addTask : {}", e);
		}
		log.info("task already exist");
		return null;
	}

	@Override
	public Task updateTask(Task newTask, Integer id) {
		try {
			if (id != null && newTask != null) {
				Optional<Task> task = taskRepository.findById(id);
				log.info("update task :{}", task);

				if (task.isPresent()) {
					log.info(" task is not null :{}", task);

					if (newTask.getDescription() != null) {
						log.info(" description is not null :{}", newTask.getDescription());
						task.get().setDescription(newTask.getDescription());
					}
					if (newTask.getCompletionDate() != null) {
						log.info(" completionDate is not null :{}", newTask.getCompletionDate());
						task.get().getCompletionDateHistory().add(task.get().getCompletionDate());
						task.get().setCompletionDate(newTask.getCompletionDate());
					}

					return taskRepository.save(task.get());

				}
			}

		} catch (Exception e) {
			log.error("exception in update task :{} ", e);
		}
		log.info("task not found with id : {}", id);
		return null;
	}

	@Override
	public boolean deleteTaskById(Integer id) {

		try {
			log.info("id : {}", id);
			Optional<Task> task = taskRepository.findById(id);
			if (task.isPresent()) {
				log.info("task is not empty ; {}", task);
				taskRepository.deleteById(id);
				return true;
			}
		} catch (Exception e) {
			log.error("exception :{} ", e);
		}
		log.info("task not found with id : {}", id);
		return false;

	}

	@Override
	public List<Task> getAllRemainningTask() {
		try {
			LocalDate date = LocalDate.now();
			List<Task> taskList = taskRepository.getAllRemainningTask(date);
			log.info("taskList " + taskList);
			if (!taskList.isEmpty()) {
				log.info("taskList is not empty :{}", taskList);
				return taskList;
			}

		} catch (Exception e) {
			log.error("exception in updateTask :{} ", e);
		}
		log.info("tasks not found");
		return null;
	}

	@Override
	public List<Task> getTaskByCreationDate(LocalDate creationDate) {
		try {
			List<Task> task = taskRepository.findTaskByCreationDate(creationDate);

			log.info("taskList :{}", task);
			if (!task.isEmpty()) {
				return task;
			}
		} catch (Exception e) {
			log.error("exception in getTaskByCreationDate : {}", e);
		}
		log.info("task not found");
		return null;
	}

	@Override
	public List<Task> getTaskByCompletionDate(LocalDateTime completionDate) {
		try {

			List<Task> task = taskRepository.findTaskByCompletionDate(completionDate);
			log.info("taskList:{} ", task);
			if (!task.isEmpty()) {
				List<Task> task1 = taskRepository.findTaskByCompletionDate(completionDate);
				log.info("taskList:{} ", task1);
				if (!task1.isEmpty()) {

					return task1;
				}
			}
		} catch (Exception e) {
			log.error("exception in getTaskByCompletionDate:{} ", e);
		}
		log.info("task not found");
		return null;
	}

//===================== for one minutes scheduler==================
//	@Override
//	@Scheduled(fixedRate = 60000)
//	public List<Task> ScheduleNotification() {
//		System.out.println("scheduler");
//		LocalDateTime currentTime = LocalDateTime.now();
//
//		List<Task> taskList = taskRepository.findAll();
//		for (Task task : taskList) {
//
//			System.out.println(currentTime);
//			LocalDateTime complitiontime = task.getCompletionDate();
//			System.out.println(complitiontime);
//			System.out.println(task.getCompletionDate());
//
//			if (currentTime.withSecond(0).withNano(0).equals(complitiontime.minusHours(1).withSecond(0).withNano(0))) {
//				System.out.println("your task" + task.getTitle() + " is near complition date");
//			}
//		}
//		return null;
//	}
//}
//=======================end==========================================
//--------------------for one hours-----------------------------------
	@Override
	@Scheduled(fixedRate = 60 * 60 * 1000)
	public List<Task> ScheduleNotification() {
		log.info("schedulerHours");
		LocalDateTime startTime = LocalDateTime.now().plusHours(1);
		LocalDateTime endTime = LocalDateTime.now().plusHours(2);
		log.info(startTime + "," + endTime);
		Status notifystatus = Status.INPROGRESS;
		notificationTask = taskRepository.findByCompletionDateBetweenAndStatus(startTime, endTime, notifystatus).get();
		log.info(notificationTask.toString());
		return notificationTask;
	}

	@Scheduled(fixedRate = 60 * 1000)
	public void subScheduleNotification() {
		log.info("schedulerMinutes");
		LocalDateTime currentTime = LocalDateTime.now();
		log.info(notificationTask.toString());
		for (Task task : notificationTask) {
			log.info(currentTime.toString());
			LocalDateTime complitiontime = task.getCompletionDate();
			log.info(complitiontime.toString());
			log.info(task.getCompletionDate().toString());
			if (currentTime.withSecond(0).withNano(0).equals(complitiontime.minusHours(1).withSecond(0).withNano(0))) {
				log.info("your task" + task.getTitle() + " is near complition date");
			}
		}
	}
//-------------------------------end--------------------------------------	

	@Override
	public List<Task> findByTitleName(String title) {
		return taskSearchDAO.findByTitleName(title);
	}

}
