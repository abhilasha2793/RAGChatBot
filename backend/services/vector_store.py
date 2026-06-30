import faiss
import numpy as np
import pickle
import os
import config
from utils.logger import logger
from models.document_store import DocumentChunk



class VectorStore:

    def __init__(self, dim, index_path=config.VECTOR_STORE):
        self.dim = dim
        self.index_path = index_path

        self.index = faiss.IndexFlatIP(dim)
        self.metadata = []

    # -------------------------
    # ADD DATA
    # -------------------------
    def add(self, embeddings, chunks, source_file="unknown", pages=None):

        embeddings = np.array(embeddings).astype("float32")

        if embeddings.shape[1] != self.dim:
            raise ValueError(
                f"Embedding dim mismatch: expected {self.dim}, got {embeddings.shape[1]}"
            )

        start_id = len(self.metadata)

        for i, text in enumerate(chunks):

            chunk = DocumentChunk(
                id=start_id + i,
                text=text,
                source_file=source_file,
                page_number=pages[i] if pages else None
            )

            self.metadata.append(chunk)

        self.index.add(embeddings)

    # -------------------------
    # SEARCH
    # -------------------------
    def search(self, query_embedding, top_k=5):

        if self.index.ntotal == 0:
            return []

        query_embedding = np.array([query_embedding]).astype("float32")

        scores, indices = self.index.search(query_embedding, top_k)

        results = []

        for idx in indices[0]:
            if 0 <= idx < len(self.metadata):
                results.append(self.metadata[idx])

        return results

    # -------------------------
    # SAVE
    # -------------------------
    def save(self):

        os.makedirs(self.index_path, exist_ok=True)

        faiss.write_index(
            self.index,
            os.path.join(self.index_path, "index.faiss")
        )

        with open(os.path.join(self.index_path, "meta.pkl"), "wb") as f:
            pickle.dump(self.metadata, f)

    # -------------------------
    # LOAD (FIXED)
    # -------------------------
    def load(self):

        index_file = os.path.join(self.index_path, "index.faiss")
        meta_file = os.path.join(self.index_path, "meta.pkl")

        if not os.path.exists(index_file) or not os.path.exists(meta_file):
            logger.info("⚠️ No FAISS index found. Creating new empty index.")
            self.index = faiss.IndexFlatIP(self.dim)
            self.metadata = []
            return

        try:
            self.index = faiss.read_index(index_file)

            # 🚨 validate dimension match
            if self.index.d != self.dim:
                logger.info("⚠️ Dimension mismatch detected. Rebuilding index.")
                self.index = faiss.IndexFlatIP(self.dim)
                self.metadata = []
                return

            with open(meta_file, "rb") as f:
                self.metadata = pickle.load(f)

            logger.info("✅ FAISS index loaded successfully")

        except Exception as e:
            logger.info(f"❌ Failed to load FAISS index: {e}")
            self.index = faiss.IndexFlatIP(self.dim)
            self.metadata = []