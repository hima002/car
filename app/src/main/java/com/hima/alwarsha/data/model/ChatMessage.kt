package com.hima.alwarsha.data.model

import android.graphics.Bitmap

enum class ChatRole { USER, ASSISTANT }

data class ChatMessage(
    val role: ChatRole,
    val text: String,
    val imageBitmap: Bitmap? = null
)
