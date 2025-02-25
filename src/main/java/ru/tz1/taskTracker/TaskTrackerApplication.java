package ru.tz1.taskTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Task Tracker.
 * Этот класс содержит метод main, который запускает Spring Boot приложение.
 */
@SpringBootApplication
public class TaskTrackerApplication {

	/**
	 * Метод запуска приложения.
	 *
	 * @param args Аргументы командной строки переданные приложению.
	 */
	public static void main(String[] args) {
		SpringApplication.run(TaskTrackerApplication.class, args); // Запускаем приложение Spring Boot
	}
}