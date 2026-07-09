package com.istts.finalproject.data.remote.model

data class QuoteResponse(
    val success: Boolean,
    val data: Quote
)

data class Quote(
    val quote: String,
    val author: String
)