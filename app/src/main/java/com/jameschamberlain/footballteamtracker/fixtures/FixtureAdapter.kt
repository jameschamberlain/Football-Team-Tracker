package com.jameschamberlain.footballteamtracker.fixtures

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.databinding.ItemFixtureBinding

class FixtureAdapter(options: FirestoreRecyclerOptions<Fixture>) : FirestoreRecyclerAdapter<Fixture, FixtureAdapter.FixtureHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixtureHolder {
        val itemBinding = ItemFixtureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FixtureHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FixtureHolder, position: Int, model: Fixture) {
        holder.dateTextView.text = model.dateString()
        holder.timeTextView.text = model.timeString()
        holder.homeTeamTextView.text = model.homeTeam
        holder.scoreTextView.text = model.score.toString()
        holder.awayTeamTextView.text = model.awayTeam
        Log.e("Yeet", model.result.toString())
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