# FastAPI imports for building API routes and handling file uploads
from fastapi import APIRouter, UploadFile, File, HTTPException

# Service to handle PDF saving and text extraction
from services.pdf_service import PDFService

# Service to split large text into smaller chunks for embeddings
from services.chunk_service import ChunkService

# Service to generate vector embeddings using BAAI/BGE model
from services.embedding_service import EmbeddingService

# FAISS Vector Store service for storing and searching embeddings
from services.vector_store import VectorStore


# Create a FastAPI router for upload-related endpoints
router = APIRouter(
    prefix="/upload",   # all APIs will start with /upload
    tags=["Upload"]     # group name in Swagger UI
)

# ------------------------------
# INITIALIZATION (GLOBAL SCOPE)
# ------------------------------

# Create embedding service once (avoid reloading model every request)
embedding_service = EmbeddingService()

# Create FAISS vector store with default dimension (placeholder)
vector_store = VectorStore(dim=384)

# Try loading existing FAISS index from disk (if already created earlier)
try:
    vector_store.load()   # loads index.faiss + metadata
except Exception as e:
    # If no index exists (first run), start fresh
    print("No existing FAISS index, starting fresh")


# ------------------------------
# UPLOAD PDF ENDPOINT
# ------------------------------

@router.post("/")   # POST endpoint: /upload/
async def upload_pdf(file: UploadFile = File(...)):

    # Validate file type (only PDFs allowed)
    if not file.filename.lower().endswith(".pdf"):
        raise HTTPException(
            status_code=400,
            detail="Only PDF files are allowed."
        )

    # --------------------------
    # STEP 1: SAVE PDF LOCALLY
    # --------------------------
    path, stored_filename = PDFService.save_pdf(file)

    # --------------------------
    # STEP 2: EXTRACT TEXT
    # --------------------------
    pdf_info = PDFService.extract_text(path)

    # --------------------------
    # STEP 3: CHUNKING TEXT
    # Break long text into small overlapping pieces
    # --------------------------
    chunks = ChunkService.split_text(pdf_info["text"])

    # --------------------------
    # STEP 4: GENERATE EMBEDDINGS
    # Convert text chunks → vector embeddings
    # --------------------------
    embeddings = embedding_service.generate_embeddings(chunks)

    # --------------------------
    # STEP 5: HANDLE FIRST RUN CASE
    # If FAISS index is empty, reinitialize with correct dimension
    # --------------------------
    # Initialize FAISS only if index is not created yet
    if vector_store.index is None:
        vector_store.dim = embeddings.shape[1]
        vector_store.index = vector_store._create_index()

    # --------------------------
    # STEP 6: ADD TO FAISS INDEX
    # Store embeddings + metadata in vector database
    # --------------------------
    vector_store.add(
        embeddings,
        chunks,
        source_file=stored_filename   # track which PDF this came from
    )

    # --------------------------
    # STEP 7: SAVE INDEX TO DISK
    # Persist FAISS index for future use
    # --------------------------
    vector_store.save()

    # --------------------------
    # RESPONSE BACK TO CLIENT
    # --------------------------
    return {
        "status": "success",
        "original_filename": file.filename,
        "stored_filename": stored_filename,
        "pages": pdf_info["pages"],
        "characters": pdf_info["characters"],
        "chunks_added": len(chunks),
        "total_chunks_in_db": len(vector_store.metadata),
        "embedding_dimension": embeddings.shape[1],
        "message": "PDF added to FAISS successfully"
    }