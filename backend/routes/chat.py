"""
Chat API

Responsible for:
1. Receiving user questions
2. Retrieving relevant document chunks
3. Building context
4. Generating answers using Ollama
"""

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

import config

from services.retriever_service import RetrieverService
from services.ollama_service import OllamaService

router = APIRouter(
    prefix="/chat",
    tags=["Chat"]
)


# ---------------------------------------
# Request Model
# ---------------------------------------
class ChatRequest(BaseModel):
    query: str


# ---------------------------------------
# Response Model
# ---------------------------------------
class Source(BaseModel):
    chunk_id: int
    file: str
    page: int | None
    preview: str


class ChatResponse(BaseModel):
    status: str
    query: str
    answer: str
    retrieved_chunks: int
    model: str
    sources: list[Source]


# ---------------------------------------
# Initialize Services
# ---------------------------------------
retriever = RetrieverService()
ollama = OllamaService()


# ---------------------------------------
# Chat Endpoint
# ---------------------------------------
@router.post("/", response_model=ChatResponse)
async def chat(request: ChatRequest):

    query = request.query.strip()

    if not query:
        raise HTTPException(
            status_code=400,
            detail="Query cannot be empty."
        )

    try:

        # ---------------------------------------
        # Retrieve Similar Chunks
        # ---------------------------------------
        chunks = retriever.retrieve(
            question=query,
            top_k=config.TOP_K
        )

        if not chunks:
            return ChatResponse(
                status="success",
                query=query,
                answer="No relevant information found in uploaded documents.",
                retrieved_chunks=0,
                model=config.MODEL_NAME,
                sources=[]
            )

        # ---------------------------------------
        # Build Context
        # ---------------------------------------
        context = retriever.build_context(chunks)

        # ---------------------------------------
        # Generate AI Answer
        # ---------------------------------------
        answer = ollama.generate_answer(
            context=context,
            question=query
        )

        # ---------------------------------------
        # Build Sources
        # ---------------------------------------
        sources = []

        for chunk in chunks:

            sources.append(
                Source(
                    chunk_id=chunk.id,
                    file=chunk.source_file,
                    page=chunk.page_number,
                    preview=chunk.text[:200]
                )
            )

        # ---------------------------------------
        # Return Response
        # ---------------------------------------
        return ChatResponse(
            status="success",
            query=query,
            answer=answer,
            retrieved_chunks=len(chunks),
            model=config.MODEL_NAME,
            sources=sources
        )

    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=str(e)
        )