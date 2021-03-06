package com.jameschamberlain.footballteamtracker.viewmodels

import androidx.lifecycle.ViewModel
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.data.Player

class PlayersViewModel(private val teamReference: DocumentReference) : ViewModel() {

    private val query: Query = teamReference.collection("players")
    val players = FirestoreArray(query, ClassSnapshotParser(Player::class.java))

    private val goalsQuery: Query = teamReference.collection("players")
            .orderBy("goals", Query.Direction.DESCENDING).orderBy("name", Query.Direction.ASCENDING)
    val playersByGoals = FirestoreArray(goalsQuery, ClassSnapshotParser(Player::class.java))

    private val assistsQuery: Query = teamReference.collection("players")
            .orderBy("assists", Query.Direction.DESCENDING).orderBy("name", Query.Direction.ASCENDING)
    val playersByAssists = FirestoreArray(assistsQuery, ClassSnapshotParser(Player::class.java))

    init {
        players.addChangeEventListener(KeepAliveListener)
        playersByGoals.addChangeEventListener(KeepAliveListener)
        playersByAssists.addChangeEventListener(KeepAliveListener)
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


    override fun onCleared() {
        players.removeChangeEventListener(KeepAliveListener)
        playersByGoals.removeChangeEventListener(KeepAliveListener)
        playersByAssists.removeChangeEventListener(KeepAliveListener)
    }


}