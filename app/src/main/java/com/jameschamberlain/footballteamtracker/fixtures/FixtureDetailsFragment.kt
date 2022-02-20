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
import com.google.firebase.firestore.FieldValue
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.adapters.FixtureStatAdapter
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureDetailsBinding
import com.jameschamberlain.footballteamtracker.data.AccountType
import com.jameschamberlain.footballteamtracker.data.Fixture
import com.jameschamberlain.footballteamtracker.viewmodels.FixturesViewModel
import com.jameschamberlain.footballteamtracker.viewmodels.FixturesViewModelFactory
import java.util.*


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

    private val model: FixturesViewModel by activityViewModels { FixturesViewModelFactory(Utils.getTeamReference(requireActivity())) }

    private val args: FixtureDetailsFragmentArgs by navArgs()

    private lateinit var fixture: Fixture


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFixtureDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fixtureId = args.fixtureId

        model.getSelectedFixture().observe(viewLifecycleOwner, {
            setupFixture(it)
            fixture = it
        })

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
    }

    private fun setupFixture(fixture: Fixture) {
        binding.homeTeamTextView.text = if (fixture.isHomeGame) Utils.getTeamName(requireActivity()) else fixture.opponent
        binding.awayTeamTextView.text = if (fixture.isHomeGame) fixture.opponent else Utils.getTeamName(requireActivity())
        binding.scoreTextView.text = fixture.score.toString()
        binding.dateTextView.text = fixture.extendedDateString()

        binding.timeTextView.text = fixture.timeString()
        binding.venueTextView.text = if (fixture.venue != null) fixture.venue else "N/A"
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
        if (Utils.getAccountType(requireActivity()) == AccountType.ADMIN)
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
                            Utils.getTeamReference(requireActivity()).collection("fixtures").document(fixtureId)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                        for (scorer in fixture.goalscorers) {
                                            val playerId = scorer.lowercase(Locale.ROOT)
                                            Utils.getTeamReference(requireActivity()).collection("players").document(playerId)
                                                    .update("goals", FieldValue.increment(-1))
                                        }
                                        for (assist in fixture.assists) {
                                            val playerId = assist.lowercase(Locale.ROOT)
                                            Utils.getTeamReference(requireActivity()).collection("players").document(playerId)
                                                    .update("assists", FieldValue.increment(-1))
                                        }
                                        NavHostFragment.findNavController(this@FixtureDetailsFragment).navigateUp()
                                    }
                                    .addOnFailureListener {
                                        e -> Log.w(TAG, "Error deleting document", e)
                                        Toast.makeText(activity, "Error deleting fixture", Toast.LENGTH_LONG).show()
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