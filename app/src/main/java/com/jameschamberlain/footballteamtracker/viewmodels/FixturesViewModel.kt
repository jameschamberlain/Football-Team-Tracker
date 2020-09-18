package com.jameschamberlain.footballteamtracker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
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
import com.jameschamberlain.footballteamtracker.data.FixtureResult

private const val TAG = "FixturesViewModel"

class FixturesViewModel : ViewModel() {

    private val fixturesRef = Utils.teamRef.collection("fixtures")
    private val query: Query = fixturesRef.orderBy("dateTime", Query.Direction.ASCENDING)
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

    fun getSelectedFixture() : LiveData<Fixture> = selectedFixture


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

    private val formFixtures = MutableLiveData<ArrayList<FixtureResult>>()

    fun getFormFixtures(): LiveData<ArrayList<FixtureResult>> = formFixtures

    private val formFixturesSnapshot = Utils.teamRef.collection("fixtures")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .whereIn("result", listOf("WIN", "LOSS", "DRAW"))
            .limit(5)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {

                    formFixtures.value!!.clear()
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
                    formFixtures.notifyObserver()
                }
            }

    private val latestResult = MutableLiveData<Fixture>()

    fun getLatestResult(): LiveData<Fixture> = latestResult

    var latestResultId = ""

    private val latestResultSnapshot = Utils.teamRef.collection("fixtures")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .whereIn("result", listOf("WIN", "LOSS", "DRAW"))
            .limit(1)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    latestResult.value = snapshot.documents[0].toObject(Fixture::class.java)!!
                    latestResultId = snapshot.documents[0].id
                }
            }


    private val nextFixture = MutableLiveData<Fixture>()

    fun getNextFixture(): LiveData<Fixture> = nextFixture

    var nextFixtureId = ""

    private val nextFixtureSnapshot = Utils.teamRef.collection("fixtures")
            .orderBy("dateTime", Query.Direction.ASCENDING)
            .whereEqualTo("result", "UNPLAYED")
            .limit(1)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    nextFixture.value = snapshot.documents[0].toObject(Fixture::class.java)!!
                    nextFixtureId = snapshot.documents[0].id
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