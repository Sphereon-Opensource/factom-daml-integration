package com.sphereon.da.ledger.mithra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@ComponentScan("com.sphereon")
@EnableScheduling
public class MithraApplication {
    public static void main(String[] args) {
        SpringApplication.run(MithraApplication.class, args);
    }

    @Scheduled(fixedDelay = 5000)
    public void liveOn(){}
}
