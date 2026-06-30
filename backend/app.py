"""
Main entry point of the application.

This file creates the FastAPI application,
registers APIs, and starts the web server.
"""

from fastapi import FastAPI
import uvicorn
import config
from routes.upload import router as upload_router
from routes.chat import router as chat_router

from services.embedding_service import EmbeddingService


# Init service once
embedding_service = EmbeddingService()

app = FastAPI(
    title=config.APP_NAME,
    version=config.APP_VERSION,
    description="RAG Chatbot Backend using FastAPI, Embeddings, and Ollama"
)

# attach to app state
app.state.embedding_service = embedding_service

app.include_router(upload_router)
app.include_router(chat_router)

@app.get("/")
def home():
    """
    Root endpoint.
    """
    return {
        "message": "Welcome to GenAI RAG Chatbot",
        "version": config.APP_VERSION,
        "status": "Running"
    }


@app.get("/health")
def health():
    """
    Health check endpoint.
    """
    return {
        "status": "Healthy"
    }

@app.get("/embedding-test")
def test_embedding(text: str = "hello world"):

    vector = app.state.embedding_service.generate_embeddings([text])[0]

    return {
        "dimension": len(vector),
        "sample": vector[:5].tolist()
    }

if __name__ == "__main__":
    uvicorn.run(
        "app:app",
        host=config.HOST,
        port=config.PORT,
        reload=False
    )