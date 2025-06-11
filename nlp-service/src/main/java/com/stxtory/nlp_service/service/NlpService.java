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
    private final Random random = new Random();

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
        String[] sentences = inputText.split("[.?!\n]");
        Map<String, Integer> pairCounts = new HashMap<>();
        Map<String, Integer> wordFrequency = new HashMap<>();

        for (String sentence : sentences) {
            if (sentence.isEmpty()) continue;
            List<String> tokens = komoran.analyze(sentence).getTokenList().stream()
                    .filter(t -> t.getPos().startsWith("NN"))
                    .map(Token::getMorph)
                    .filter(w -> w.length() >= 1 && !stopwords.contains(w))
                    .collect(Collectors.toList());

            Set<String> uniqueTokens = new HashSet<>(tokens);
            uniqueTokens.forEach(w -> wordFrequency.put(w, wordFrequency.getOrDefault(w, 0) + 1));

            List<String> list = new ArrayList<>(uniqueTokens);
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    String w1 = list.get(i);
                    String w2 = list.get(j);
                    String key = w1.compareTo(w2) < 0 ? w1 + "," + w2 : w2 + "," + w1;
                    pairCounts.put(key, pairCounts.getOrDefault(key, 0) + 1);
                }
            }
        }

        // 상위 단어 추출
        List<String> topWords = wordFrequency.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .map(Map.Entry::getKey)
                .limit(5)
                .collect(Collectors.toList());

        List<String> minPairs = new ArrayList<>();
        int minCount = Integer.MAX_VALUE;

        for (int i = 0; i < topWords.size(); i++) {
            for (int j = i + 1; j < topWords.size(); j++) {
                String w1 = topWords.get(i);
                String w2 = topWords.get(j);
                String key = w1.compareTo(w2) < 0 ? w1 + "," + w2 : w2 + "," + w1;
                int count = pairCounts.getOrDefault(key, 0);

                if (count < minCount) {
                    minCount = count;
                    minPairs.clear();
                    minPairs.add(key);
                } else if (count == minCount) {
                    minPairs.add(key);
                }
            }
        }

        if (!minPairs.isEmpty()) {
            String selectedPair = minPairs.get(random.nextInt(minPairs.size()));
            String[] words = selectedPair.split(",");
            String word1 = words[0];
            String word2 = words[1];
            return String.format("%s%s %s%s 어떤 관련이 있을까요?",
                    word1, josa(word1, "과", "와"),
                    word2, josa(word2, "은", "는"));
        } else {
            return "조금 더 명확한 단어나 문장을 사용해주실 수 있을까요?";
        }
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
