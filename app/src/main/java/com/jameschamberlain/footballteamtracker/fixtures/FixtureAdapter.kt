package com.jameschamberlain.footballteamtracker.fixtures

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.databinding.ItemFixtureBinding

class FixtureAdapter(options: FirestoreRecyclerOptions<Fixture>, private val mContext: Context?, private val parentFragment: Fragment) : FirestoreRecyclerAdapter<Fixture, FixtureAdapter.FixtureHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixtureHolder {
        val itemBinding = ItemFixtureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FixtureHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FixtureHolder, position: Int, model: Fixture) {
        holder.dateTextView.text = model.dateString()
        holder.timeTextView.text = model.timeString()
        setResult(holder.resultTextView, model.result)
        holder.homeTeamTextView.text = model.homeTeam
        holder.scoreTextView.text = model.score.toString()
        holder.awayTeamTextView.text = model.awayTeam
        holder.parentLayout.setOnClickListener(FixtureOnClickListener(parentFragment, this@FixtureAdapter, model, position))
    }

    private fun setResult(resultTextView: TextView, result: FixtureResult) {
        when (result) {
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

    inner class FixtureHolder(itemBinding: ItemFixtureBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var dateTextView: TextView = itemBinding.dateTextView
        var timeTextView: TextView = itemBinding.timeTextView
        var resultTextView: TextView = itemBinding.resultTextView
        var homeTeamTextView: TextView = itemBinding.homeTeamTextView
        var scoreTextView: TextView = itemBinding.scoreTextView
        var awayTeamTextView: TextView = itemBinding.awayTeamTextView
        var parentLayout: ConstraintLayout = itemBinding.root
    }


}