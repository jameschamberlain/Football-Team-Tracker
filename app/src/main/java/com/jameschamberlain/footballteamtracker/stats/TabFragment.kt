package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.objects.Player
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatListBinding
import java.util.*

private const val TAG = "TabFragment"

class TabFragment(private val isGoals: Boolean, private val statsFragment: StatsFragment) : Fragment() {

    private lateinit var binding: FragmentStatListBinding
    private lateinit var adapter: StatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentStatListBinding.inflate(layoutInflater)

        Log.d(TAG, "Is goals: $isGoals")
        Log.d(TAG, "Parent fragment: $statsFragment")
        val playersRef = Utils.teamRef.collection("players")

        adapter = if (isGoals) {
            val query: Query = playersRef.orderBy("goals", Query.Direction.DESCENDING).orderBy("name", Query.Direction.ASCENDING)
            val options = FirestoreRecyclerOptions.Builder<Player>()
                    .setQuery(query, Player::class.java)
                    .build()
            StatAdapter(options, true, statsFragment)
        }
        else {
            val query: Query = playersRef.orderBy("assists", Query.Direction.DESCENDING).orderBy("name", Query.Direction.ASCENDING)
            val options = FirestoreRecyclerOptions.Builder<Player>()
                    .setQuery(query, Player::class.java)
                    .build()
            StatAdapter(options, false, statsFragment)
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

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}