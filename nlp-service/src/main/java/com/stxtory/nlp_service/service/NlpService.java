package com.stxtory.nlp_service.service;


import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NlpService {

    private final Komoran komoran;
    private final Set<String> stopwords;

    public NlpService() throws IOException {
        this.komoran = new Komoran(DEFAULT_MODEL.FULL);
        this.stopwords = loadStopwords("/stopwords-ko.txt");
    }

    private Set<String> loadStopwords(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        }
    }

    public String generateQuestion(String inputText) {
        List<String> tokens = komoran.analyze(inputText).getTokenList().stream()
                .filter(t -> t.getPos().startsWith("NN"))
                .map(Token::getMorph)
                .filter(w -> w.length() >= 2 && !stopwords.contains(w))
                .toList();

        Map<String, Long> frequencyMap = tokens.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Optional<Map.Entry<String, Long>> mostFrequent = frequencyMap.entrySet().stream().min((a, b) -> Long.compare(b.getValue(), a.getValue()));

        if (mostFrequent.isPresent()) {
            String keyword = mostFrequent.get().getKey();
            return String.format("\u201c%s\u201d%s 자주 떠오른다면 어떤 이유일까요?",
                    keyword, josa(keyword, "이", "가"));
        }

        return "조금 더 구체적인 표현을 사용해주실 수 있을까요?";
    }

    private String josa(String word, String batchim, String noBatchim) {
        if (word == null || word.isEmpty()) return noBatchim;

        char lastChar = word.charAt(word.length() - 1);
        if (lastChar < 0xAC00 || lastChar > 0xD7A3) {
            return noBatchim;
        }

        int code = lastChar - 0xAC00;
        boolean hasBatchim = (code % 28) != 0;

        return hasBatchim ? batchim : noBatchim;
    }
}