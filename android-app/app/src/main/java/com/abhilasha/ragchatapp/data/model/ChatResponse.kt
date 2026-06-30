package com.abhilasha.ragchatapp.data.model

import com.google.gson.annotations.SerializedName

data class ChatResponse(

    @SerializedName("status")
    val status: String,

    @SerializedName("query")
    val query: String,

    @SerializedName("answer")
    val answer: String,

    @SerializedName("retrieved_chunks")
    val retrievedChunks: Int,

    @SerializedName("model")
    val model: String,

    @SerializedName("sources")
    val sources: List<Source>
)