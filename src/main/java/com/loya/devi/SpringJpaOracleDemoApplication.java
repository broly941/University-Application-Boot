package com.loya.devi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;

@SpringBootApplication
@EnableJpaAuditing
public class SpringJpaOracleDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringJpaOracleDemoApplication.class, args);
    }
}
