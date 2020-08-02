package com.jameschamberlain.footballteamtracker.fixtures

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.ItemFixtureBinding

class FixtureRecyclerAdapter internal constructor(private val mContext: Context?, private val parentFragment: Fragment) : RecyclerView.Adapter<FixtureRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemFixtureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFixture = team.fixtures[position]
        holder.dateTextView.text = currentFixture.dateString()
        holder.timeTextView.text = currentFixture.timeString()
        setResult(holder.resultTextView, currentFixture)
        holder.homeTeamTextView.text = currentFixture.homeTeam
        holder.scoreTextView.text = currentFixture.score.toString()
        holder.awayTeamTextView.text = currentFixture.awayTeam
//        holder.parentLayout.setOnClickListener(FixtureOnClickListener(parentFragment, position))
    }

    override fun getItemCount(): Int {
        return team.fixtures.size
    }

    private fun setResult(resultTextView: TextView, currentFixture: Fixture) {
        when (currentFixture.result) {
            FixtureResult.WIN -> {
                resultTextView.text = "W"
                resultTextView.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorWin))
            }
            FixtureResult.LOSE -> {
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

    inner class ViewHolder(itemBinding: ItemFixtureBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var dateTextView: TextView = itemBinding.dateTextView
        var timeTextView: TextView = itemBinding.timeTextView
        var resultTextView: TextView = itemBinding.resultTextView
        var homeTeamTextView: TextView = itemBinding.homeTeamTextView
        var scoreTextView: TextView = itemBinding.scoreTextView
        var awayTeamTextView: TextView = itemBinding.awayTeamTextView
        var parentLayout: ConstraintLayout = itemBinding.root
    }

}