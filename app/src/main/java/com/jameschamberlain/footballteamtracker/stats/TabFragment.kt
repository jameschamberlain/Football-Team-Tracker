package com.jameschamberlain.footballteamtracker.stats

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.Player
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatListBinding
import com.jameschamberlain.footballteamtracker.team.PlayerAdapter
import java.util.*

private const val TAG = "TabFragment"

class TabFragment(private val isGoals: Boolean) : Fragment() {

    private lateinit var adapter: StatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentStatListBinding = FragmentStatListBinding.inflate(layoutInflater)

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        val preferences: SharedPreferences = activity!!.getSharedPreferences("com.jameschamberlain.footballteamtracker", Context.MODE_PRIVATE)
        val teamName = preferences.getString("team_name", null)!!

        val playersRef = db.collection("users")
                .document(userId)
                .collection("teams")
                .document(teamName.toLowerCase(Locale.ROOT))
                .collection("players")

        adapter = if (isGoals) {
            val query: Query = playersRef.orderBy("goals", Query.Direction.DESCENDING)
            val options = FirestoreRecyclerOptions.Builder<Player>()
                    .setQuery(query, Player::class.java)
                    .build()
            StatAdapter(options, true, this@TabFragment)
        }
        else {
            val query: Query = playersRef.orderBy("assists", Query.Direction.DESCENDING)
            val options = FirestoreRecyclerOptions.Builder<Player>()
                    .setQuery(query, Player::class.java)
                    .build()
            StatAdapter(options, false, this@TabFragment)
        }

        binding.playersRecyclerView.adapter = adapter
        binding.playersRecyclerView.setHasFixedSize(true)
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(activity)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
        Log.e(TAG, adapter.itemCount.toString())

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}