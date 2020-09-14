package com.jameschamberlain.footballteamtracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.ItemFixtureBinding
import com.jameschamberlain.footballteamtracker.fixtures.FixturesFragment
import com.jameschamberlain.footballteamtracker.fixtures.FixturesFragmentDirections
import com.jameschamberlain.footballteamtracker.data.Fixture
import com.jameschamberlain.footballteamtracker.data.FixtureResult
import com.jameschamberlain.footballteamtracker.viewmodels.FixturesViewModel

class FixtureAdapter(
        options: FirestoreRecyclerOptions<Fixture>,
        private val context: Context,
        private val parentFragment: FixturesFragment,
        private val viewModel: FixturesViewModel
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
        holder.resultTextView.text = model.result.text
        if (model.result != FixtureResult.DRAW)
            holder.resultTextView.setTextColor(FixtureResult.getColor(model.result, context))
        holder.scoreTextView.text = model.score.toString()

        val teamName = Utils.getTeamNameTest()
        holder.homeTeamTextView.text = if (model.isHomeGame) teamName else model.opponent
        holder.awayTeamTextView.text = if (model.isHomeGame) model.opponent else teamName

        holder.parentLayout.setOnClickListener {
            viewModel.selectFixture(model)
            val action = FixturesFragmentDirections.actionFixturesFragmentToFixtureDetailsFragment(
                    fixtureId = this.snapshots.getSnapshot(position).id
            )
            NavHostFragment
                    .findNavController(parentFragment)
                    .navigate(action)
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