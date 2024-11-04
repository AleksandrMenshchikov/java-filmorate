package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FilmorateApplication {
	private static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
		run(args);
	}

	public static void run(String[] args) {
		ctx = SpringApplication.run(FilmorateApplication.class, args);
	}

	public static void close() {
		ctx.close();
	}
}
