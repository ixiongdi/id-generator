package icu.congee.id.generator.service.springwebflux.controller;

import icu.congee.id.util.IdUtil;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/id")
public class IdGeneratorController {

    @GetMapping("/generate")
    public Mono<String> generateId() {
        return Mono.just(IdUtil.uuid7());
    }
}
