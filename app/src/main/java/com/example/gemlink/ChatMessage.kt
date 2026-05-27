package com.example.gemlink

data class ChatMessage(
    val text: String,
    val isSentByMe: Boolean,
    val time: String
)