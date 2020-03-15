package com.antnest.antmsgerconverterdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.antnest"})
public class AntMsgerConverterDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AntMsgerConverterDemoApplication.class, args);
    }

}
