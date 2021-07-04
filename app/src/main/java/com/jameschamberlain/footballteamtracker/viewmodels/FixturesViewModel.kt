package com.jameschamberlain.footballteamtracker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.data.Fixture
import com.jameschamberlain.footballteamtracker.data.FixtureResult

private const val TAG = "FixturesViewModel"

class FixturesViewModel(teamReference: DocumentReference) : ViewModel() {

    private val query: Query = teamReference.collection("fixtures").orderBy("dateTime", Query.Direction.ASCENDING)
    val fixtures = FirestoreArray(query, ClassSnapshotParser(Fixture::class.java))


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


    private val selectedFixture = MutableLiveData<Fixture>()

    fun selectFixture(fixture: Fixture) {
        selectedFixture.value = fixture
    }

    fun getSelectedFixture(): LiveData<Fixture> = selectedFixture


    private val formFixtures = MutableLiveData<ArrayList<FixtureResult>>()

    fun getFormFixtures(): LiveData<ArrayList<FixtureResult>> = formFixtures

    private val formFixturesSnapshot = teamReference.collection("fixtures")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .whereIn("result", listOf("WIN", "LOSS", "DRAW"))
            .limit(5)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {

                    formFixtures.value!!.clear()
                    if (!snapshot.isEmpty) {
                        for (document in snapshot) {
                            formFixtures.value?.add(
                                    when (document.getString("result")) {
                                        "WIN" -> FixtureResult.WIN
                                        "DRAW" -> FixtureResult.DRAW
                                        "LOSS" -> FixtureResult.LOSS
                                        else -> FixtureResult.UNPLAYED
                                    }
                            )
                        }
                    }
                    formFixtures.notifyObserver()
                }
            }

    private val latestResult = MutableLiveData<Fixture>()

    fun getLatestResult(): LiveData<Fixture?> = latestResult

    var latestResultId = ""

    private val latestResultSnapshot = teamReference.collection("fixtures")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .whereIn("result", listOf("WIN", "LOSS", "DRAW"))
            .limit(1)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        latestResult.value = snapshot.documents[0].toObject(Fixture::class.java)!!
                        latestResultId = snapshot.documents[0].id
                    }
                    else {
                        latestResult.value = null
                        latestResultId = ""
                    }
                }
            }


    private val nextFixture = MutableLiveData<Fixture>()

    fun getNextFixture(): LiveData<Fixture?> = nextFixture

    var nextFixtureId = ""

    private val nextFixtureSnapshot = teamReference.collection("fixtures")
            .orderBy("dateTime", Query.Direction.ASCENDING)
            .whereEqualTo("result", "UNPLAYED")
            .limit(1)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        nextFixture.value = snapshot.documents[0].toObject(Fixture::class.java)!!
                        nextFixtureId = snapshot.documents[0].id
                    }
                    else {
                        nextFixture.value = null
                        nextFixtureId  =""
                    }
                }
            }


    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }


    init {
        fixtures.addChangeEventListener(KeepAliveListener)
        formFixtures.value = ArrayList()
    }


    override fun onCleared() {
        fixtures.removeChangeEventListener(KeepAliveListener)
        formFixturesSnapshot.remove()
        latestResultSnapshot.remove()
        nextFixtureSnapshot.remove()
    }

}