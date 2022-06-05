package com.example.tanamin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.bottomnavigation.ui.profile.credit.CreditViewModel
import com.example.tanamin.ui.bottomnavigation.ui.profile.logout.ManageAccountViewModel
import com.example.tanamin.ui.login.LoginViewModel
import com.example.tanamin.ui.welcomingpage.WelcomingPageViewModel

class ViewModelFactory(private val pref: UserPreferences) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(WelcomingPageViewModel::class.java) -> {
                WelcomingPageViewModel(pref) as T
            }
            modelClass.isAssignableFrom(ManageAccountViewModel::class.java) -> {
                ManageAccountViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}