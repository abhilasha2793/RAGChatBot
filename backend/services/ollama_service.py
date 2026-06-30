"""
Ollama Service

Responsible for:
1. Loading the RAG prompt template
2. Sending prompts to Ollama
3. Returning the generated response
"""

import os
import requests

import config


class OllamaService:
    """
    Service class for communicating with the Ollama API.
    """

    def __init__(self):
        self.base_url = config.OLLAMA_BASE_URL
        self.model = config.MODEL_NAME
        self.timeout = config.OLLAMA_TIMEOUT

    def load_prompt(self):
        """
        Load prompt template from prompts folder.
        """

        prompt_path = os.path.join(
            "prompts",
            "rag_prompt.txt"
        )

        with open(
            prompt_path,
            "r",
            encoding="utf-8"
        ) as file:
            return file.read()

    def generate_answer(
        self,
        context: str,
        question: str
    ):
        """
        Generate answer using Ollama.

        Args:
            context: Retrieved document context.
            question: User question.

        Returns:
            AI generated answer.
        """

        prompt_template = self.load_prompt()

        final_prompt = prompt_template.format(
            context=context,
            question=question
        )

        payload = {
            "model": self.model,
            "prompt": final_prompt,
            "stream": False
        }

        response = requests.post(
            url=f"{self.base_url}/api/generate",
            json=payload,
            timeout=self.timeout
        )

        response.raise_for_status()

        result = response.json()

        return result.get("response", "").strip()