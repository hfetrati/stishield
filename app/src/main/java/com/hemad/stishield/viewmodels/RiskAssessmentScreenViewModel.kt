package com.hemad.stishield.viewmodels

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemad.stishield.model.common.ChatItem
import com.hemad.stishield.model.common.Message
import com.hemad.stishield.model.riskassessment.RiskAssessmentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class RiskAssessmentScreenViewModel(context: Context) : ViewModel(){

    val messageListScrollState = LazyListState()
    var repository = RiskAssessmentRepository(context = context)
    val messageListState = mutableStateListOf<ChatItem>()
    val buttonsEnabledStateList = mutableStateListOf<Boolean>()
    val numberOfQuestions = repository.getNumberOfQuestions()
    val progressState = mutableStateOf(false)
    val loadingIndicator = ChatItem.LoadingIndicator
    val errorState = mutableStateOf<String?>(null)
    val appContext = context

    init {
        repeat(numberOfQuestions) { buttonsEnabledStateList.add(true) }
        messageListState.add(repository.getFirstMessage())
    }

    fun onChoiceButtonPressed (questionId:Int, buttonTitle:String){

        val randomDelay = Random.nextInt(2000, 3500).toLong()

        viewModelScope.launch {
            buttonsEnabledStateList[questionId] = !buttonsEnabledStateList[questionId]
            addNewItemToList(createUserMessage(buttonTitle))
            updateProgress(start = true)
            repository.recordResponse(questionId,buttonTitle)
            val nextMessage = repository.getNextMessage()
            delay(randomDelay)
            updateProgress(start = false)
            nextMessage?.let { addNewItemToList(it) }
            if (nextMessage is ChatItem.MessageItemWithServerResult){
                val serverResult = nextMessage.serverResult
                if (serverResult != null){
                    serverResult.onFailure { exception ->
                        errorState.value = exception.message
                    }
                }
            }
        }


    }

    fun createUserMessage (text:String): ChatItem {
        return ChatItem.MessageItem(Message(body = text, role = Message.SenderRole.USER))
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
                index = messageListScrollState.layoutInfo.totalItemsCount
            )
        }
    }

    fun refresh(){

        repository = RiskAssessmentRepository(context = appContext)
        messageListState.clear()
        messageListState.add(repository.getFirstMessage())

        buttonsEnabledStateList.clear()
        repeat(numberOfQuestions) { buttonsEnabledStateList.add(true) }

    }

    fun updateProgress(start:Boolean){

        if (start){
            progressState.value = true
            updateLoadingIndicator(progressState.value)
        } else {
            progressState.value = false
            updateLoadingIndicator(progressState.value)
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

}