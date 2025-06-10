package com.stxtory.semantic_llm;

public class UserMessageTemplates {
	private UserMessageTemplates() {
	}
	/**
	 * 사용자의 글을 받아 정신분석학적 관점에서 질문을 생성하기 위한 템플릿입니다.
	 */
	public static final String PSYCHOANALYTIC_MESSAGE = """
			다음 사용자의 글을 읽고 질문을 생성해 주세요:
			사용자 글: {userText}
			""";
}
