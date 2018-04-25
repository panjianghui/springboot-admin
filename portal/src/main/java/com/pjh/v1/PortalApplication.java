package com.pjh.v1;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author 辉
 */
@SpringBootApplication
public class PortalApplication extends SpringBootServletInitializer{
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(PortalApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(PortalApplication.class, args);
	}

}
