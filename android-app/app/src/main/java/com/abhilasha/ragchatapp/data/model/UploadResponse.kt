package com.abhilasha.ragchatapp.data.model

data class UploadResponse(
    val status: String,
    val original_filename: String,
    val stored_filename: String,
    val message: String
)