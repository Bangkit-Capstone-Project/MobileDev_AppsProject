package com.example.tanamin.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tanamin.nonui.userpreference.UserPreferences
import kotlinx.coroutines.launch

class LoginViewModel(private val preferences: UserPreferences) : ViewModel() {

    fun saveToken(token: String) {
        viewModelScope.launch {
            preferences.setToken(token)
        }
    }

    fun saveRefreshToken(refreshToken: String){
        viewModelScope.launch {
            preferences.setRefreshToken(refreshToken)
        }
    }

    fun getSession(): LiveData<Boolean> = preferences.getSession().asLiveData()

    fun login(){
        viewModelScope.launch {
            preferences.login()
        }
    }

}