
from services.chunk_service import ChunkService
from services.embedding_service import EmbeddingService
from services.pdf_service import PDFService
from services.vector_store import VectorStore

import config


def run_ingestion(pdf_path):

    # 1. Extract text using YOUR existing service
    print("Reading PDF...")

    pdf_info = PDFService.extract_text(pdf_path)
    text = pdf_info["text"]

    # 2. Chunking (YOUR CHUNK SERVICE CONNECTED HERE)
    print("Chunking text...")
    chunks = ChunkService.split_text(text)

    print(f"Total chunks: {len(chunks)}")

    # 3. Embeddings
    print("Generating embeddings...")
    embedder = EmbeddingService()
    embeddings = embedder.generate_embeddings(chunks)

    # 4. Vector Store (FAISS)
    print("Creating FAISS index...")
    dim = embeddings.shape[1]

    vector_store = VectorStore(dim)
    vector_store.build_index(embeddings, chunks)

    # 5. Save index
    print("Saving index...")
    vector_store.save()

    print("Ingestion completed successfully!")


if __name__ == "__main__":
    run_ingestion("data/pdfs/sample.pdf")