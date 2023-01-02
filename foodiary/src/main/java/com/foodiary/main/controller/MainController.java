package com.foodiary.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
    // aws 헬스 체크
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {

        return new ResponseEntity<>("ok", HttpStatus.OK) ;
    }
}
