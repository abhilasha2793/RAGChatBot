"""
PDF Service

Responsible for:
1. Saving uploaded PDFs
2. Extracting text
3. Returning PDF statistics
"""

import os
import uuid

from fastapi import UploadFile, HTTPException
from pypdf import PdfReader

from utils.logger import logger

UPLOAD_FOLDER = "data/pdfs"


class PDFService:

    @staticmethod
    def save_pdf(file: UploadFile):
        """
        Save uploaded PDF using a unique filename.
        """

        os.makedirs(UPLOAD_FOLDER, exist_ok=True)

        unique_filename = f"{uuid.uuid4()}.pdf"

        file_path = os.path.join(UPLOAD_FOLDER, unique_filename)

        try:
            with open(file_path, "wb") as buffer:
                buffer.write(file.file.read())

            logger.info(f"PDF saved successfully: {unique_filename}")

            return file_path, unique_filename

        except Exception as e:
            logger.error(f"Error saving PDF: {str(e)}")
            raise HTTPException(status_code=500, detail="Unable to save PDF.")

    @staticmethod
    def extract_text(file_path: str):
        """
        Extract text from a PDF file.
        """

        try:
            reader = PdfReader(file_path)

            full_text = ""

            for page in reader.pages:
                page_text = page.extract_text()

                if page_text:
                    full_text += page_text + "\n"

            logger.info("PDF text extracted successfully.")

            return {
                "pages": len(reader.pages),
                "characters": len(full_text),
                "text": full_text
            }

        except Exception as e:
            logger.error(f"Error reading PDF: {str(e)}")
            raise HTTPException(status_code=400, detail="Invalid or corrupted PDF.")