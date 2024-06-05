package com.chensoul.resourceserver;

import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        LocalDateTime time = LocalDateTime.now();
        return "Hello from the resource server! - " + time;
    }

}
