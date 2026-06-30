"""
Retriever Service

Responsible for:
1. Generating embeddings for user queries
2. Loading the FAISS vector database
3. Retrieving similar document chunks
4. Building context for the LLM
"""

import config

from services.embedding_service import EmbeddingService
from services.vector_store import VectorStore


class RetrieverService:

    def __init__(self):
        """
        Initialize embedding model and FAISS vector store.
        """

        self.embedding_service = EmbeddingService()

        # BGE-small-en-v1.5 embedding dimension = 384
        self.vector_store = VectorStore(
            dim=384,
            index_path=config.VECTOR_STORE
        )

        self.vector_store.load()

    def retrieve(self, question: str, top_k: int | None = None):
        """
        Retrieve top matching document chunks.

        Args:
            question: User question
            top_k: Number of chunks to retrieve

        Returns:
            List[DocumentChunk]
        """

        if top_k is None:
            top_k = config.TOP_K

        # Generate query embedding
        query_embedding = self.embedding_service.generate_embeddings(
            [question]
        )[0]

        # Search FAISS
        return self.vector_store.search(
            query_embedding=query_embedding,
            top_k=top_k
        )

    def build_context(self, chunks):
        """
        Build context string from retrieved chunks.

        Args:
            chunks: List[DocumentChunk]

        Returns:
            str
        """

        if not chunks:
            return ""

        context_parts = []

        for chunk in chunks:

            context_parts.append(
                f"""Source: {chunk.source_file}
Page: {chunk.page_number}

{chunk.text}
----------------------------------------"""
            )

        return "\n\n".join(context_parts)