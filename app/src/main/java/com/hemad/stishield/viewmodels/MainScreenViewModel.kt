package com.hemad.stishield.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hemad.stishield.model.common.UserDataRepository

class MainScreenViewModel(context: Context): ViewModel() {

    val userRepo = UserDataRepository(context)
    val userNickname = userRepo.nickname
    var dropdownExpanded = mutableStateOf(false)

}