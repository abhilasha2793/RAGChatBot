package com.abhilasha.ragchatapp.data.model

import com.google.gson.annotations.SerializedName

data class UploadResponse(

    @SerializedName("status")
    val status: String,

    @SerializedName("original_filename")
    val originalFilename: String,

    @SerializedName("stored_filename")
    val storedFilename: String,

    @SerializedName("pages")
    val pages: Int,

    @SerializedName("characters")
    val characters: Int,

    @SerializedName("chunks")
    val chunks: Int
)