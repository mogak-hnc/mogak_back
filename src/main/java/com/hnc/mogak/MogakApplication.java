package com.hnc.mogak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MogakApplication {

	public static void main(String[] args) {
		SpringApplication.run(MogakApplication.class, args);
	}

}