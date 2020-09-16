package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.data.Player
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.adapters.StatAdapter
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatListBinding
import com.jameschamberlain.footballteamtracker.viewmodels.PlayersViewModel

private const val TAG = "TabFragment"

class TabFragment(private val isGoals: Boolean, private val statsFragment: StatsFragment) : Fragment() {

    private var _binding: FragmentStatListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: PlayersViewModel by activityViewModels()

    private lateinit var adapter: StatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentStatListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = if (isGoals) {
            val options = FirestoreRecyclerOptions.Builder<Player>()
                    .setSnapshotArray(viewModel.playersByGoals)
                    .setLifecycleOwner(this@TabFragment)
                    .build()
            StatAdapter(options, true, statsFragment)
        }
        else {
            val options = FirestoreRecyclerOptions.Builder<Player>()
                    .setSnapshotArray(viewModel.playersByAssissts)
                    .setLifecycleOwner(this@TabFragment)
                    .build()
            StatAdapter(options, false, statsFragment)
        }

        binding.playersRecyclerView.adapter = adapter
        binding.playersRecyclerView.setHasFixedSize(true)
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