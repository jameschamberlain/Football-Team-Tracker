package com.jameschamberlain.footballteamtracker.fixtures

import androidx.lifecycle.ViewModel
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.objects.Fixture

class FixturesViewModel : ViewModel() {

    private val fixturesRef = Utils.teamRef.collection("fixtures")
    private val query: Query = fixturesRef.orderBy("dateTime", Query.Direction.ASCENDING)
    val fixtures = FirestoreArray(query, ClassSnapshotParser(Fixture::class.java))

    init {
        fixtures.addChangeEventListener(KeepAliveListener)
    }

    override fun onCleared() {
        fixtures.removeChangeEventListener(KeepAliveListener)
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