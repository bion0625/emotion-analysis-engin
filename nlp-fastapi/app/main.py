from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from fastapi.responses import FileResponse
from pydantic import BaseModel
from app.services.nlp_service import NlpService

app = FastAPI(
    title="NLP Question Generator",
    description="텍스트에서 의미망 기반 질문을 생성하는 FastAPI 서비스",
    version="0.1.0"
)

# 1) /static/* 에만 정적 파일 서빙
app.mount("/static", StaticFiles(directory="app/static"), name="static")

# 2) 루트 GET 은 index.html 반환
@app.get("/", response_class=FileResponse)
async def root():
    # uvicorn 실행 시 현재 작업 디렉터리에 맞춰 경로 지정
    return "app/static/index.html"

nlp_service = NlpService()

class TextInput(BaseModel):
    text: str

# 3) POST /question 은 API 로직 실행
@app.post("/question")
async def generate_question(input: TextInput):
    return {"question": nlp_service.generate_question(input.text)}
