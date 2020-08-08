package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatsBinding

/**
 * A simple [Fragment] subclass.
 */
class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val navView: BottomNavigationView = activity!!.findViewById(R.id.nav_view)
        navView.menu.getItem(2).isChecked = true
        navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
        navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
        navView.menu.getItem(2).setIcon(R.drawable.ic_analytics)
        navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
        activity!!.findViewById<View>(R.id.nav_view).visibility = View.VISIBLE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        val pixels = 56 * context!!.resources.displayMetrics.density
        params.setMargins(0, 0, 0, pixels.toInt())
        containerLayout.layoutParams = params

        binding = FragmentStatsBinding.inflate(layoutInflater)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""

        binding.viewPager.adapter = TabAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.goals) else getString(R.string.assists)
        }.attach()


        // Inflate the layout for this fragment
        return binding.root
    }

    fun addNoStatsLayout() { binding.noStatsLayout.visibility = View.VISIBLE }

    fun removeNoStatsLayout() { binding.noStatsLayout.visibility = View.GONE }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.action_rename_team) {
//            // User chose the "Rename Team" action, show a window to allow this.
//            val editText = EditText(context)
//            editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
//            editText.setText(team.name)
//            val scale = resources.displayMetrics.density
//            val dpAsPixels = (20 * scale + 0.5f).toInt()
//            val container = FrameLayout(context!!)
//            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            lp.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, 0)
//            editText.layoutParams = lp
//            container.addView(editText)
//            MaterialAlertDialogBuilder(context)
//                    .setTitle(getString(R.string.rename_your_team))
//                    .setView(container)
//                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
//                        val input = editText.text.toString()
//                        team.name = input
//                        writeTeamFile(team.name)
//                        writeFixturesFile(team.fixtures)
//                    }
//                    .setNegativeButton(getString(R.string.cancel), null)
//                    .show()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
}