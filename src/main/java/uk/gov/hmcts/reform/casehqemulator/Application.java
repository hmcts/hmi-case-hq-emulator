package uk.gov.hmcts.reform.casehqemulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"uk.gov.hmcts.reform.casehqemulator.controllers","uk.gov.hmcts.reform.casehqemulator.services"})
@EnableCircuitBreaker
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

