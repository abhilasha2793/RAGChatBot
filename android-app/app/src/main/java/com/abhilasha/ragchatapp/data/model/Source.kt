package com.abhilasha.ragchatapp.data.model

import com.google.gson.annotations.SerializedName

data class Source(

    @SerializedName("chunk_id")
    val chunkId: Int,

    @SerializedName("file")
    val file: String,

    @SerializedName("page")
    val page: Int?,

    @SerializedName("preview")
    val preview: String
)