package com.devopsboard.cicd_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
public class CicdMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CicdMonitorApplication.class, args);
	}

}
