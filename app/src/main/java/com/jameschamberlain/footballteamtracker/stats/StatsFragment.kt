package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.jameschamberlain.footballteamtracker.BaseFragment
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.adapters.TabAdapter
import com.jameschamberlain.footballteamtracker.data.AccountType
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatsBinding
import com.jameschamberlain.footballteamtracker.viewmodels.FixturesViewModelFactory
import com.jameschamberlain.footballteamtracker.viewmodels.PlayersViewModel
import com.jameschamberlain.footballteamtracker.viewmodels.PlayersViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class StatsFragment : BaseFragment() {

    private var _binding: FragmentStatsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: PlayersViewModel by activityViewModels { PlayersViewModelFactory(Utils.getTeamReference(requireActivity())) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        setHasOptionsMenu(true)

        binding.viewPager.adapter = TabAdapter(this@StatsFragment)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.goals) else getString(R.string.assists)
        }.attach()
    }

    fun addNoStatsLayout() {
        binding.noStatsLayout.visibility = View.VISIBLE
        if (Utils.getAccountType(requireActivity()) == AccountType.ADMIN)
            binding.noStatsTextView.text = getString(R.string.no_stats_manager_desc)
    }

    fun removeNoStatsLayout() { binding.noStatsLayout.visibility = View.GONE }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_team_code -> {
                completeTeamCodeAction()
                true
            }
            R.id.action_settings -> {
                val action = StatsFragmentDirections
                        .actionStatsFragmentToSettingsFragment()
                NavHostFragment
                        .findNavController(this@StatsFragment)
                        .navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}