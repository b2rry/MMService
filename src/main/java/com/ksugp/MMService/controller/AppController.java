package com.ksugp.MMService.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AppController {
    @GetMapping("/service")
    public String getAllActions() {
        return "service";
    }
}
