package com.jameschamberlain.footballteamtracker.hub

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentHubBinding
import com.jameschamberlain.footballteamtracker.fixtures.FixtureDetailsFragment
import com.jameschamberlain.footballteamtracker.objects.Fixture
import com.jameschamberlain.footballteamtracker.objects.FixtureResult
import com.jameschamberlain.footballteamtracker.objects.Team
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "HubFragment"


/**
 * A simple [Fragment] subclass.
 */
class HubFragment : Fragment() {

    private lateinit var mContext : Context

    private lateinit var binding: FragmentHubBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        val fm = activity?.supportFragmentManager
//        if (fm != null) {
//            if (fm.backStackEntryCount > 0) {
//                val first = fm.getBackStackEntryAt(0)
//                fm.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//            }
//        }
        mContext = requireContext()
        val navView: BottomNavigationView = activity!!.findViewById(R.id.bottom_nav)
        navView.menu.getItem(0).isChecked = true
        navView.menu.getItem(0).setIcon(R.drawable.ic_home)
        navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
        navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
        navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
        activity!!.findViewById<View>(R.id.bottom_nav).visibility = View.VISIBLE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        val pixels = 56 * mContext.resources.displayMetrics.density
        params.setMargins(0, 0, 0, pixels.toInt())
        containerLayout.layoutParams = params
        binding = FragmentHubBinding.inflate(layoutInflater)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""


        Utils.setTeamNameTextView(binding.teamNameTextView)

        setupStatHighlights()
        if (Team.gamesPlayed > 0) {
            setupForm()
            setupLatestResult()
        }
        if (Team.gamesPlayed < Team.totalGames) {
            setupNextFixture()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupStatHighlights() {
        if (Team.gamesPlayed == 0) {
            binding.baseProgressBar.progressDrawable.setTint(ContextCompat.getColor(mContext, R.color.colorUnplayed))
        } else {
            binding.baseProgressBar.progressDrawable.setTint(ContextCompat.getColor(mContext, R.color.colorWin))
        }
        binding.winsTextView.text = String.format(Locale.ENGLISH, "%d", Team.wins)
        binding.lossesTextView.text = String.format(Locale.ENGLISH, "%d", Team.losses)
        binding.drawsTextView.text = String.format(Locale.ENGLISH, "%d", Team.draws)
        binding.goalsForTextView.text = String.format(Locale.ENGLISH, "%d", Team.goalsFor)
        binding.goalsAgainstTextView.text = String.format(Locale.ENGLISH, "%d", Team.goalsAgainst)
        binding.goalDiffTextView.text = String.format(Locale.ENGLISH, "%d", Team.goalDifference)

        val lossProgressPercent = (Team.losses.toDouble() / (Team.wins + Team.draws + Team.losses).toDouble() * 100).toInt()
        binding.progressLose.post { binding.progressLose.progress = lossProgressPercent }

        val drawProgressPercent = (Team.draws.toDouble() / (Team.wins + Team.draws + Team.losses).toDouble() * 100).toInt()
        binding.progressDraw.post { binding.progressDraw.progress = lossProgressPercent + drawProgressPercent }
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
                        binding.fixtureDateTextView.text = fixture.dateString()
                        binding.fixtureTimeTextView.text = fixture.timeString()
                        binding.fixtureHomeTeamTextView.text = if (fixture.isHomeGame) Team.teamName else fixture.opponent
                        binding.fixtureAwayTeamTextView.text = if (fixture.isHomeGame) fixture.opponent else Team.teamName
                        binding.fixtureLayout.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putParcelable("fixture", fixture)
                            bundle.putString("id", documents.documents[0].id)
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
                        binding.resultDateTextView.text = result.dateString()
                        binding.resultTimeTextView.text = result.timeString()
                        binding.resultHomeTeamTextView.text = if (result.isHomeGame) Team.teamName else result.opponent
                        binding.resultHomeTeamScoreTextView.text = String.format(Locale.ENGLISH, "%d", result.score.home)
                        binding.resultAwayTeamTextView.text = if (result.isHomeGame) result.opponent else Team.teamName
                        binding.resultAwayTeamScoreTextView.text = String.format(Locale.ENGLISH, "%d", result.score.away)
                        binding.resultLayout.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putParcelable("fixture", result)
                            bundle.putString("id", documents.documents[0].id)
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
                        Log.d(TAG, "Fixture list: $fixturesPlayed")
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
            setupStatHighlights()
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