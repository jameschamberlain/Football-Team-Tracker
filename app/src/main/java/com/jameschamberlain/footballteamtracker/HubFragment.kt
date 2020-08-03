package com.jameschamberlain.footballteamtracker

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.databinding.FragmentHubBinding
import com.jameschamberlain.footballteamtracker.fixtures.FixtureDetailsFragment
import com.jameschamberlain.footballteamtracker.fixtures.FixtureResult
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class HubFragment : Fragment() {

    private val team: Team = Team.team
    private lateinit var binding: FragmentHubBinding
    private lateinit var teamName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fm = activity?.supportFragmentManager
        if (fm != null) {
            if (fm.backStackEntryCount > 0) {
                val first = fm.getBackStackEntryAt(0)
                fm.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
        val navView: BottomNavigationView = activity!!.findViewById(R.id.nav_view)
        navView.menu.getItem(0).isChecked = true
        navView.menu.getItem(0).setIcon(R.drawable.ic_home)
        navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
        navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
        navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
        activity!!.findViewById<View>(R.id.nav_view).visibility = View.VISIBLE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        val pixels = 56 * context!!.resources.displayMetrics.density
        params.setMargins(0, 0, 0, pixels.toInt())
        containerLayout.layoutParams = params
        binding = FragmentHubBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        val preferences: SharedPreferences = activity!!.getSharedPreferences("com.jameschamberlain.footballteamtracker", MODE_PRIVATE)
        teamName = preferences.getString("team_name", null)!!
        binding.teamNameTextView.text = teamName
        setupStatHighlights()
        if (team.gamesPlayed > 0) {
            setupForm()
            setupLatestResult()
        }
        if (team.gamesPlayed < team.fixtures.size) {
            setupNextFixture()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupStatHighlights() {
        if (team.gamesPlayed == 0) {
            binding.baseProgressBar.progressDrawable.setTint(ContextCompat.getColor(context!!, R.color.colorUnplayed))
        }
        else {
            binding.baseProgressBar.progressDrawable.setTint(ContextCompat.getColor(context!!, R.color.colorWin))
        }
        binding.winsTextView.text = String.format(Locale.ENGLISH, "%d", team.wins)
        binding.lossesTextView.text = String.format(Locale.ENGLISH, "%d", team.losses)
        binding.drawsTextView.text = String.format(Locale.ENGLISH, "%d", team.draws)
        binding.goalsForTextView.text = String.format(Locale.ENGLISH, "%d", team.goalsFor)
        binding.goalsAgainstTextView.text = String.format(Locale.ENGLISH, "%d", team.goalsAgainst)
        binding.goalDiffTextView.text = String.format(Locale.ENGLISH, "%d", team.goalDifference)

        val lossProgressPercent = (team.losses.toDouble() / (team.wins + team.draws + team.losses).toDouble() * 100).toInt()
        binding.progressLose.post { binding.progressLose.progress = lossProgressPercent }

        val drawProgressPercent = (team.draws.toDouble() / (team.wins + team.draws + team.losses).toDouble() * 100).toInt()
        binding.progressDraw.post { binding.progressDraw.progress = lossProgressPercent + drawProgressPercent }
    }

    private fun setupNextFixture() {
        var i = 0
        for(j in 0 until team.fixtures.size) {
            if (team.fixtures[j].result == FixtureResult.UNPLAYED) {
                i = j
                break
            }
        }
        val fixture = team.fixtures[i]
        binding.fixtureDateTextView.text = fixture.dateString()
        binding.fixtureTimeTextView.text = fixture.timeString()
        binding.fixtureHomeTeamTextView.text = if (fixture.isHomeGame) teamName else fixture.opponent
        binding.fixtureAwayTeamTextView.text = if (fixture.isHomeGame) fixture.opponent else teamName
        binding.fixtureLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("fixture", fixture)
            // set arguments
            val fragment: Fragment = FixtureDetailsFragment()
            fragment.arguments = bundle
            // load fragment
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun setupLatestResult() {
        var i = 0
        for(j in 0 until team.fixtures.size) {
            if (team.fixtures[j].result != FixtureResult.UNPLAYED) i = j
        }
        val result = team.fixtures[i]
        binding.resultDateTextView.text = result.dateString()
        binding.resultTimeTextView.text = result.timeString()
        binding.resultHomeTeamTextView.text = if (result.isHomeGame) teamName else result.opponent
        binding.resultHomeTeamScoreTextView.text = String.format(Locale.ENGLISH, "%d", result.score.home)
        binding.resultAwayTeamTextView.text = if (result.isHomeGame) result.opponent else teamName
        binding.resultAwayTeamScoreTextView.text = String.format(Locale.ENGLISH, "%d", result.score.away)
        binding.resultLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("fixture", result)
            // set arguments
            val fragment: Fragment = FixtureDetailsFragment()
            fragment.arguments = bundle
            // load fragment
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun setupForm() {
        val fixturesPlayed = ArrayList<FixtureResult>()
        for(i in 0 until team.fixtures.size) {
            if (team.fixtures[i].result != FixtureResult.UNPLAYED)
                fixturesPlayed.add(team.fixtures[i].result)
        }
        setFormDrawable(binding.game5, fixturesPlayed[fixturesPlayed.size - 1])
        if (fixturesPlayed.size > 1)
            setFormDrawable(binding.game4, fixturesPlayed[fixturesPlayed.size - 2])
        if (fixturesPlayed.size > 2)
            setFormDrawable(binding.game3, fixturesPlayed[fixturesPlayed.size - 3])
        if (fixturesPlayed.size > 3)
            setFormDrawable(binding.game2, fixturesPlayed[fixturesPlayed.size - 4])
        if (fixturesPlayed.size > 4)
            setFormDrawable(binding.game1, fixturesPlayed[fixturesPlayed.size - 5])
    }

    private fun setFormDrawable(view: ImageView, result: FixtureResult) {
        when (result) {
            FixtureResult.WIN -> view.setColorFilter(ContextCompat.getColor(context!!, R.color.colorWin))
            FixtureResult.LOSE -> view.setColorFilter(ContextCompat.getColor(context!!, R.color.colorLoss))
            FixtureResult.DRAW -> view.setColorFilter(ContextCompat.getColor(context!!, R.color.colorDraw))
            else -> view.setColorFilter(ContextCompat.getColor(context!!, R.color.colorUnplayed))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_rename_team) {
            // User chose the "Rename Team" action, show a window to allow this.
            val editText = EditText(context)
            editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            editText.setText(team.name)
            val scale = resources.displayMetrics.density
            val dpAsPixels = (20 * scale + 0.5f).toInt()
            val container = FrameLayout(context!!)
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, 0)
            editText.layoutParams = lp
            container.addView(editText)
            MaterialAlertDialogBuilder(context)
                    .setTitle(getString(R.string.rename_your_team))
                    .setView(container)
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        val input = editText.text.toString()
                        team.name = input
                        binding.teamNameTextView.text = team.name
                        FileUtils.writeTeamFile(team.name)
                        FileUtils.writeFixturesFile(team.fixtures)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}