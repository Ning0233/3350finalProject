package com.company.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebApp {
    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }

    @Bean
    public EmployeeDAO employeeDAO() throws Exception {
        // uses src/main/resources/db.properties by default
        return new EmployeeDAO("db.properties");
    }
}
