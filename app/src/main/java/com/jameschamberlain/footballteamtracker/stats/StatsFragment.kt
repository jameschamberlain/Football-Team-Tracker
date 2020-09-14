package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.jameschamberlain.footballteamtracker.BaseFragment
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.adapters.TabAdapter
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatsBinding

/**
 * A simple [Fragment] subclass.
 */
class StatsFragment : BaseFragment() {

    private var _binding: FragmentStatsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        setHasOptionsMenu(true)

        binding.viewPager.adapter = TabAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.goals) else getString(R.string.assists)
        }.attach()


        // Inflate the layout for this fragment
        return binding.root
    }

    fun addNoStatsLayout() { binding.noStatsLayout.visibility = View.VISIBLE }

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