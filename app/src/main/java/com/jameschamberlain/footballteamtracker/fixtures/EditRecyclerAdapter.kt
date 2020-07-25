package com.jameschamberlain.footballteamtracker.fixtures

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.databinding.ItemPlayerEditBinding
import java.util.*

class EditRecyclerAdapter internal constructor(private val fixture: Fixture, private val isGoals: Boolean) : RecyclerView.Adapter<EditRecyclerAdapter.ViewHolder>() {
    private var players: ArrayList<String> = if (isGoals) {
        fixture.goalscorers
    } else {
        fixture.assists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPlayerEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = players[position]
        holder.nameTextView.text = player
        holder.clearImageView.setOnClickListener(EditOnClickListener(fixture, isGoals, player, this))
    }

    override fun getItemCount(): Int {
        return players.size
    }

    inner class ViewHolder(itemBinding: ItemPlayerEditBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var nameTextView: TextView = itemBinding.nameTextView
        var clearImageView: ImageView = itemBinding.clearImageView
    }

}