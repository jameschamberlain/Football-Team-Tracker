package com.jameschamberlain.footballteamtracker.fixtures

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.instance

class FixtureRecyclerAdapter internal constructor(private val mContext: Context?, private val parentFragment: Fragment) : RecyclerView.Adapter<FixtureRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fixture, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFixture = instance.fixtures[position]
        holder.dateTextView.text = currentFixture.dateString
        holder.timeTextView.text = currentFixture.timeString
        setResult(holder.resultTextView, currentFixture)
        holder.homeTeamTextView.text = currentFixture.homeTeam
        holder.scoreTextView.text = currentFixture.score.toString()
        holder.awayTeamTextView.text = currentFixture.awayTeam
        holder.parentLayout.setOnClickListener(FixtureOnClickListener(parentFragment, position))
    }

    override fun getItemCount(): Int {
        return instance.fixtures.size
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
                resultTextView.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorDraw))
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateTextView: TextView = itemView.findViewById(R.id.date_text_view)
        var timeTextView: TextView = itemView.findViewById(R.id.time_text_view)
        var resultTextView: TextView = itemView.findViewById(R.id.result_text_view)
        var homeTeamTextView: TextView = itemView.findViewById(R.id.home_team_text_view)
        var scoreTextView: TextView = itemView.findViewById(R.id.score_text_view)
        var awayTeamTextView: TextView = itemView.findViewById(R.id.away_team_text_view)
        var parentLayout: RelativeLayout = itemView.findViewById(R.id.parent_layout)

    }

}