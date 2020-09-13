package com.jameschamberlain.footballteamtracker.viewmodels

import androidx.lifecycle.ViewModel
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.data.Player

class PlayersViewModel : ViewModel() {

    private val query: Query = Utils.teamRef.collection("players")
    val players = FirestoreArray(query, ClassSnapshotParser(Player::class.java))

    init {
        players.addChangeEventListener(KeepAliveListener)
    }

    override fun onCleared() {
        players.removeChangeEventListener(KeepAliveListener)
    }

    private object KeepAliveListener : ChangeEventListener {

        override fun onChildChanged(
                type: ChangeEventType,
                snapshot: DocumentSnapshot,
                newIndex: Int,
                oldIndex: Int
        ) = Unit

        override fun onDataChanged() = Unit

        override fun onError(e: FirebaseFirestoreException) = Unit

    }


}