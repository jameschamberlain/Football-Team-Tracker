package com.jameschamberlain.footballteamtracker

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jameschamberlain.footballteamtracker.databinding.FragmentHubBinding
import com.jameschamberlain.footballteamtracker.fixtures.FixtureDetailsFragment
import com.jameschamberlain.footballteamtracker.fixtures.FixtureResult
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HubFragment : Fragment() {

    private val team: Team = Team.team
    private lateinit var binding: FragmentHubBinding

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
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        val pixels = 56 * context!!.resources.displayMetrics.density
        params.setMargins(0, 0, 0, pixels.toInt())
        containerLayout.layoutParams = params
        binding = FragmentHubBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        binding.teamNameTextView.text = team.name
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
        binding.winsTextView.text = String.format(Locale.ENGLISH, "%d", team.wins)
        binding.lossesTextView.text = String.format(Locale.ENGLISH, "%d", team.losses)
        binding.drawsTextView.text = String.format(Locale.ENGLISH, "%d", team.draws)
        Log.e("HubFragment.kt", "GF: " + team.goalsFor + ", GA: "+ team.goalsAgainst)
        binding.goalsForTextView.text = String.format(Locale.ENGLISH, "%d", team.goalsFor)
        binding.goalsAgainstTextView.text = String.format(Locale.ENGLISH, "%d", team.goalsAgainst)
        binding.goalDiffTextView.text = String.format(Locale.ENGLISH, "%d", team.goalDifference)

        val lossProgressPercent = (team.losses.toDouble() / (team.wins + team.draws + team.losses).toDouble() * 100).toInt()
        binding.progressLose.post { binding.progressLose.progress = lossProgressPercent }

        val drawProgressPercent = (team.draws.toDouble() / (team.wins + team.draws + team.losses).toDouble() * 100).toInt()
        binding.progressDraw.post { binding.progressDraw.progress = lossProgressPercent + drawProgressPercent }
    }

    private fun setupNextFixture() {
        val fixture = team.fixtures[team.gamesPlayed]
        binding.fixtureDateTextView.text = fixture.dateString
        binding.fixtureTimeTextView.text = fixture.timeString
        binding.fixtureHomeTeamTextView.text = fixture.homeTeam
        binding.fixtureAwayTeamTextView.text = fixture.awayTeam
        binding.fixtureLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("fixture", fixture)
            // set arguments
            val fragment: Fragment = FixtureDetailsFragment()
            fragment.arguments = bundle
            // load fragment
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun setupLatestResult() {
        val result = team.fixtures[team.gamesPlayed - 1]
        binding.resultDateTextView.text = result.dateString
        binding.resultTimeTextView.text = result.timeString
        binding.resultHomeTeamTextView.text = result.homeTeam
        binding.resultHomeTeamScoreTextView.text = String.format(Locale.ENGLISH, "%d", result.score.home)
        binding.resultAwayTeamTextView.text = result.awayTeam
        binding.resultAwayTeamScoreTextView.text = String.format(Locale.ENGLISH, "%d", result.score.away)
        binding.resultAwayTeamTextView.text = result.awayTeam
        binding.resultLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("fixture", result)
            // set arguments
            val fragment: Fragment = FixtureDetailsFragment()
            fragment.arguments = bundle
            // load fragment
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun setupForm() {
        setFormDrawable(binding.game5, team.fixtures[team.gamesPlayed - 1].result)
        if (team.gamesPlayed > 1)
            setFormDrawable(binding.game4, team.fixtures[team.gamesPlayed - 2].result)
        if (team.gamesPlayed > 2)
            setFormDrawable(binding.game3, team.fixtures[team.gamesPlayed - 3].result)
        if (team.gamesPlayed > 3)
            setFormDrawable(binding.game2, team.fixtures[team.gamesPlayed - 4].result)
        if (team.gamesPlayed > 4)
            setFormDrawable(binding.game1, team.fixtures[team.gamesPlayed - 5].result)
    }

    private fun setFormDrawable(view: ImageView, result: FixtureResult) {
        when (result) {
            FixtureResult.WIN -> view.setColorFilter(ContextCompat.getColor(context!!, R.color.colorWin))
            FixtureResult.LOSE -> view.setColorFilter(ContextCompat.getColor(context!!, R.color.colorLoss))
            else -> view.setColorFilter(ContextCompat.getColor(context!!, R.color.colorDraw))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_rename_team) {
            // User chose the "Rename Team" action, show a window to allow this.
            val alert = AlertDialog.Builder(context!!)
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
            alert.setTitle("Rename your team:")
                    .setView(container)
                    .setPositiveButton("Confirm") { _, _ ->
                        val input = editText.text.toString()
                        for (fixture in team.fixtures) {
                            if (fixture.homeTeam == team.name) {
                                fixture.homeTeam = input
                            } else {
                                fixture.awayTeam = input
                            }
                        }
                        team.name = input
                        binding.teamNameTextView.text = team.name
                        FileUtils.writeTeamFile(team.name)
                        FileUtils.writeFixturesFile(team.fixtures)
                    }
                    .setNegativeButton("Cancel", null)
            val dialog = alert.create()
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            dialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}