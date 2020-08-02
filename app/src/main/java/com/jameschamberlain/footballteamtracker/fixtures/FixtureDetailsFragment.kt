package com.jameschamberlain.footballteamtracker.fixtures

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jameschamberlain.footballteamtracker.FileUtils.writeFixturesFile
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureDetailsBinding
import java.util.*


private const val TAG = "FixtureDetailsFragment"

class FixtureDetailsFragment : Fragment() {
    /**
     * The selected fixture.
     */
    private lateinit var fixture: Fixture

    /**
     * The id of the selected fixture.
     */
    private lateinit var fixtureId: String

    /**
     * The root view of the layout.
     */
    private lateinit var binding: FragmentFixtureDetailsBinding

    /**
     * Adapter for the list of goalscorers.
     */
    private lateinit var goalsAdapter: SimpleRecyclerAdapter

    /**
     * Adapter for the list of assists.
     */
    private lateinit var assistsAdapter: SimpleRecyclerAdapter
    private val userId = FirebaseAuth.getInstance().currentUser?.uid!!

    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activity!!.findViewById<View>(R.id.nav_view).visibility = View.GONE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        containerLayout.layoutParams = params

        val data = this.arguments!!
        fixture = data.getParcelable("fixture")!!
        fixtureId = data.getString("id")!!

        binding = FragmentFixtureDetailsBinding.inflate(layoutInflater)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""

        binding.homeTeamTextView.text = fixture.homeTeam
        binding.scoreTextView.text = fixture.score.toString()
        binding.awayTeamTextView.text = fixture.awayTeam
        binding.dateTextView.text = fixture.extendedDateString()

        binding.timeTextView.text = fixture.timeString()
        setupGoals()
        setupAssists()
        return binding.root
    }

    /**
     * Sets up the list of goalscorers.
     */
    private fun setupGoals() {
        goalsAdapter = SimpleRecyclerAdapter(fixture.goalscorers, true, context!!)
        binding.goalsRecyclerView.adapter = goalsAdapter
        binding.goalsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    /**
     * Sets up the list of assists.
     */
    private fun setupAssists() {
        assistsAdapter = SimpleRecyclerAdapter(fixture.assists, false, context!!)
        binding.assistsRecyclerView.adapter = assistsAdapter
        binding.assistsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private fun loadFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putParcelable("fixture", fixture.copyOf())
        // set arguments
        fragment.arguments = bundle
        // load fragment
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fixture_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // User chose the "Back" item, go back.
                val fm = activity!!.supportFragmentManager
                if (fm.backStackEntryCount > 0) {
                    fm.popBackStack()
                }
                true
            }
            R.id.action_edit -> {
                // User chose the "Edit" action, move to the edit page.
                loadFragment(EditFixtureFragment())
                true
            }
            R.id.action_delete -> {
                // User chose the "Delete" action, delete the fixture.
                MaterialAlertDialogBuilder(context)
                        .setTitle(getString(R.string.delete_this_fixture))
                        .setPositiveButton(getString(R.string.delete)) { _, _ ->
                            val preferences: SharedPreferences = activity!!.getSharedPreferences("com.jameschamberlain.footballteamtracker", Context.MODE_PRIVATE)
                            val teamName = preferences.getString("team_name", null)!!
                            db.collection("users")
                                    .document(userId)
                                    .collection("teams")
                                    .document(teamName.toLowerCase(Locale.ROOT))
                                    .collection("fixtures")
                                    .document(fixtureId)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                        val fm = activity!!.supportFragmentManager
                                        if (fm.backStackEntryCount > 0) {
                                            fm.popBackStack()
                                        }
                                    }
                                    .addOnFailureListener {
                                        e -> Log.w(TAG, "Error deleting document", e)
                                        Toast.makeText(activity, "Error deleting document", Toast.LENGTH_SHORT).show()
                                    }
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                true
            }
            else ->                 // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }
}