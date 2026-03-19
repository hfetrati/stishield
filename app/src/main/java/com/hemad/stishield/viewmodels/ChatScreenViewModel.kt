package com.hemad.stishield.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemad.stishield.model.common.Message
import com.hemad.stishield.model.chat.ChatRepository
import com.hemad.stishield.model.common.ChatItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatScreenViewModel(persona: String) : ViewModel() {

    var currentPersona:String = persona
    var repository:ChatRepository = ChatRepository(currentPersona)
    var messageListScrollState = LazyListState()
    val textFieldState = mutableStateOf("")
    val messageListState = mutableStateListOf<ChatItem>()
    val processState = mutableStateOf(false)
    val loadingIndicator = ChatItem.LoadingIndicator
    val errorState = mutableStateOf<String?>(null)


    fun onTextFieldContentChanged(newValue: String) {
        textFieldState.value = newValue
    }

    fun clearTextField() {
        textFieldState.value = ""
    }


    fun loadFirstMessage(){
        val firstMessage = repository.loadFirstMessage()
        messageListState.clear()
        messageListState.add(firstMessage)
    }

    fun onSendButtonPressed() {

        val newUserMessage = ChatItem.MessageItem(Message(body = textFieldState.value, role = Message.SenderRole.USER))
        addNewItemToList(newUserMessage)
        fetchResponse(textFieldState.value)
        clearTextField()
    }

    fun addNewItemToList(item: ChatItem) {
        messageListState.add(item)
        scrollToLastItem()
    }

    fun removeItemFromList(item: ChatItem) {
        messageListState.remove(item)
    }

    fun scrollToLastItem() {
        viewModelScope.launch {
            delay(300)
            messageListScrollState.scrollToItem(
                index = messageListScrollState.layoutInfo.totalItemsCount,
            )
        }
    }

    fun fetchResponse(inputMessage: String) {

        viewModelScope.launch {

            try {
                updateProgress(start = true)
                val responseMessage = repository.getGPTResponse(inputMessage)
                updateProgress(start = false)
                addNewItemToList(responseMessage)

            } catch (error: Exception) {
                errorState.value = error.message ?: "Unknown error occurred"
                updateProgress(start = false)
            }

        }
    }

    fun updateProgress(start:Boolean){

        if (start){
            processState.value = true
            updateLoadingIndicator(processState.value)
        } else {
            processState.value = false
            updateLoadingIndicator(processState.value)
        }
    }

    fun updateLoadingIndicator(progressState:Boolean) {

        viewModelScope.launch {
            if (progressState){
                delay(600)
                addNewItemToList(loadingIndicator)
            } else {
                removeItemFromList(loadingIndicator)
            }
        }
    }


    fun clearError() {
        errorState.value = null
    }

    suspend fun clearHistory() {
        try {
            repository.clearHistory()
        } catch (error: Exception) {
            errorState.value = error.message ?: "Unknown error occurred"
        }
    }

    fun refresh() {

        if (!processState.value) {
            viewModelScope.launch {
                clearHistory()
                repository = ChatRepository(currentPersona)
                messageListState.clear()
            }
        }

    }




}