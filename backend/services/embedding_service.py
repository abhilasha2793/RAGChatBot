"""
Embedding Service

Generates embeddings using the BAAI BGE model.
"""

import numpy as np
from sentence_transformers import SentenceTransformer
import config

class EmbeddingService:

    def __init__(self):
        self.model = SentenceTransformer(config.EMBEDDING_MODEL)

    def generate_embeddings(self, chunks):
        """
        Generate embeddings for a list of text chunks.
        """
        embeddings = self.model.encode(
            chunks,
            normalize_embeddings=True,
            show_progress_bar=False
        )

        return np.array(embeddings).astype("float32")
    

