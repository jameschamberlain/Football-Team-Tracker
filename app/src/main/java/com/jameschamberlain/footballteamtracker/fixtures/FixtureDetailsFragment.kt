package com.jameschamberlain.footballteamtracker.fixtures

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
import com.google.firebase.firestore.ktx.toObject
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureDetailsBinding
import com.jameschamberlain.footballteamtracker.objects.AccountType
import com.jameschamberlain.footballteamtracker.objects.Fixture


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

    private lateinit var teamName: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activity!!.findViewById<View>(R.id.bottom_nav).visibility = View.GONE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        containerLayout.layoutParams = params

        val extras = this.arguments!!
//        fixture = extras.getParcelable("fixture")!!
        fixtureId = extras.getString("id")!!
        setupFixture(fixtureId)

        binding = FragmentFixtureDetailsBinding.inflate(layoutInflater)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""



        return binding.root
    }

    private fun setupFixture(id: String) {
        Utils.teamRef.collection("fixtures").document(id).get()
                .addOnSuccessListener {documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        this.fixture = documentSnapshot.toObject(Fixture::class.java)!!
                        teamName = Utils.getTeamNameTest()
                        binding.homeTeamTextView.text = if (fixture.isHomeGame) teamName else fixture.opponent
                        binding.awayTeamTextView.text = if (fixture.isHomeGame) fixture.opponent else teamName
                        binding.scoreTextView.text = fixture.score.toString()
                        binding.dateTextView.text = fixture.extendedDateString()

                        binding.timeTextView.text = fixture.timeString()
                        setupGoals()
                        setupAssists()
                    }
                }
                .addOnFailureListener { e -> Log.e(TAG, "Get failed with ", e) }

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
        bundle.putParcelable("fixture", fixture)
        bundle.putString("id", fixtureId)
        // set arguments
        fragment.arguments = bundle
        // load fragment
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (Utils.accountType == AccountType.ADMIN)
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
                            Utils.teamRef.collection("fixtures").document(fixtureId)
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

    override fun onStop() {
        super.onStop()
        Utils.showBottomNav(activity!!)
    }
}