package icu.congee.id.generator.service.springmvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/id")
public class IdGeneratorController {

    @GetMapping("/generate")
    public String generateId() {
        return null;
    }
}
