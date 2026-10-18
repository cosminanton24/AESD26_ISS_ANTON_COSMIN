package com.example.lab2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final String greetingMessage;

    public HelloController(@Value("${greeting.message}") String greetingMessage) {
        this.greetingMessage = greetingMessage;
    }

    @GetMapping("/hello")
    public String hello() {
        return greetingMessage;
    }
}
