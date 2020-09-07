package com.jameschamberlain.footballteamtracker.fixtures

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.ItemFixtureBinding
import com.jameschamberlain.footballteamtracker.objects.Fixture
import com.jameschamberlain.footballteamtracker.objects.FixtureResult

class FixtureAdapter(
        options: FirestoreRecyclerOptions<Fixture>,
        private val mContext: Context?,
        private val parentFragment: FixturesFragment
) : FirestoreRecyclerAdapter<Fixture, FixtureAdapter.FixtureHolder>(options) {

    override fun onDataChanged() {
        if (itemCount == 0)
            parentFragment.addNoFixturesLayout()
        else
            parentFragment.removeNoFixturesLayout()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixtureHolder {
        val itemBinding = ItemFixtureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FixtureHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FixtureHolder, position: Int, model: Fixture) {
        // Update the result with the actual score
        model.score = model.score

        holder.dateTextView.text = model.dateString()
        holder.timeTextView.text = model.timeString()
        setResult(holder.resultTextView, model.result)
        holder.scoreTextView.text = model.score.toString()

        val teamName = Utils.getTeamNameTest()
        holder.homeTeamTextView.text = if (model.isHomeGame) teamName else model.opponent
        holder.awayTeamTextView.text = if (model.isHomeGame) model.opponent else teamName

        holder.parentLayout.setOnClickListener {
            val fixtureId = this.snapshots.getSnapshot(position).id
            val action = FixturesFragmentDirections
                    .actionFixturesFragmentToFixtureDetailsFragment(fixtureId)
            NavHostFragment
                    .findNavController(parentFragment)
                    .navigate(action)
        }
    }

    private fun setResult(resultTextView: TextView, result: FixtureResult) {
        when (result) {
            FixtureResult.WIN -> {
                resultTextView.text = "W"
                resultTextView.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorWin))
            }
            FixtureResult.LOSS -> {
                resultTextView.text = "L"
                resultTextView.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorLoss))
            }
            FixtureResult.DRAW -> {
                resultTextView.text = "D"
                resultTextView.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorDraw))
            }
            else -> {
                resultTextView.text = ""
            }
        }
    }

    class FixtureHolder(itemBinding: ItemFixtureBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var dateTextView: TextView = itemBinding.dateTextView
        var timeTextView: TextView = itemBinding.timeTextView
        var resultTextView: TextView = itemBinding.resultTextView
        var homeTeamTextView: TextView = itemBinding.homeTeamTextView
        var scoreTextView: TextView = itemBinding.scoreTextView
        var awayTeamTextView: TextView = itemBinding.awayTeamTextView
        var parentLayout: ConstraintLayout = itemBinding.root
    }


}