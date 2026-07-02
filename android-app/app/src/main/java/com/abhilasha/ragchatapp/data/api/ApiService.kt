package com.abhilasha.ragchatapp.data.api

import com.abhilasha.ragchatapp.data.model.ChatRequest
import com.abhilasha.ragchatapp.data.model.ChatResponse
import com.abhilasha.ragchatapp.data.model.DocumentResponse
import com.abhilasha.ragchatapp.data.model.StatusResponse
import com.abhilasha.ragchatapp.data.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    /**
     * Upload PDF
     */
    @Multipart
    @POST("upload/")
    suspend fun uploadPdf(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    /**
     * Get processing status of uploaded PDF
     */
    @GET("upload/status/{filename}")
    suspend fun getDocumentStatus(
        @Path("filename") filename: String
    ): Response<StatusResponse>

    /**
     * Get all uploaded documents
     */
    @GET("upload/documents")
    suspend fun getDocuments(
    ): Response<List<DocumentResponse>>

    /**
     * Ask Question
     */
    @POST("chat/")
    suspend fun chat(
        @Body request: ChatRequest
    ): Response<ChatResponse>
}