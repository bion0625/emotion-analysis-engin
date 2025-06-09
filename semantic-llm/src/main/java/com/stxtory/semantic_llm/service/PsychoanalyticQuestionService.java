package com.stxtory.semantic_llm.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.model.ChatResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

// 이 클래스가 Spring의 서비스 컴포넌트임을 나타냅니다.
@Service
public class PsychoanalyticQuestionService {
    private final ChatClient chatClient;

    @Autowired
    public PsychoanalyticQuestionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 사용자의 글을 받아 정신분석학적 관점에서 질문을 생성합니다.
     * 이 메서드는 Ollama LLM에 프롬프트를 전송하고 응답을 파싱합니다.
     *
     * @param userText 사용자가 작성한 원본 글
     * @return Ollama 모델이 생성한 정신분석학적 질문
     */
    public String generateQuestion(String userText) {
        String systemMessage = """
            당신은 경험이 풍부하고 통찰력 있는 정신분석학자입니다.
            주어진 사용자의 글을 깊이 있게 이해하고, 정신분석학적 관점(예: 무의식, 방어 기제, 유년기 경험, 꿈, 관계 패턴, 내면의 갈등, 숨겨진 동기, 투사 등)에서 분석해 주세요.
            분석을 바탕으로 사용자에게 스스로의 감정, 생각, 경험, 행동의 근원을 탐색하도록 유도하는 질문을
            단 하나만, 간결하고 현학적이지 않으며, 실질적인 언어로 생성해야 합니다.
            질문은 사용자의 글쓰기를 활성화하고, 자기 성찰을 돕는 데 초점을 맞춰야 합니다.
            명령:
            - 사용자의 글을 정신분석학적으로 해석.
            - 핵심적인 하나의 질문만 출력.
            - 질문은 간결하고 일상적인 언어 사용.
            - '무의식', '리비도' 등 전문 용어 사용 자제.
            - 예시: "그 감정은 어떤 기억과 연결되어 있을까요?", "이 상황에서 당신이 정말 원했던 것은 무엇인가요?", "그때 느꼈던 감정은 어떤 불안감에서 비롯된 걸까요?"
            """;

        String userMessageTemplate = """
            다음 사용자의 글을 읽고 질문을 생성해 주세요:
            사용자 글: {userText}
            """;

        PromptTemplate promptTemplate = new PromptTemplate(systemMessage + "\n\n" + userMessageTemplate);
        Prompt prompt = new Prompt(promptTemplate.createMessage(Map.of("userText", userText)));

        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

        // getText()가 올바른 메서드입니다.

        assert chatResponse != null;
        return chatResponse.getResults().get(0).getOutput().getText();
    }
}
