package com.abhilasha.ragchatapp.data.repository

import com.abhilasha.ragchatapp.data.api.RetrofitClient
import com.abhilasha.ragchatapp.data.model.ChatRequest
import com.abhilasha.ragchatapp.data.model.ChatResponse
import com.abhilasha.ragchatapp.data.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response

class ChatRepository {

    /**
     * Upload PDF to backend
     */
    suspend fun uploadPdf(
        file: MultipartBody.Part
    ): Response<UploadResponse> {

        return RetrofitClient.apiService.uploadPdf(file)
    }

    /**
     * Send question to RAG backend
     */
    suspend fun askQuestion(
        question: String
    ): Response<ChatResponse> {

        val request = ChatRequest(
            query = question
        )

        return RetrofitClient.apiService.chat(request)
    }
}