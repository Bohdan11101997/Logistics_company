package edu.netcracker.project.logistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "edu.netcracker.project.logistic" })
public class Application {
    public static void main(String[] args) { SpringApplication.run(Application.class, args); }

}