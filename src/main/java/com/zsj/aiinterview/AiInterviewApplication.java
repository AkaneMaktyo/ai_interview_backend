package com.zsj.aiinterview;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zsj.aiinterview.mapper")
public class AiInterviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiInterviewApplication.class, args);
    }
}