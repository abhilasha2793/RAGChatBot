"""
RAG Service (Production Grade)

Handles:
- Query embedding
- FAISS retrieval
- Chat history (last 4 turns)
- Prompt building
- Ollama LLM call
- Android-ready response
"""

import requests

from services.embedding_service import EmbeddingService
from services.vector_store_instance import vector_store
from services.chat_memory import ChatMemory


class RAGService:

    def __init__(self):

        self.embedder = EmbeddingService()
        self.memory = ChatMemory(max_history=4)

        # Ollama config
        self.ollama_url = "http://localhost:11434/api/generate"
        self.model = "llama3"

    # -------------------------
    # MAIN ENTRY POINT
    # -------------------------
    def answer(self, query: str, session_id: str = "default", top_k: int = 5):

        # 1. Save user message
        self.memory.add(session_id, "user", query)

        # 2. Convert query → embedding
        query_embedding = self.embedder.generate_embeddings([query])[0]

        # 3. FAISS search
        scores, indices = vector_store.index.search(
            query_embedding.reshape(1, -1).astype("float32"),
            top_k
        )

        # 4. Retrieve context chunks + citations
        context_chunks = []
        sources = []

        for idx in indices[0]:
            if idx == -1:
                continue

            if idx >= len(vector_store.metadata):
                continue

            chunk = vector_store.metadata[idx]

            context_chunks.append(chunk.text)

            sources.append({
                "chunk_id": chunk.id,
                "file": chunk.source_file,
                "page": chunk.page_number
            })

        context = "\n\n".join(context_chunks)

        # 5. Get chat history (last 4)
        history = self.memory.get(session_id)

        history_text = "\n".join(
            [f"{h['role']}: {h['content']}" for h in history]
        )

        # 6. Build prompt (RAG + Memory)
        prompt = self._build_prompt(query, context, history_text)

        # 7. Call LLM
        llm_response = self._call_llm(prompt)

        # 8. Save assistant response
        self.memory.add(session_id, "assistant", llm_response)

        # 9. Return Android-ready response
        return {
            "status": "success",
            "query": query,
            "answer": llm_response,
            "sources": sources,
            "chat_history": history,
            "retrieved_chunks": len(context_chunks)
        }

    # -------------------------
    # PROMPT ENGINEERING
    # -------------------------
    def _build_prompt(self, query: str, context: str, history: str):

        return f"""
You are a helpful AI assistant.

RULES:
- Use ONLY context + chat history
- If answer not found, say "Not found in documents"
- Be concise and accurate

CHAT HISTORY (last 4 turns):
{history}

DOCUMENT CONTEXT:
{context}

QUESTION:
{query}

ANSWER:
""".strip()

    # -------------------------
    # OLLAMA CALL
    # -------------------------
    def _call_llm(self, prompt: str):

        try:
            response = requests.post(
                self.ollama_url,
                json={
                    "model": self.model,
                    "prompt": prompt,
                    "stream": False
                },
                timeout=60
            )

            response.raise_for_status()

            return response.json().get("response", "")

        except Exception as e:
            return f"LLM call failed: {str(e)}"