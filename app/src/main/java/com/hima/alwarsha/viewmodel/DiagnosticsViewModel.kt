package com.hima.alwarsha.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hima.alwarsha.data.entity.CarEntity
import com.hima.alwarsha.data.model.ChatMessage
import com.hima.alwarsha.data.model.ChatRole
import com.hima.alwarsha.data.repository.DiagnosticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiagnosticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DiagnosticsRepository()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    fun sendMessage(car: CarEntity?, text: String, imageBitmap: Bitmap?) {
        if (text.isBlank() && imageBitmap == null) return
        val userMessage = ChatMessage(role = ChatRole.USER, text = text, imageBitmap = imageBitmap)
        val historyWithUserTurn = _messages.value + userMessage
        _messages.value = historyWithUserTurn
        _isSending.value = true

        viewModelScope.launch {
            val replyText = repository.sendMessage(car, historyWithUserTurn)
            _messages.value = _messages.value + ChatMessage(role = ChatRole.ASSISTANT, text = replyText)
            _isSending.value = false
        }
    }
}
