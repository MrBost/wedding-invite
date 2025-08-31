package com.bost.wedding.invite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties
@EnableTransactionManagement
public class InviteApplication {

	public static void main(String[] args) {
		SpringApplication.run(InviteApplication.class, args);
	}

}
