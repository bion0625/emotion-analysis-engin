import re
import random
import logging
import math
from itertools import combinations
from collections import Counter, defaultdict
from kiwipiepy import Kiwi
from konlpy.tag import Okt
import stopwordsiso as stopwords
from pyjosa.josa import Josa

# 로거 설정
logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.INFO)

# 설정값
PREFIXES = ["텍스트에서", "당신의 관점에서", "문맥 속에서", "경험에 비추어"]
MIN_FREQ = 2         # 최소 빈도
DYN_RATIO = 0.8      # 문장 중 80% 이상 등장 시 동적 불용어

class NlpService:
    def __init__(self):
        self.kiwi = Kiwi()
        self.okt = Okt()                  # 명사 검사용
        self.stopwords = stopwords.stopwords("ko")

    def _is_valid_noun(self, word: str) -> bool:
        # Okt로 명사 여부 판단 (부사·기타 제외)
        return any(pos == "Noun" for _, pos in self.okt.pos(word))

    def _get_dynamic_stopwords(self, text: str) -> set[str]:
        sentences = [s for s in re.split(r"[.?!]\s*", text.strip()) if s]
        doc_counts = Counter()
        for s in sentences:
            tokens = self.kiwi.tokenize(s)
            # 모든 토큰 집계
            words = {token.form for token in tokens if len(token.form) > 1}
            doc_counts.update(words)
        threshold = DYN_RATIO * len(sentences)
        return {w for w, cnt in doc_counts.items() if cnt >= threshold}

    def generate_question(self, input_text: str) -> str:
        sentences = [s for s in re.split(r"[.?!]\s*", input_text.strip()) if s]
        dynamic_sw = self._get_dynamic_stopwords(input_text)

        # 문서 내 명사별 문장 출현수 (IDF용)
        doc_counts_nouns = Counter()
        # 전체 토큰 필터링 및 카운팅
        word_freq = Counter()
        pair_counts = defaultdict(int)

        for s in sentences:
            # Okt로 문장 내 명사 추출 및 IDF 집계
            nouns_in_sent = set(self.okt.nouns(s))
            doc_counts_nouns.update(nouns_in_sent)

            # Kiwi 토큰화 후, Okt 명사 교차 필터링
            tokens = self.kiwi.tokenize(s)
            filtered = [
                t.form for t in tokens
                if t.tag == "NNG"
                   and t.form in nouns_in_sent
                   and t.form not in self.stopwords
                   and t.form not in dynamic_sw
                   and self._is_valid_noun(t.form)
                   and len(t.form) > 1
            ]
            unique = set(filtered)
            word_freq.update(unique)
            for w1, w2 in combinations(sorted(unique), 2):
                pair_counts[(w1, w2)] += 1

        # 최소 빈도 필터링
        freq_filtered = {w: cnt for w, cnt in word_freq.items() if cnt >= MIN_FREQ}
        if len(freq_filtered) < 2:
            return "조금 더 명확한 단어나 문장을 사용해주실 수 있을까요?"

        # 상위 단어 추출
        top_words = sorted(freq_filtered, key=freq_filtered.get, reverse=True)[:5]

        # co-occurrence 기반 후보
        positive = [(p, pair_counts[p]) for p in combinations(sorted(top_words), 2) if pair_counts[p] > 0]
        candidates = [p for p, _ in positive] if positive else [tuple(top_words[:2])]

        # 명사 검증
        valid_candidates = [p for p in candidates if self._is_valid_noun(p[0]) and self._is_valid_noun(p[1])]
        if valid_candidates:
            candidates = valid_candidates

        # IDF 가중치로 최적 쌍 선택
        idf = {w: math.log(1 + len(sentences) / cnt) for w, cnt in doc_counts_nouns.items()}
        scored = [(sum(idf.get(w, 0) for w in pair), pair) for pair in candidates]
        _, best_pair = max(scored, key=lambda x: x[0])
        w1, w2 = best_pair

        # 조사 처리 및 질문 생성
        first = Josa.get_full_string(w1, "과")
        second = Josa.get_full_string(w2, "은")
        prefix = random.choice(PREFIXES)
        question = f"{prefix} {first} {second} 어떤 관련이 있을까요?"
        logger.info(f"Generated question: {question}")
        return question