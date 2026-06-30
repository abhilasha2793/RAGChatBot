package com.abhilasha.ragchatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhilasha.ragchatapp.data.model.ChatResponse
import com.abhilasha.ragchatapp.data.model.UploadResponse
import com.abhilasha.ragchatapp.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()

    // -------------------------------
    // Chat History
    // -------------------------------
    private val _messages = MutableStateFlow<List<com.abhilasha.ragchatapp.ui.chat.ChatMessage>>(emptyList())
    val messages: StateFlow<List<com.abhilasha.ragchatapp.ui.chat.ChatMessage>> = _messages

    // -------------------------------
    // Loading State
    // -------------------------------
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // -------------------------------
    // Upload Response
    // -------------------------------
    private val _uploadResponse =
        MutableStateFlow<UploadResponse?>(null)

    val uploadResponse: StateFlow<UploadResponse?> =
        _uploadResponse

    // -------------------------------
    // Chat Response
    // -------------------------------
    private val _chatResponse =
        MutableStateFlow<ChatResponse?>(null)

    val chatResponse: StateFlow<ChatResponse?> =
        _chatResponse

    // -------------------------------
    // Error
    // -------------------------------
    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error

    // -------------------------------
    // Upload PDF
    // -------------------------------
    fun uploadPdf(
        file: MultipartBody.Part
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val response =
                    repository.uploadPdf(file)

                if (response.isSuccessful) {

                    _uploadResponse.value =
                        response.body()

                } else {

                    _error.value =
                        response.errorBody()?.string()
                            ?: "Upload failed"

                }

            } catch (e: Exception) {

                _error.value =
                    e.localizedMessage ?: "Unknown Error"

            } finally {

                _isLoading.value = false

            }

        }

    }

    // -------------------------------
    // Ask Question
    // -------------------------------
    fun askQuestion(
        question: String
    ) {

        val userMessage = com.abhilasha.ragchatapp.ui.chat.ChatMessage(
            text = question,
            isUser = true
        )
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val response =
                    repository.askQuestion(question)

                if (response.isSuccessful) {

                    val chatResponse = response.body()
                    _chatResponse.value = chatResponse
                    
                    chatResponse?.let {
                        val assistantMessage = com.abhilasha.ragchatapp.ui.chat.ChatMessage(
                            text = it.answer,
                            isUser = false
                        )
                        _messages.value = _messages.value + assistantMessage
                    }

                } else {

                    _error.value =
                        response.errorBody()?.string()
                            ?: "Chat failed"

                }

            } catch (e: Exception) {

                _error.value =
                    e.localizedMessage ?: "Unknown Error"

            } finally {

                _isLoading.value = false

            }

        }

    }

    // -------------------------------
    // Clear Error
    // -------------------------------
    fun clearError() {

        _error.value = null

    }

}