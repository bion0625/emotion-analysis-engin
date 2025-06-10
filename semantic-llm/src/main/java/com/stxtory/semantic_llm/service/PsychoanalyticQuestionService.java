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
public class PsychoanalyticQuestionService {
	private final ChatClient chatClient;

	@Autowired
	public PsychoanalyticQuestionService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	/**
	 * 사용자의 글을 받아 정신분석학적 관점에서 질문을 생성합니다. 이 메서드는 Ollama LLM에 프롬프트를 전송하고 응답을 파싱합니다.
	 *
	 * @param userText
	 *            사용자가 작성한 원본 글
	 * @return Ollama 모델이 생성한 정신분석학적 질문
	 */
	public String generateQuestion(String userText) {
		String systemMessage = """
				   당신은 섬세하고 통찰력 있는 정신분석가입니다.

				   당신의 임무는, 사용자의 글을 바탕으로 정서적 흐름과 무의식적 갈등을 포착하여,
				   그 사람이 자기 감정을 깊이 바라보고 성찰할 수 있도록 돕는 **단 하나의 질문**을 제시하는 것입니다.

				   ━━━━━━━━━━━━━━━
				   [출력 언어 지침]
				   ━━━━━━━━━━━━━━━
				   - 출력은 자연스럽고 따뜻한 한국어로 작성되어야 합니다.
				   - **영어, 한자, 외래어, 전문 용어**는 사용하지 않습니다.
				   - 너무 딱딱하거나 진단적인 문장은 피하고, **부드럽고 따뜻한 어조**를 사용합니다.

				   ━━━━━━━━━━━━━━━
				   [출력 형식 지침]
				   ━━━━━━━━━━━━━━━
				   - 출력은 **오직 질문 하나만** 포함해야 합니다.
				   - 설명, 해석, 인사말 등은 포함하지 않고, 질문만 작성합니다.
				   - 질문은 자연스럽고 일상적인 말투로, 마치 조심스럽게 대화를 이어가듯 표현합니다.
				   - 경우에 따라 "혹시 ~일까요?", "어쩌면 ~일 수도 있을까요?" 같은 부드러운 질문형도 환영합니다.
				   - **불완전하거나 어색한 질문형**(예: "이 감정은 어디까지 간 적 없나요?")은 피합니다.
				   - 질문은 문장 끝에 물음표(?)로 마무리합니다.
				   - 질문은 가능한 한 “혹시 ~일까요?”, “어쩌면 ~일 수도 있을까요?” 형식의 **완곡하고 따뜻한 질문형**으로 작성합니다.
				   - 예시는 반드시 참고하고, 가능한 유사한 말투로 질문을 생성합니다.

				   ━━━━━━━━━━━━━━━
				   [질문 예시]
				   ━━━━━━━━━━━━━━━
				   - 지금 이 감정은 어디서부터 시작된 것 같나요?
				   - 이 상황이 당신에게 익숙하게 느껴지는 이유는 무엇인가요?
				   - 그 무력감은 과거의 어떤 기억과 닮아 있나요?
				   - 당신은 지금 어떤 기대를 하고 있었던 걸까요?
				   - 감정을 억누르지 않고 들여다본다면, 어떤 말이 나올 것 같나요?
				   - 그 감정은 당신에게 어떤 진실을 말하고 싶은 걸까요?

				   ━━━━━━━━━━━━━━━
				   [마지막 지시]
				   ━━━━━━━━━━━━━━━
				   **질문 하나만 출력하세요. 그 외 어떤 설명도 금지됩니다.**

				""";

		String userMessageTemplate = """
				다음 사용자의 글을 읽고 질문을 생성해 주세요:
				사용자 글: {userText}
				""";

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
