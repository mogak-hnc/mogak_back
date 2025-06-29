package com.hnc.mogak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping("/ci-cd")
    public String test() {
        return "ci-cd success";
    }

}