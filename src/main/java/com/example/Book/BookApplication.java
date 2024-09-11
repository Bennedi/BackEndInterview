package com.example.Book;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDate;

@SpringBootApplication
@EnableJpaRepositories
@EnableAsync
@EnableCaching
public class BookApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookApplication.class, args);
	}

	@Bean
	CommandLineRunner init(BookRepository bookRepository) {
		return args -> {
			// Create some books and save them to the database
//			Books books1 = new Books(1L,"Lorenzo's Adventure","Lala", LocalDate.now());
//			bookRepository.save(books1);

		};
	}

}
