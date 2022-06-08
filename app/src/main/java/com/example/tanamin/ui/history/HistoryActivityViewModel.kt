package com.example.tanamin.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tanamin.nonui.userpreference.UserPreferences
import kotlinx.coroutines.launch

class HistoryActivityViewModel(private val preferences: UserPreferences) : ViewModel() {
    fun getToken() : LiveData<String> {
        return preferences.getToken().asLiveData()
    }

    fun getRefreshToken() : LiveData<String> {
        return preferences.getRefreshToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            preferences.setToken(token)
        }
    }
}