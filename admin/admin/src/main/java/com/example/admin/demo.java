package com.example.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class demo {
    private final RestTemplate restTemplate;

    public demo(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/callPartner")
    public String callPartner() {
        // call through the gateway
        // http://localhost:8080/partner
        String result = restTemplate.getForObject("http://partner-service/api/hello", String.class);
        System.out.println("Call from Admin to Partner: " + result);
        return "Call from Admin to Partner: " + result;
    }
}
