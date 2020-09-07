package com.jameschamberlain.footballteamtracker.hub

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.MenuFragment
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentHubBinding
import com.jameschamberlain.footballteamtracker.objects.Fixture
import com.jameschamberlain.footballteamtracker.objects.FixtureResult
import com.jameschamberlain.footballteamtracker.objects.Team
import kotlin.collections.ArrayList
import kotlin.math.round

private const val TAG = "HubFragment"


/**
 * A simple [Fragment] subclass.
 */
class HubFragment : MenuFragment() {

    private lateinit var mContext: Context

    private lateinit var binding: FragmentHubBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHubBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mContext = requireContext()

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        setHasOptionsMenu(true)

        Utils.updateTeamNameTextView(binding.teamNameTextView)

        setupRecord()
        setupGoals()
        if (Team.gamesPlayed > 0) {
            setupForm()
            setupLatestResult()
        }
        if (Team.gamesPlayed < Team.totalGames) {
            setupNextFixture()
        }
    }

    private fun setupRecord() {
        binding.winsTextView.text = Team.wins.toString()
        binding.lossesTextView.text = Team.losses.toString()
        binding.drawsTextView.text = Team.draws.toString()

        if (Team.gamesPlayed == 0) {
            binding.baseProgressBar.progressDrawable.setTint(ContextCompat.getColor(mContext, R.color.colorUnplayed))
        }
        if (Team.wins > 0) {
            binding.baseProgressBar.progressDrawable.setTint(ContextCompat.getColor(mContext, R.color.colorWin))
        }
        val lossProgressPercent = round(Team.losses.toDouble() / (Team.wins + Team.draws + Team.losses).toDouble() * 100).toInt()
        binding.progressLose.post { binding.progressLose.progress = lossProgressPercent }

        val drawProgressPercent = round(Team.draws.toDouble() / (Team.wins + Team.draws + Team.losses).toDouble() * 100).toInt()
        binding.progressDraw.post { binding.progressDraw.progress = lossProgressPercent + drawProgressPercent }
    }

    private fun setupGoals() {
        binding.goalsForTextView.text = Team.goalsFor.toString()
        binding.goalsAgainstTextView.text = Team.goalsAgainst.toString()
        binding.goalDiffTextView.text = Team.goalDifference.toString()
    }

    private fun setupNextFixture() {
        Utils.teamRef.collection("fixtures")
                .orderBy("dateTime", Query.Direction.ASCENDING)
                .whereEqualTo("result", "UNPLAYED")
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val fixture = documents.documents[0].toObject(Fixture::class.java)!!
                        binding.apply {
                            fixtureDateTextView.text = fixture.dateString()
                            fixtureTimeTextView.text = fixture.timeString()
                            fixtureHomeTeamTextView.text = if (fixture.isHomeGame) Team.teamName else fixture.opponent
                            fixtureAwayTeamTextView.text = if (fixture.isHomeGame) fixture.opponent else Team.teamName
                            fixtureLayout.setOnClickListener {
                                val fixtureId = documents.documents[0].id
                                val action = HubFragmentDirections
                                        .actionHubFragmentToFixtureDetailsFragment(fixtureId)
                                NavHostFragment
                                        .findNavController(this@HubFragment)
                                        .navigate(action)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Get failed with ", e)
                }

    }

    private fun setupLatestResult() {
        Utils.teamRef.collection("fixtures")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .whereIn("result", listOf("WIN", "LOSS", "DRAW"))
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val result = documents.documents[0].toObject(Fixture::class.java)!!
                        binding.apply {
                            resultDateTextView.text = result.dateString()
                            resultTimeTextView.text = result.timeString()
                            resultHomeTeamTextView.text = if (result.isHomeGame) Team.teamName else result.opponent
                            resultHomeTeamScoreTextView.text = result.score.home.toString()
                            resultAwayTeamTextView.text = if (result.isHomeGame) result.opponent else Team.teamName
                            resultAwayTeamScoreTextView.text = result.score.away.toString()
                            resultLayout.setOnClickListener {
                                val fixtureId = documents.documents[0].id
                                val action = HubFragmentDirections
                                        .actionHubFragmentToFixtureDetailsFragment(fixtureId)
                                NavHostFragment
                                        .findNavController(this@HubFragment)
                                        .navigate(action)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Get failed with ", e)
                }
    }

    private fun setupForm() {
        Utils.teamRef.collection("fixtures")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .whereIn("result", listOf("WIN", "LOSS", "DRAW"))
                .limit(5)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val fixturesPlayed = ArrayList<FixtureResult>()
                        for (document in documents.documents) {
                            fixturesPlayed.add(document.toObject(Fixture::class.java)!!.result)
                        }
                        setFormDrawable(binding.game5, fixturesPlayed[0])
                        if (documents.documents.size > 1)
                            setFormDrawable(binding.game4, fixturesPlayed[1])
                        if (documents.documents.size > 2)
                            setFormDrawable(binding.game3, fixturesPlayed[2])
                        if (documents.documents.size > 3)
                            setFormDrawable(binding.game2, fixturesPlayed[3])
                        if (documents.documents.size > 4)
                            setFormDrawable(binding.game1, fixturesPlayed[4])
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Get failed with ", e)
                }

    }

    private fun setFormDrawable(view: ImageView, result: FixtureResult) {
        when (result) {
            FixtureResult.WIN -> view.setColorFilter(ContextCompat.getColor(mContext, R.color.colorWin))
            FixtureResult.LOSS -> view.setColorFilter(ContextCompat.getColor(mContext, R.color.colorLoss))
            FixtureResult.DRAW -> view.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDraw))
            else -> view.setColorFilter(ContextCompat.getColor(mContext, R.color.colorUnplayed))
        }
    }

    override fun onStart() {
        super.onStart()
        Utils.teamRef.collection("fixtures").addSnapshotListener { snapshot, e ->

            Log.d(TAG, "Snapshot listener created")
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            Log.d(TAG, "Snapshot data changed")
            Team.updateStats(snapshot!!.documents)
            setupRecord()
            if (Team.gamesPlayed > 0) {
                setupForm()
                setupLatestResult()
            }
            if (Team.gamesPlayed < Team.totalGames) {
                setupNextFixture()
            }
        }
    }

}