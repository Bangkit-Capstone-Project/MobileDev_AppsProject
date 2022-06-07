package com.example.tanamin.ui.mainfeature.riceplant

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.tanamin.nonui.userpreference.UserPreferences

class RicePlantActivityViewModel(private val preferences: UserPreferences) : ViewModel() {
    fun getToken() : LiveData<String> {
        return preferences.getToken().asLiveData()
    }
}