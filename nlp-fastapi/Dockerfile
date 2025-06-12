# syntax=docker/dockerfile:1
FROM python:3.10-slim

# 시스템 의존성 설치: OpenJDK 17, 빌드 도구, SWIG
RUN apt-get update && apt-get install -y --no-install-recommends \
    openjdk-17-jdk-headless \
    build-essential \
    swig \
  && rm -rf /var/lib/apt/lists/*

# JAVA_HOME 환경 변수 설정
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"

WORKDIR /app

# 의존성 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 복사
COPY app ./app

# 포트 노출
EXPOSE 8000

# 기본 실행 커맨드
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]