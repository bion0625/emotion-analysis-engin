package com.stxtory.semantic_llm;

// todo 추후 DB로 수정
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

	/**
	 * 다음 종목들의 가격 정보를 이해해서 추천 종목을 알려주고, 그 이유에 대한 분석 내용을 돌려받기 위한 템플릿.
	 */
	public static final String STOCK_INFO = """
			다음은 여러 종목들의 최근 가격 데이터야:
			{userText}

			이 데이터를 바탕으로 다음 질문을 참고해서 종목 추천과 분석을 해줘.
			""";
}
