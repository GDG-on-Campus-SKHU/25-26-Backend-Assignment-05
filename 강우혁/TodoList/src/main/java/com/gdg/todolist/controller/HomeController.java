package com.gdg.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {
    @GetMapping("/index.html")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
