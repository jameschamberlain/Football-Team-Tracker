package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.adapters.FixtureStatAdapter
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureDetailsBinding
import com.jameschamberlain.footballteamtracker.data.AccountType
import com.jameschamberlain.footballteamtracker.data.Fixture
import com.jameschamberlain.footballteamtracker.viewmodels.FixturesViewModel


private const val TAG = "FixtureDetailsFragment"

class FixtureDetailsFragment : Fragment() {

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
    private lateinit var goalsAdapter: FixtureStatAdapter

    /**
     * Adapter for the list of assists.
     */
    private lateinit var assistsAdapter: FixtureStatAdapter

    private lateinit var teamName: String

    private val model: FixturesViewModel by activityViewModels()

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
        model.teamName.observe(viewLifecycleOwner, {
            teamName = it
        })

        model.getSelectedFixture().observe(viewLifecycleOwner, {
            setupFixture(it)
        })

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
    }

    private fun setupFixture(fixture: Fixture) {
        binding.homeTeamTextView.text = if (fixture.isHomeGame) teamName else fixture.opponent
        binding.awayTeamTextView.text = if (fixture.isHomeGame) fixture.opponent else teamName
        binding.scoreTextView.text = fixture.score.toString()
        binding.dateTextView.text = fixture.extendedDateString()

        binding.timeTextView.text = fixture.timeString()
        setupGoals(fixture)
        setupAssists(fixture)
    }

    /**
     * Sets up the list of goalscorers.
     */
    private fun setupGoals(fixture: Fixture) {
        goalsAdapter = FixtureStatAdapter(fixture.goalscorers, true, requireContext())
        binding.goalsRecyclerView.adapter = goalsAdapter
        binding.goalsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    /**
     * Sets up the list of assists.
     */
    private fun setupAssists(fixture: Fixture) {
        assistsAdapter = FixtureStatAdapter(fixture.assists, false, requireContext())
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
                        .actionFixtureDetailsFragmentToEditFixtureFragment(
                                fixtureId = args.fixtureId
                        )
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
                                        NavHostFragment.findNavController(this@FixtureDetailsFragment).navigateUp()
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