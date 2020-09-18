package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.BaseFragment
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.adapters.FixtureAdapter
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixturesBinding
import com.jameschamberlain.footballteamtracker.data.AccountType
import com.jameschamberlain.footballteamtracker.data.Fixture
import com.jameschamberlain.footballteamtracker.viewmodels.FixturesViewModel

/**
 * A simple [Fragment] subclass.
 */
class FixturesFragment : BaseFragment() {

    private lateinit var adapter: FixtureAdapter
    private lateinit var teamName: String

    private var _binding: FragmentFixturesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: FixturesViewModel by activityViewModels()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFixturesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.appbar.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        setHasOptionsMenu(true)

        teamName = Utils.getTeamNameTest()

        val options = FirestoreRecyclerOptions.Builder<Fixture>()
                .setSnapshotArray(viewModel.fixtures)
                .setLifecycleOwner(this@FixturesFragment)
                .build()
        adapter = FixtureAdapter(options, requireContext(), this@FixturesFragment, viewModel)
        binding.fixturesRecyclerView.adapter = adapter

        if (Utils.accountType == AccountType.ADMIN) {
            binding.fab.setOnClickListener {
                val action = FixturesFragmentDirections
                        .actionFixturesFragmentToNewFixtureFragment()
                NavHostFragment
                        .findNavController(this@FixturesFragment)
                        .navigate(action)
            }
        }
        else {
            binding.fab.visibility = View.GONE
        }
    }

    fun addNoFixturesLayout() {
        binding.noFixturesLayout.visibility = View.VISIBLE
        if (Utils.accountType == AccountType.ADMIN)
            binding.noFixturesTextView.text = getString(R.string.no_fixtures_manager_desc)
    }

    fun removeNoFixturesLayout() { binding.noFixturesLayout.visibility = View.GONE }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_team_code -> {
                completeTeamCodeAction()
                true
            }
            R.id.action_settings -> {
                val action = FixturesFragmentDirections
                        .actionFixturesFragmentToSettingsFragment()
                NavHostFragment
                        .findNavController(this@FixturesFragment)
                        .navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}