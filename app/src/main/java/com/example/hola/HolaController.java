package com.example.hola;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaController {

    @Value("${app.version:dev}")
    private String version;

    @GetMapping("/hola")
    public String hola() {
        return "Hola Mundo";
    }

    @GetMapping("/version")
    public String version() {
        return version;
    }
}
