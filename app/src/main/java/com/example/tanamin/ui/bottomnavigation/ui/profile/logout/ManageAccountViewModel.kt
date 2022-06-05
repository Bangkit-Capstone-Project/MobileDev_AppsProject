package com.example.tanamin.ui.bottomnavigation.ui.profile.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tanamin.nonui.userpreference.UserPreferences
import kotlinx.coroutines.launch

class ManageAccountViewModel (private val preferences: UserPreferences): ViewModel(){
    fun logout() {
        viewModelScope.launch {
            preferences.logout()
        }
    }
}