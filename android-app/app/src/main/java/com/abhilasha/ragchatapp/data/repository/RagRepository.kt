package com.abhilasha.ragchatapp.data.repository

import com.abhilasha.ragchatapp.data.api.RetrofitClient
import com.abhilasha.ragchatapp.data.model.ChatRequest
import com.abhilasha.ragchatapp.data.model.ChatResponse
import com.abhilasha.ragchatapp.data.model.DocumentResponse
import com.abhilasha.ragchatapp.data.model.StatusResponse
import com.abhilasha.ragchatapp.data.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response

class RagRepository {

    /**
     * Upload PDF
     */
    suspend fun uploadPdf(
        file: MultipartBody.Part
    ): Response<UploadResponse> {

        return RetrofitClient.apiService.uploadPdf(file)
    }

    /**
     * Get processing status
     */
    suspend fun getDocumentStatus(
        filename: String
    ): Response<StatusResponse> {

        return RetrofitClient.apiService.getDocumentStatus(filename)
    }

    /**
     * Get all uploaded documents
     */
    suspend fun getDocuments(
    ): Response<List<DocumentResponse>> {

        return RetrofitClient.apiService.getDocuments()
    }

    /**
     * Ask Question
     */
    suspend fun askQuestion(
        question: String,
    ): Response<ChatResponse> {

        val request = ChatRequest(
            query = question,
        )

        return RetrofitClient.apiService.chat(request)
    }

}