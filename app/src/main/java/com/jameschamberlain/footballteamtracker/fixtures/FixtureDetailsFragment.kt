package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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


    private var _binding: FragmentFixtureDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * Adapter for the list of goalscorers.
     */
    private lateinit var goalsAdapter: SimpleRecyclerAdapter

    /**
     * Adapter for the list of assists.
     */
    private lateinit var assistsAdapter: SimpleRecyclerAdapter

    private lateinit var teamName: String

    private val args: FixtureDetailsFragmentArgs by navArgs()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFixtureDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Utils.hideBottomNav(requireActivity())

        fixtureId = args.id
        setupFixture(fixtureId)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
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
        goalsAdapter = SimpleRecyclerAdapter(fixture.goalscorers, true, requireContext())
        binding.goalsRecyclerView.adapter = goalsAdapter
        binding.goalsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    /**
     * Sets up the list of assists.
     */
    private fun setupAssists() {
        assistsAdapter = SimpleRecyclerAdapter(fixture.assists, false, requireContext())
        binding.assistsRecyclerView.adapter = assistsAdapter
        binding.assistsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (Utils.accountType == AccountType.ADMIN)
            inflater.inflate(R.menu.fixture_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // User chose the "Back" item, go back.
                NavHostFragment.findNavController(this@FixtureDetailsFragment).navigateUp()
                true
            }
            R.id.action_edit -> {
                // User chose the "Edit" action, move to the edit page.
                val action = FixtureDetailsFragmentDirections
                        .actionFixtureDetailsFragmentToEditFixtureFragment(fixture, fixtureId)
                NavHostFragment
                        .findNavController(this@FixtureDetailsFragment)
                        .navigate(action)
                true
            }
            R.id.action_delete -> {
                // User chose the "Delete" action, delete the fixture.
                MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.delete_this_fixture))
                        .setPositiveButton(getString(R.string.delete)) { _, _ ->
                            Utils.teamRef.collection("fixtures").document(fixtureId)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                        val fm = requireActivity().supportFragmentManager
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}