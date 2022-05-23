package com.example.tanamin.ui.bottomnavigation.ui.myplant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyPlantViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is My Plant Fragment"
    }
    val text: LiveData<String> = _text
}