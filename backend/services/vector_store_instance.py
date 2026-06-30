"""
Singleton instance of VectorStore
This ensures FAISS index + metadata are shared across all APIs
"""

from services.vector_store import VectorStore

# -----------------------------
# GLOBAL SINGLETON INSTANCE
# -----------------------------
vector_store = VectorStore(dim=384)

# -----------------------------
# LOAD EXISTING INDEX (IF ANY)
# -----------------------------
try:
    vector_store.load()
    print("✅ VectorStore loaded successfully")
except Exception as e:
    print("⚠️ No existing FAISS index, starting fresh")
    print(str(e))