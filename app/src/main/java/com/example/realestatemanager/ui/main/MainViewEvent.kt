package com.example.realestatemanager.ui.main

sealed class MainViewEvent {
    object EstateList : MainViewEvent()
    //object AddEstates : MainViewEvent()
    object FilterEstates : MainViewEvent()
    //object EditEstates : MainViewEvent()
    //object MapEstates : MainViewEvent()

    data class NavigateToBlank(val fragmentTag: String) : MainViewEvent()
}