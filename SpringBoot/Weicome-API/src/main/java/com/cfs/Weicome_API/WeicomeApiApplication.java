package com.cfs.Weicome_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class WeicomeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeicomeApiApplication.class, args);
	}


	@Bean
	public RestTemplate getInstance()
	{
		return new RestTemplate();
	}

}
