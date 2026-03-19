package com.hemad.stishield.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemad.stishield.model.quiz.QuizQuestion
import com.hemad.stishield.model.quiz.QuizRepository
import com.hemad.stishield.ui.utilities.formatTextWithHtmlTags
import kotlinx.coroutines.launch

class QuizScreenViewModel(context: Context,difficulty:String): ViewModel() {

    val repository = QuizRepository(context,difficulty)
    val mainTextState = mutableStateOf(AnnotatedString(""))
    val questionNumberTextState = mutableStateOf("")
    val buttonTitles =  mutableStateListOf<String>()
    val correctChoiceIndex = mutableStateOf<Int?>(null)
    val totalQuestionNum = repository.totalQuestionsNum
    val errorState = mutableStateOf<String?>(null)
    val processState = mutableStateOf(false)
    val isAnswerSelected = mutableStateOf(false)
    val showTryAgainButton = mutableStateOf(false)
    lateinit var currentQuestion: QuizQuestion

    init {
        loadQuestion()
    }

    fun loadQuestion(){
        buttonTitles.clear()
        currentQuestion = repository.getNextQuestion()
        mainTextState.value = formatTextWithHtmlTags(currentQuestion.text)
        questionNumberTextState.value = getQuestionNumberText()
        buttonTitles.addAll(currentQuestion.choices)
        correctChoiceIndex.value = currentQuestion.correctChoiceIndex
    }

    fun getQuestionNumberText():String{
        return "Question " + (currentQuestion.id+1).toString() + "/" + totalQuestionNum.toString()
    }


    fun loadResults(){
        viewModelScope.launch {
            processState.value = true
            val (resultText,updatePointsRes,saveQuizScoreRes) = repository.getResult()
            processState.value = false
            saveQuizScoreRes.onFailure { exception ->
                errorState.value = exception.message
            }
            if (updatePointsRes != null){
                updatePointsRes.onFailure { exception ->
                    errorState.value = exception.message
                }
            }
            questionNumberTextState.value = ""
            mainTextState.value = formatTextWithHtmlTags(resultText)
            buttonTitles.clear()
            showTryAgainButton.value = true
        }

    }

    fun onAnswerButtonPressed(response:String){
        repository.recordUserResponse(response)
    }

    fun onNextButtonPressed(){
        isAnswerSelected.value = false
        if (currentQuestion.id == totalQuestionNum-1){
            loadResults()
        } else {
            loadQuestion()
        }

    }

    fun onTryAgainButtonPressed(){
        resetQuiz()
    }

    fun resetQuiz(){
        repository.resetRepo()
        isAnswerSelected.value = false
        showTryAgainButton.value = false
        loadQuestion()
    }

    fun clearError() {
        errorState.value = null
    }




}