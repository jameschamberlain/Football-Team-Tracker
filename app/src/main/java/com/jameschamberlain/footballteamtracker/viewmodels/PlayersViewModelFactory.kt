package com.jameschamberlain.footballteamtracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.firestore.DocumentReference

class PlayersViewModelFactory(private val teamReference: DocumentReference) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayersViewModel(teamReference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}