package com.jameschamberlain.footballteamtracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.google.firebase.firestore.DocumentReference

class FixturesViewModelFactory(private val teamReference: DocumentReference) : NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FixturesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FixturesViewModel(teamReference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}