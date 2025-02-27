package com.tutorial.message.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageService {

    @GetMapping("/messages")
    public String getMessages() {
        return "not implemented yet";
    }
}
