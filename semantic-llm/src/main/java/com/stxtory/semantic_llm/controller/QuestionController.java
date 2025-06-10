package com.stxtory.semantic_llm.controller;

import com.stxtory.semantic_llm.model.QuestionDto;
import com.stxtory.semantic_llm.service.PsychoanalyticQuestionService; // 서비스 클래스 임포트
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity; // HTTP 응답을 위한 ResponseEntity 임포트
import org.springframework.web.bind.annotation.*; // 웹 관련 어노테이션 임포트

@Slf4j
// 이 클래스가 RESTful API를 제공하는 컨트롤러임을 나타냅니다.
@RestController
// 이 컨트롤러의 모든 엔드포인트에 대한 기본 경로를 설정합니다.
@RequestMapping("/api/question")
public class QuestionController {
	// 정신분석학적 질문 생성 서비스를 주입받기 위한 필드
	private final PsychoanalyticQuestionService questionService;

	// 생성자 주입을 통해 PsychoanalyticQuestionService 인스턴스를 주입받습니다.
	// @Autowired 어노테이션은 생략 가능합니다 (Spring 4.3+).
	public QuestionController(PsychoanalyticQuestionService questionService) {
		this.questionService = questionService;
	}

	/**
	 * 사용자 글을 받아 정신분석학적 관점의 질문을 생성하는 API 엔드포인트. HTTP POST 요청의 본문(body)에 사용자 글을 담아 보냅니다.
	 *
	 * @param dto
	 *            HTTP 요청 본문에 담긴 사용자 글 (RequestBody 어노테이션으로 매핑)
	 * @return 생성된 질문이 담긴 HTTP 응답 (ResponseEntity로 감싸서 반환)
	 */
	@PostMapping("/generate") // HTTP POST 요청을 '/api/question/generate' 경로로 매핑합니다.
	public ResponseEntity<String> generateQuestion(@RequestBody QuestionDto dto) {
		// 사용자 글이 비어있거나 null인 경우, Bad Request 응답을 반환합니다.
		if (dto == null || dto.userText() == null || dto.userText().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("사용자 글이 비어있습니다. 글을 작성해 주세요.");
		}

		// 수신된 사용자 텍스트를 콘솔에 출력 (디버깅 목적)
		log.info("Received user text: " + dto.userText());

		// PsychoanalyticQuestionService를 호출하여 질문을 생성합니다.
		String generatedQuestion = questionService.generateQuestion(dto.userText());

		// 생성된 질문을 콘솔에 출력 (디버깅 목적)
		log.info("Generated question: " + generatedQuestion);

		// 생성된 질문을 HTTP 200 OK 상태 코드와 함께 응답 본문에 담아 반환합니다.
		return ResponseEntity.ok(generatedQuestion);
	}

	// --- CORS 설정 예시 (선택 사항) ---
	// 만약 프론트엔드 애플리케이션이 이 백엔드와 다른 도메인/포트에서 실행된다면,
	// 브라우저의 Same-Origin Policy로 인해 CORS(Cross-Origin Resource Sharing) 문제가 발생할 수 있습니다.
	// 이 경우 아래 주석 처리된 WebConfig 클래스를 활성화하여 CORS를 허용할 수 있습니다.
	// 실제 배포 환경에서는 더 구체적인 CORS 설정을 권장합니다.
	/*
	 * import org.springframework.context.annotation.Configuration; import
	 * org.springframework.web.servlet.config.annotation.CorsRegistry; import
	 * org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
	 * 
	 * @Configuration public static class WebConfig implements WebMvcConfigurer {
	 * 
	 * @Override public void addCorsMappings(CorsRegistry registry) { registry.addMapping("/**") //
	 * 모든 경로에 대해 CORS 허용 규칙을 적용합니다. // 허용할 Origin(출처)을 지정합니다. 개발 환경에서는 localhost:3000(React/Vue 등)이
	 * 일반적입니다. .allowedOrigins("http://localhost:3000", "http://your-frontend-domain.com") // 허용할
	 * HTTP 메서드를 지정합니다. .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 모든 헤더를 허용합니다.
	 * .allowedHeaders("*") // 자격 증명(쿠키, HTTP 인증 등)을 허용합니다. .allowCredentials(true); } }
	 */
}
