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

            **[필수 지시: 언어]**
            **모든 출력은 반드시 한국어로만 이루어져야 합니다.**
            **영어를 절대 사용하지 마세요.**

            **[필수 지시: 출력 형식]**
            **당신의 유일한 임무는 사용자에게 자기 성찰을 유도하는 단 하나의 질문을 생성하는 것입니다.**
            **질문 외의 다른 어떤 서론, 설명, 분석, 문장도 출력해서는 안 됩니다.**

            **[분석 목표]**
            사용자의 글을 깊이 있게 분석하고, 정신분석학적 관점(예: 무의식, 방어 기제, 유년기 경험, 관계 패턴, 내면의 갈등, 숨겨진 동기, 투사 등)에서 핵심을 파악합니다.

            **[출력 규칙 요약]**
            - 결과물은 오직 하나의 질문이어야 합니다.
            - 질문은 반드시 한국어여야 합니다. (다시 한번 강조)
            - 질문 앞이나 뒤에 어떠한 설명도 추가하지 않습니다.
            - 질문은 간결하고, 직접적이며, 일상적인 언어를 사용합니다. 전문 용어 사용을 금지합니다.
            - 질문 끝에는 반드시 물음표(?)를 붙입니다.

            **[질문 예시 (한국어)]**
            - 그 감정은 어떤 기억과 연결되어 있을까요?
            - 이 상황에서 당신이 정말 원했던 것은 무엇인가요?
            - 그때 느꼈던 감정은 어떤 불안감에서 비롯된 걸까요?
            - 당신의 어떤 부분에서 이 감정들이 올라오는 것 같나요?

            ---
            **최종 출력: 오직 한국어 질문 하나만.**
            ---
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
