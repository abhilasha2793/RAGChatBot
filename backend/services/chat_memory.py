"""
Chat Memory Service
Stores last N chat messages per session (default = 4)
Used for conversational RAG context
"""

from collections import defaultdict, deque


class ChatMemory:

    def __init__(self, max_history: int = 4):
        # session_id → deque of messages
        self.store = defaultdict(lambda: deque(maxlen=max_history))

    # -------------------------
    # Add message to history
    # -------------------------
    def add(self, session_id: str, role: str, content: str):
        """
        role: 'user' or 'assistant'
        """
        self.store[session_id].append({
            "role": role,
            "content": content
        })

    # -------------------------
    # Get last N messages
    # -------------------------
    def get(self, session_id: str):
        return list(self.store[session_id])

    # -------------------------
    # Clear session history
    # -------------------------
    def clear(self, session_id: str):
        self.store[session_id].clear()