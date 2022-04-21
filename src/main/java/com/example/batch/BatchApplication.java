package com.example.batch;

import com.example.batch.domain.User;
import com.example.batch.domain.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.stream.IntStream;

@EnableScheduling
@SpringBootApplication
public class BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(UserRepository userRepository) {
		return (args) -> {
			IntStream.rangeClosed(1, 20).forEach(index ->
					userRepository.save(User.builder()
							.name(String.format("이름%s", index))
							.email(String.format("test%s@naver.com", index))
							.amount(index * 1000)
							.build())
			);
		};
	}
}
