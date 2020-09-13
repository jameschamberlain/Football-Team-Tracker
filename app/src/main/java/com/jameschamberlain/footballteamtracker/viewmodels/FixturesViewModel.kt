package com.jameschamberlain.footballteamtracker.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.data.Fixture

private const val TAG = "FixturesViewModel"

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


    private val selectedFixture = MutableLiveData<Int>()

    fun selectFixture(position: Int) {
        selectedFixture.value = position
    }

    fun getSelectedFixture() : MutableLiveData<Int> {
        return selectedFixture
    }


     val teamName: MutableLiveData<String> by lazy {
         MutableLiveData<String>().also {
             loadTeamName()
         }
     }

    private fun loadTeamName() {
        Utils.teamRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        teamName.value = document.getString("name")!!
                    } else {
                        // Convert the whole Query Snapshot to a list
                        // of objects directly! No need to fetch each
                        // document.
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Get failed with ", e)
                }
    }
}