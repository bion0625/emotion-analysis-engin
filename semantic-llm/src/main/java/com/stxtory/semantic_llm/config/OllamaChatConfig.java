package com.stxtory.semantic_llm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel; // ChatModel 인터페이스 임포트

// 이 클래스가 Spring의 설정 클래스임을 나타냅니다.
@Configuration
public class OllamaChatConfig {
    // application.yml의 Ollama 설정 값들은 Spring AI 스타터가 자동으로 읽어들여 ChatModel을 구성합니다.
    // 따라서 여기서는 ChatClient 생성에 직접 사용하지 않아도 됩니다. (단, 필요하다면 사용할 수 있습니다.)
    // @Value("${spring.ai.ollama.chat.base-url}")
    // private String ollamaBaseUrl;
    // @Value("${spring.ai.ollama.chat.model}")
    // private String ollamaModelId;


    /**
     * Spring AI의 ChatClient 빈을 생성하여 등록합니다.
     * `spring-ai-starter-model-ollama` 의존성이 `ChatModel` (OllamaChatModel 구현체) 빈을
     * 자동으로 생성하여 Spring 컨텍스트에 등록합니다.
     * 우리는 그 자동 구성된 `ChatModel` 빈을 여기에 주입받아 `ChatClient`를 만듭니다.
     *
     * @param chatModel Spring AI 스타터가 자동 구성한 ChatModel 빈 (OllamaChatModel)
     * @return Ollama와 통신할 수 있는 ChatClient 인스턴스
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        // ChatClient.builder()에 스타터가 제공하는 ChatModel 인스턴스를 전달합니다.
        // 이렇게 하면 application.yml에 설정된 base-url 및 model이 이미 ChatModel에 적용됩니다.
        return ChatClient.builder(chatModel).build();
    }
}
