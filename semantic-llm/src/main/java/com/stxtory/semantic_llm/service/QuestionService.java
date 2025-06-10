package com.stxtory.semantic_llm.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

// 이 클래스가 Spring의 서비스 컴포넌트임을 나타냅니다.
@Service
public class QuestionService {
	private final ChatClient chatClient;

	@Autowired
	public QuestionService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	/**
	 * 이 메서드는 Ollama LLM에 프롬프트를 전송하고 응답을 파싱합니다.
	 *
	 * @param userText
	 *            사용자가 작성한 원본 글
	 * @return Ollama 모델이 생성한 답변
	 */
	public String generateQuestion(String userText,String systemMessage,
			String userMessageTemplate) {

		PromptTemplate promptTemplate = new PromptTemplate(
				systemMessage + "\n\n" + userMessageTemplate);
		Prompt prompt = new Prompt(promptTemplate.createMessage(Map.of("userText",userText)));

		ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

		return Optional.ofNullable(chatResponse).map(ChatResponse::getResults)
				.filter(data -> !data.isEmpty()).map(data -> data.get(0)).map(Generation::getOutput)
				.map(AbstractMessage::getText)
				.orElseThrow(() -> new IllegalStateException("LLM 응답이 유효하지 않음"));
	}
}
