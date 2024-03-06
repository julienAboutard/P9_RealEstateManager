package com.example.realestatemanager.ui.slidepane

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlidePaneViewModel: ViewModel() {
    val selection: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}