package com.abhilasha.ragchatapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhilasha.ragchatapp.data.model.ChatResponse
import com.abhilasha.ragchatapp.data.model.UploadResponse
import com.abhilasha.ragchatapp.data.repository.RagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import com.abhilasha.ragchatapp.data.model.DocumentResponse
import com.abhilasha.ragchatapp.data.model.StatusResponse
import kotlinx.coroutines.delay

class ChatViewModel : ViewModel() {

    private val repository = RagRepository()

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
    // Documents List
    // -------------------------------
    private val _documents =
        MutableStateFlow<List<DocumentResponse>>(emptyList())

    val documents: StateFlow<List<DocumentResponse>> =
        _documents


    // -------------------------------
    // Current Processing Status
    // -------------------------------
    private val _documentStatus =
        MutableStateFlow<StatusResponse?>(null)

    val documentStatus: StateFlow<StatusResponse?> =
        _documentStatus

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
                Log.d("ChatViewModel", "Uploading PDF...")
                val response =
                    repository.uploadPdf(file)

                if (response.isSuccessful) {

                    val upload = response.body()
                    Log.d("ChatViewModel", "Upload success: ${upload?.stored_filename}")

                    _uploadResponse.value = upload

                    upload?.stored_filename?.let {

                        pollDocumentStatus(it)

                    }

                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Upload failed"
                    Log.e("ChatViewModel", "Upload error: $errorMsg")
                    _error.value = errorMsg

                }

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Upload exception: ${e.message}", e)
                _error.value =
                    e.localizedMessage ?: "Unknown Error"

            } finally {

                _isLoading.value = false

            }

        }

    }
     fun pollDocumentStatus(
        filename: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            while (true) {

                try {
                    Log.d("ChatViewModel", "Polling status for $filename...")
                    val response = repository.getDocumentStatus(filename)

                    if (response.isSuccessful) {

                        val status = response.body()
                        Log.d("ChatViewModel", "Status update: ${status?.status}")

                        _documentStatus.value = status

                        when (status?.status?.uppercase()) {

                            "READY" -> {
                                Log.d("ChatViewModel", "Document READY")
                                _isLoading.value = false
                                break
                            }

                            "FAILED" -> {
                                Log.e("ChatViewModel", "Document processing FAILED")
                                _isLoading.value = false
                                _error.value = "Document processing failed."
                                break
                            }

                            else -> {
                                // Still processing...
                            }
                        }

                    } else {
                        Log.e("ChatViewModel", "Failed to get document status: ${response.code()}")
                        _isLoading.value = false
                        _error.value = "Unable to fetch document status."
                        break
                    }

                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Poll status exception: ${e.message}", e)
                    _isLoading.value = false
                    _error.value = e.localizedMessage ?: "Status check failed."
                    break
                }

                delay(2000)
            }
        }
    }

    fun loadDocuments() {

        viewModelScope.launch {

            try {
                Log.d("ChatViewModel", "Loading documents...")
                val response =
                    repository.getDocuments()

                if (response.isSuccessful) {
                    Log.d("ChatViewModel", "Documents loaded: ${response.body()?.size}")
                    _documents.value =
                        response.body().orEmpty()

                }

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Load documents exception: ${e.message}", e)
            }

        }

    }

    // -------------------------------
    // Ask Question
    // -------------------------------
    fun askQuestion(
        question: String
    ) {
        Log.d("ChatViewModel", "askQuestion: $question")

        // Add user message
        val userMessage = com.abhilasha.ragchatapp.ui.chat.ChatMessage(
            text = question,
            isUser = true
        )

        _messages.value = _messages.value + userMessage

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {
                Log.d("ChatViewModel", "Fetching answer from repository...")
                val response = repository.askQuestion(
                    question = question
                )

                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    Log.d("ChatViewModel", "Chat success: ${chatResponse?.answer}")

                    _chatResponse.value = chatResponse

                    chatResponse?.let {

                        val assistantMessage =
                            com.abhilasha.ragchatapp.ui.chat.ChatMessage(
                                text = it.answer,
                                isUser = false
                            )

                        _messages.value =
                            _messages.value + assistantMessage
                    }

                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Chat failed"
                    Log.e("ChatViewModel", "Chat error: $errorMsg")
                    _error.value = errorMsg

                }

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Chat exception: ${e.message}", e)
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



    init {

        loadDocuments()

    }

    fun clearUploadResponse() {

        _uploadResponse.value = null

    }


    private val _selectedDocument =
        MutableStateFlow<String?>(null)

    val selectedDocument: StateFlow<String?> =
        _selectedDocument

    fun selectDocument(filename: String) {
        _selectedDocument.value = filename
    }

}