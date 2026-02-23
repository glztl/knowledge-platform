package com.knowledge.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Options;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/hello")
    @Operation(summary = "hello")
    public String hello() {
        return "hello";
    }
}
