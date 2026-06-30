"""
Chunk Service

Responsible for splitting extracted text into
smaller overlapping chunks for vector embeddings.
"""

from langchain_text_splitters import RecursiveCharacterTextSplitter

import config


class ChunkService:

    @staticmethod
    def split_text(text: str):
        """
        Split text into chunks.
        """

        splitter = RecursiveCharacterTextSplitter(
            chunk_size=config.CHUNK_SIZE,
            chunk_overlap=config.CHUNK_OVERLAP,
            separators=[
                "\n\n",
                "\n",
                ". ",
                " ",
                ""
            ]
        )

        chunks = splitter.split_text(text)

        return chunks