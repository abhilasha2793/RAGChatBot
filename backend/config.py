"""
Application configuration.

This file loads all configuration values from the .env file.
Keeping configuration separate from the source code makes the
application easier to maintain and deploy.
"""

import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# ------------------------------
# Application
# ------------------------------
APP_NAME = os.getenv("APP_NAME")
APP_VERSION = os.getenv("APP_VERSION")

# ------------------------------
# Server
# ------------------------------
HOST = os.getenv("HOST")
PORT = int(os.getenv("PORT"))

# ------------------------------
# Ollama
# ------------------------------
OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL")
MODEL_NAME = os.getenv("MODEL_NAME")
OLLAMA_TIMEOUT = int(os.getenv("OLLAMA_TIMEOUT", "120"))

# ------------------------------
# PDF Processing
# ------------------------------
CHUNK_SIZE = int(os.getenv("CHUNK_SIZE"))
CHUNK_OVERLAP = int(os.getenv("CHUNK_OVERLAP"))

# ------------------------------
# Storage
# ------------------------------
VECTOR_DB = os.getenv("VECTOR_DB")
DATA_FOLDER = os.getenv("DATA_FOLDER")
VECTOR_STORE = os.getenv("VECTOR_STORE")

# ------------------------------
# Embedding Model
# ------------------------------
EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL", "BAAI/bge-small-en-v1.5")

# ------------------------------
# Retrieval
# ------------------------------
TOP_K = int(os.getenv("TOP_K", "3"))

