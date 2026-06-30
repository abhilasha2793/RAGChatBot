from dataclasses import dataclass

@dataclass
class DocumentChunk:
    id: int
    text: str
    source_file: str
    page_number: int | None = None
    score: float | None = None