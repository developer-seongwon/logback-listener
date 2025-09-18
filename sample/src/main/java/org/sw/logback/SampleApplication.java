package org.sw.logback;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/log")
@SpringBootApplication
public class SampleApplication {

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @GetMapping("/")
    public void logging() {
        this.scheduler.scheduleAtFixedRate(() -> {
            LoggerFactory.getLogger(SampleApplication.class).info("INFO - " + LocalDateTime.now());
        },0, 1, TimeUnit.MILLISECONDS);
    }
}
