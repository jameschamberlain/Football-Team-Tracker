package com.jameschamberlain.footballteamtracker.hub

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.jameschamberlain.footballteamtracker.BaseFragment
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.data.FixtureResult
import com.jameschamberlain.footballteamtracker.data.Team
import com.jameschamberlain.footballteamtracker.databinding.FragmentHubBinding
import com.jameschamberlain.footballteamtracker.viewmodels.FixturesViewModel
import kotlin.math.round

private const val TAG = "HubFragment"


/**
 * A simple [Fragment] subclass.
 */
class HubFragment : BaseFragment() {

    private lateinit var mContext: Context

    private var _binding: FragmentHubBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: FixturesViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mContext = requireContext()

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.appbar.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        setHasOptionsMenu(true)

        viewModel.teamName.observe(viewLifecycleOwner, {
            binding.teamNameTextView.text = it!!
        })

        setupRecord()
        setupForm()
        setupGoals()
        setupLatestResult()
        setupNextFixture()
    }


    private fun setupRecord() {
        binding.winsTextView.text = Team.wins.toString()
        binding.lossesTextView.text = Team.losses.toString()
        binding.drawsTextView.text = Team.draws.toString()

        if (Team.gamesPlayed < 1) {
            binding.baseProgressBar.progressDrawable.setTint(ContextCompat.getColor(mContext, R.color.colorUnplayed))
        }
        if (Team.wins >= 1) {
            binding.baseProgressBar.progressDrawable.setTint(ContextCompat.getColor(mContext, R.color.colorWin))
        }
        val lossProgressPercent = round(Team.losses.toDouble() / (Team.wins + Team.draws + Team.losses).toDouble() * 100).toInt()
        binding.progressLose.post { binding.progressLose.progress = lossProgressPercent }

        val drawProgressPercent = round(Team.draws.toDouble() / (Team.wins + Team.draws + Team.losses).toDouble() * 100).toInt()
        binding.progressDraw.post { binding.progressDraw.progress = lossProgressPercent + drawProgressPercent }
    }

    private fun setupForm() {
        viewModel.getFormFixtures().observe(viewLifecycleOwner, {
            val form: MutableList<FixtureResult> = MutableList(5) { FixtureResult.UNPLAYED }
            if (it.size > 0) {
                for (i in 0 until it.size) {
                    form[i] = it[i]
                }
            }
            binding.game5.setColorFilter(FixtureResult.getColor(form[0], requireContext()))
            binding.game4.setColorFilter(FixtureResult.getColor(form[1], requireContext()))
            binding.game3.setColorFilter(FixtureResult.getColor(form[2], requireContext()))
            binding.game2.setColorFilter(FixtureResult.getColor(form[3], requireContext()))
            binding.game1.setColorFilter(FixtureResult.getColor(form[4], requireContext()))
        })
    }

    private fun setupGoals() {
        binding.goalsForTextView.text = Team.goalsFor.toString()
        binding.goalsAgainstTextView.text = Team.goalsAgainst.toString()
        binding.goalDiffTextView.text = Team.goalDifference.toString()
    }

    private fun setupNextFixture() {
        viewModel.getNextFixture().observe(viewLifecycleOwner, {
            binding.apply {
                if (it != null) {
                    it.apply {
                        fixtureDateTextView.text = it.dateString()
                        fixtureTimeTextView.text = it.timeString()
                        fixtureHomeTeamTextView.text = if (it.isHomeGame) Team.name else it.opponent
                        fixtureAwayTeamTextView.text = if (it.isHomeGame) it.opponent else Team.name
                        fixtureLayout.setOnClickListener { _ ->
                            viewModel.selectFixture(it)
                            val action = HubFragmentDirections
                                    .actionHubFragmentToFixtureDetailsFragment(viewModel.nextFixtureId)
                            NavHostFragment
                                    .findNavController(this@HubFragment)
                                    .navigate(action)
                        }
                    }
                }
                else {
                    fixtureDateTextView.text = ""
                    fixtureTimeTextView.text = ""
                    fixtureHomeTeamTextView.text = ""
                    fixtureAwayTeamTextView.text = ""
                    fixtureLayout.setOnClickListener(null)
                    fixtureLayout.isClickable = false
                }
            }
        })


    }

    private fun setupLatestResult() {
        viewModel.getLatestResult().observe(viewLifecycleOwner, {
            binding.apply {
                if (it != null) {
                    it.apply {
                        resultDateTextView.text = dateString()
                        resultTimeTextView.text = timeString()
                        resultHomeTeamTextView.text = if (isHomeGame) Team.name else opponent
                        resultHomeTeamScoreTextView.text = score.home.toString()
                        resultAwayTeamTextView.text = if (isHomeGame) opponent else Team.name
                        resultAwayTeamScoreTextView.text = score.away.toString()
                        resultLayout.setOnClickListener { _ ->
                            viewModel.selectFixture(it)
                            val action = HubFragmentDirections
                                    .actionHubFragmentToFixtureDetailsFragment(viewModel.latestResultId)
                            NavHostFragment
                                    .findNavController(this@HubFragment)
                                    .navigate(action)
                        }
                    }
                }
                else {
                    resultDateTextView.text = ""
                    resultTimeTextView.text = ""
                    resultHomeTeamTextView.text = ""
                    resultHomeTeamScoreTextView.text = ""
                    resultAwayTeamTextView.text = ""
                    resultAwayTeamScoreTextView.text = ""
                    resultLayout.setOnClickListener(null)
                    resultLayout.isClickable = false
                }


            }

        })
    }

    override fun onStart() {
        super.onStart()
        Utils.teamRef.collection("fixtures").addSnapshotListener { snapshot, e ->

            Log.d(TAG, "Fixtures Snapshot listener created")
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            Log.d(TAG, "Fixtures Snapshot data changed")
            if (_binding != null) {
                Team.updateStats(snapshot!!.documents)
                setupRecord()
//                setupForm()
                setupGoals()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_team_code -> {
                completeTeamCodeAction()
                true
            }
            R.id.action_settings -> {
                val action = HubFragmentDirections
                        .actionHubFragmentToSettingsFragment()
                NavHostFragment
                        .findNavController(this@HubFragment)
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