package edu.netcracker.project.logistic;

import edu.netcracker.project.logistic.processing.RouteProcessor;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@SpringBootApplication(scanBasePackages = { "edu.netcracker.project.logistic" })
public class Application implements CommandLineRunner {
    private TaskProcessor taskProcessor;
    private RouteProcessor routeProcessor;

    public Application(TaskProcessor taskProcessor, RouteProcessor routeProcessor) {
        this.taskProcessor = taskProcessor;
        this.routeProcessor = routeProcessor;
    }

    public static void main(String[] args) { SpringApplication.run(Application.class, args); }

    @Override
    public void run(String... args) throws Exception {
        taskProcessor.taskLoop();
        routeProcessor.taskLoop();
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Tasks-");
        executor.initialize();
        return executor;
    }
}