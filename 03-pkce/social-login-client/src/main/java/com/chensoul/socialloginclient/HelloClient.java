package com.chensoul.socialloginclient;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("http://localhost:8090")
public interface HelloClient {

    @GetExchange("/")
    String getHello();
}

