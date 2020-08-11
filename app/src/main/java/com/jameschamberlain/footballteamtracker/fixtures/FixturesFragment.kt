package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixturesBinding
import com.jameschamberlain.footballteamtracker.objects.AccountType
import com.jameschamberlain.footballteamtracker.objects.Fixture

private const val TAG = "FixturesFragment"

/**
 * A simple [Fragment] subclass.
 */
class FixturesFragment : Fragment() {

    private lateinit var adapter: FixtureAdapter
    private lateinit var teamName: String

    private lateinit var binding: FragmentFixturesBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Utils.showBottomNav(activity!!)

        binding = FragmentFixturesBinding.inflate(layoutInflater)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""


        teamName = Utils.getTeamNameTest()

        val fixturesRef = Utils.teamRef.collection("fixtures")
        val query: Query = fixturesRef.orderBy("dateTime", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<Fixture>()
                .setQuery(query, Fixture::class.java)
                .build()
        adapter = FixtureAdapter(options, activity, this@FixturesFragment)

        binding.fixturesRecyclerView.adapter = adapter
        binding.fixturesRecyclerView.setHasFixedSize(true)
        binding.fixturesRecyclerView.layoutManager = LinearLayoutManager(activity)

        if (Utils.accountType == AccountType.ADMIN) {
            binding.fab.setOnClickListener { loadFragment(NewFixtureFragment()) }
        }
        else {
            binding.fab.visibility = View.GONE
        }

        //Scroll item 2 to 20 pixels from the top
//        layoutManager.scrollToPositionWithOffset(team.gamesPlayed - 3, 0)


        // Inflate the layout for this fragment.
        return binding.root
    }

    fun addNoFixturesLayout() { binding.noFixturesLayout.visibility = View.VISIBLE }

    fun removeNoFixturesLayout() { binding.noFixturesLayout.visibility = View.GONE }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private fun loadFragment(fragment: Fragment) {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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