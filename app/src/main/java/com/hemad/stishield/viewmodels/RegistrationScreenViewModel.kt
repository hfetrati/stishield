package com.hemad.stishield.viewmodels

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemad.stishield.model.common.UserDataRepository
import kotlinx.coroutines.launch


class RegistrationScreenViewModel(context: Context) : ViewModel() {

    val repository = UserDataRepository(context)
    val errorState = mutableStateOf<String?>(null)
    val registrationSuccessful = mutableStateOf<Boolean?>(null)
    val nicknameFieldState = mutableStateOf("")
    val emailFieldState = mutableStateOf("")
    val processState = mutableStateOf( false)
    val pageScrollState = ScrollState(0)
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    val isEmailValid = derivedStateOf {
        emailFieldState.value.matches(emailRegex)
    }

    fun registerNewUser(email: String, nickname: String) {
        processState.value = true
        viewModelScope.launch {
            val result = repository.registerNewUser(email, nickname)
            processState.value = false
            result.onSuccess {
                registrationSuccessful.value = true
            }.onFailure { exception ->
                errorState.value = exception.message
                registrationSuccessful.value = false
            }
        }
    }


    fun clearError() {
        errorState.value = null
    }

    fun scrollPageToBottom(){
        viewModelScope.launch {
            pageScrollState.scrollTo(pageScrollState.maxValue)
        }
    }

}


