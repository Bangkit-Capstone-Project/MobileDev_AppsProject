package com.example.tanamin.ui.bottomnavigation.ui.profile.logout

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tanamin.nonui.userpreference.UserPreferences
import kotlinx.coroutines.launch

class ManageAccountViewModel (private val preferences: UserPreferences): ViewModel(){
    fun logout() {
        viewModelScope.launch {
            preferences.logout()
        }
    }

    fun getRefreshToken() : LiveData<String> {
        return preferences.getRefreshToken().asLiveData()
    }
}