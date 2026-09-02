package com.tranquility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpeakSmartApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeakSmartApplication.class, args);
	}

}
