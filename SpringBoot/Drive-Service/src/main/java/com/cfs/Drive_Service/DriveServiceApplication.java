package com.cfs.Drive_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DriveServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriveServiceApplication.class, args);
	}

}
