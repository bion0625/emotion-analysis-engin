package com.stxtory.nlp_service.controller;

import com.stxtory.nlp_service.service.NlpService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nlp")
public class NlpController {

    private final NlpService nlpService;

    public NlpController(NlpService nlpService) {
        this.nlpService = nlpService;
    }

    @PostMapping("/analyze")
    public String analyzeText(@RequestBody Map<String, String> payload) {
        String input = payload.get("text");
        return nlpService.generateQuestion(input);
    }

    @PostMapping("/analyze/long")
    public String analyzeText(@RequestBody String text) {
        return nlpService.generateQuestion(text);
    }
}
