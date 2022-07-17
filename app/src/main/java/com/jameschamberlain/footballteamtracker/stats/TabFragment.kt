package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.data.Player
import com.jameschamberlain.footballteamtracker.adapters.StatAdapter
import com.jameschamberlain.footballteamtracker.data.AccountType
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatListBinding
import com.jameschamberlain.footballteamtracker.viewmodels.PlayersViewModel
import com.jameschamberlain.footballteamtracker.viewmodels.PlayersViewModelFactory
import kotlin.properties.Delegates

class TabFragment() : Fragment() {

    private var _binding: FragmentStatListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: PlayersViewModel by activityViewModels { PlayersViewModelFactory(Utils.getTeamReference(requireActivity())) }

    private lateinit var adapter: StatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = FragmentStatListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var tabIsGoals by Delegates.notNull<Boolean>()
        arguments?.takeIf { it.containsKey("tabIsGoals") }?.apply {
            tabIsGoals = getInt("tabIsGoals") == 0
        }
        adapter = if (tabIsGoals) {
            val options = FirestoreRecyclerOptions.Builder<Player>()
                    .setSnapshotArray(viewModel.playersByGoals)
                    .setLifecycleOwner(this@TabFragment)
                    .build()
            StatAdapter(options, true, this@TabFragment)
        }
        else {
            val options = FirestoreRecyclerOptions.Builder<Player>()
                    .setSnapshotArray(viewModel.playersByAssists)
                    .setLifecycleOwner(this@TabFragment)
                    .build()
            StatAdapter(options, false, this@TabFragment)
        }

        binding.playersRecyclerView.adapter = adapter
        binding.playersRecyclerView.setHasFixedSize(true)
    }

    fun addNoStatsLayout() {
        binding.noStatsLayout.visibility = View.VISIBLE
        if (Utils.getAccountType(requireActivity()) == AccountType.ADMIN)
            binding.noStatsTextView.text = getString(R.string.no_stats_manager_desc)
    }

    fun removeNoStatsLayout() { binding.noStatsLayout.visibility = View.GONE }

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