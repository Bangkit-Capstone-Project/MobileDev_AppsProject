package com.example.tanamin.ui.welcomingpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.tanamin.nonui.userpreference.UserPreferences

class WelcomingPageViewModel (private val preferences: UserPreferences) : ViewModel(){
    fun getSession(): LiveData<Boolean> = preferences.getSession().asLiveData()
}