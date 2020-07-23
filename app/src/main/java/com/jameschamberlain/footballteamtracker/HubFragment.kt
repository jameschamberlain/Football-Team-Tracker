package com.jameschamberlain.footballteamtracker

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jameschamberlain.footballteamtracker.fixtures.FixtureDetailsFragment
import com.jameschamberlain.footballteamtracker.fixtures.FixtureResult
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HubFragment : Fragment() {

    private val team: Team = Team.instance
    private lateinit var rootView: View

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
        rootView = inflater.inflate(R.layout.fragment_hub, container, false)
        setHasOptionsMenu(true)
        val myToolbar: Toolbar = rootView.findViewById(R.id.toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(myToolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        val teamNameTextView = rootView.findViewById<TextView>(R.id.team_name_text_view)
        teamNameTextView.text = team.name
        setupStatHighlights()
        if (team.gamesPlayed > 0) {
            setupForm()
            setupLatestResult()
        }
        if (team.gamesPlayed < team.fixtures.size) {
            setupNextFixture()
        }

        // Inflate the layout for this fragment
        return rootView
    }

    private fun setupStatHighlights() {
        val winsTextView = rootView.findViewById<TextView>(R.id.win_text_view)
        winsTextView.text = String.format(Locale.ENGLISH, "%d", team.wins)

        val lossesTextView = rootView.findViewById<TextView>(R.id.lose_text_view)
        lossesTextView.text = String.format(Locale.ENGLISH, "%d", team.losses)

        val drawsTextView = rootView.findViewById<TextView>(R.id.draw_text_view)
        drawsTextView.text = String.format(Locale.ENGLISH, "%d", team.draws)

        val goalsForTextView = rootView.findViewById<TextView>(R.id.goals_for_text_view)
        goalsForTextView.text = String.format(Locale.ENGLISH, "%d", team.goalsFor)

        val goalsAgainstTextView = rootView.findViewById<TextView>(R.id.goals_against_text_view)
        goalsAgainstTextView.text = String.format(Locale.ENGLISH, "%d", team.goalsAgainst)

        val goalDiffTextView = rootView.findViewById<TextView>(R.id.goal_diff_text_view)
        goalDiffTextView.text = String.format(Locale.ENGLISH, "%d", team.goalDifference)

        val lossProgress = rootView.findViewById<ProgressBar>(R.id.progress_lose)
        var d = team.losses.toDouble() / (team.wins + team.draws + team.losses).toDouble()
        val lossProgressPercent = (d * 100).toInt()
        lossProgress.post { lossProgress.progress = lossProgressPercent }

        val drawProgress = rootView.findViewById<ProgressBar>(R.id.progress_draw)
        d = team.draws.toDouble() / (team.wins + team.draws + team.losses).toDouble()

        val drawProgressPercent = (d * 100).toInt()
        drawProgress.post { drawProgress.progress = lossProgressPercent + drawProgressPercent }
    }

    private fun setupNextFixture() {
        val fixture = team.fixtures[team.gamesPlayed]
        val dateTextView = rootView.findViewById<TextView>(R.id.fixture_date_text_view)
        dateTextView.text = fixture.dateString
        val timeTextView = rootView.findViewById<TextView>(R.id.fixture_time_text_view)
        timeTextView.text = fixture.timeString
        val homeTeamTextView = rootView.findViewById<TextView>(R.id.fixture_home_team_text_view)
        homeTeamTextView.text = fixture.homeTeam
        val awayTeamTextView = rootView.findViewById<TextView>(R.id.fixture_away_team_text_view)
        awayTeamTextView.text = fixture.awayTeam
        val fixtureLayout = rootView.findViewById<ConstraintLayout>(R.id.fixture_layout)
        fixtureLayout.setOnClickListener {
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
        val fixture = team.fixtures[team.gamesPlayed - 1]
        val dateTextView = rootView.findViewById<TextView>(R.id.result_date_text_view)
        dateTextView.text = fixture.dateString
        val timeTextView = rootView.findViewById<TextView>(R.id.result_time_text_view)
        timeTextView.text = fixture.timeString
        val homeTeamTextView = rootView.findViewById<TextView>(R.id.result_home_team_text_view)
        homeTeamTextView.text = fixture.homeTeam
        val homeScoreTextView = rootView.findViewById<TextView>(R.id.result_home_team_score_text_view)
        homeScoreTextView.text = String.format(Locale.ENGLISH, "%d", fixture.score.home)
        val awayScoreTextView = rootView.findViewById<TextView>(R.id.result_away_team_score_text_view)
        awayScoreTextView.text = String.format(Locale.ENGLISH, "%d", fixture.score.away)
        val awayTeamTextView = rootView.findViewById<TextView>(R.id.result_away_team_text_view)
        awayTeamTextView.text = fixture.awayTeam
        val fixtureLayout = rootView.findViewById<ConstraintLayout>(R.id.result_layout)
        fixtureLayout.setOnClickListener {
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

    private fun setupForm() {
        setFormDrawable(
                rootView.findViewById<View>(R.id.game5) as ImageView,
                team.fixtures[team.gamesPlayed - 1].result)
        if (team.gamesPlayed > 1) {
            setFormDrawable(
                    rootView.findViewById<View>(R.id.game4) as ImageView,
                    team.fixtures[team.gamesPlayed - 2].result)
        }
        if (team.gamesPlayed > 2) {
            setFormDrawable(
                    rootView.findViewById<View>(R.id.game3) as ImageView,
                    team.fixtures[team.gamesPlayed - 3].result)
        }
        if (team.gamesPlayed > 3) {
            setFormDrawable(
                    rootView.findViewById<View>(R.id.game2) as ImageView,
                    team.fixtures[team.gamesPlayed - 4].result)
        }
        if (team.gamesPlayed > 4) {
            setFormDrawable(
                    rootView.findViewById<View>(R.id.game1) as ImageView,
                    team.fixtures[team.gamesPlayed - 5].result)
        }
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
                        val teamNameTextView = rootView.findViewById<TextView>(R.id.team_name_text_view)
                        teamNameTextView.text = team.name
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