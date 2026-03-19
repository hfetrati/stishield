package com.hemad.stishield.viewmodels

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemad.stishield.model.game.BattleResult
import com.hemad.stishield.model.game.GameRepository
import com.hemad.stishield.model.game.Task
import com.hemad.stishield.ui.utilities.formatTextWithHtmlTags
import kotlinx.coroutines.launch
import com.hemad.stishield.model.game.GameResult

class GameScreenViewModel(context: Context): ViewModel() {

    val mainTextState = mutableStateOf(AnnotatedString(""))
    val buttonTitles =  mutableStateListOf<String>()
    val correctChoiceIndex = mutableStateOf<Int?>(null)
    val pageTitle = mutableStateOf<String>("")
    val popUpText =  mutableStateOf<AnnotatedString>(AnnotatedString(""))
    val showSTIFactPopUpState = mutableStateOf(false)
    val showRestartGameConfirmationPopUpState = mutableStateOf(false)
    val repository = GameRepository(context)
    val imageIDState = mutableStateOf<Int?>(null)
    val screenScrollState = ScrollState(0)
    val textScrollState = ScrollState(0)
    val dropdownExpanded = mutableStateOf(false)
    lateinit var currentTask: Task
    val showTheEndImage = mutableStateOf(false)
    val errorState = mutableStateOf<String?>(null)
    val processState = mutableStateOf(false)

    init {
        loadCurrentTask()
    }

    fun onButtonPressed(title: String) {

        val result = currentTask.action!!.invoke(title)

        when (result) {
            is GameResult.Success -> handleSuccess()
            is GameResult.Continue -> handleContinue()
            is GameResult.Failure -> handleFailure()
            is GameResult.Repeat -> handleRepeat(result)
            is GameResult.Achievement -> handleAchievement()
            else -> return
        }
    }


    fun loadCurrentTask(){

        viewModelScope.launch{
//            delay(500)
            buttonTitles.clear()
            currentTask = repository.getCurrentTask()
            pageTitle.value = repository.getCurrentLevelTitle()
            mainTextState.value = formatTextWithHtmlTags(currentTask.description)
            imageIDState.value = currentTask.imageID
            buttonTitles.addAll(currentTask.choices)
            correctChoiceIndex.value = currentTask.correctChoiceIndex
            screenScrollState.scrollTo(0)
            textScrollState.scrollTo(0)
            if (currentTask.isFinalTask == true){
                showTheEndImage.value = true
            } else {
                showTheEndImage.value = false
            }
        }

    }

    fun handleAchievement(){

        val factText = repository.getCurrentFact().description
        val formattedFactText = formatTextWithHtmlTags(factText)
        popUpText.value = formattedFactText
        showSTIFactPopUpState.value = true

    }

    fun handleSuccess(){

        viewModelScope.launch {
            repository.incrementLevel(1)
            repository.setTaskIndex(0)
            repository.incrementFactIndex(1)
            processState.value = true
            val result = repository.incrementPoints(100)
            processState.value = false
            result.onFailure { exception ->
                errorState.value = exception.message
            }
            loadCurrentTask()
        }

    }

    fun handleFailure(){
        loadCurrentTask()
    }

    fun handleRepeat(result:GameResult.Repeat){

        viewModelScope.launch{
//            delay(500)
            val resultContent = result.content as BattleResult
            mainTextState.value= formatTextWithHtmlTags(resultContent.message)
            textScrollState.scrollTo(0)
            buttonTitles.clear()
            buttonTitles.addAll(resultContent.possibleActions)
        }

    }

    fun handleContinue(){
        repository.incrementTaskIndex(1)
        loadCurrentTask()
    }

    fun restartGame(){
        repository.restartGame()
        loadCurrentTask()
    }

    fun dismissPopUp(){
        showSTIFactPopUpState.value = false
    }

    fun clearError() {
        errorState.value = null
    }





}