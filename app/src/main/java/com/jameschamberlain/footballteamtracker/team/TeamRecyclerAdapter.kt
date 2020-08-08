package com.jameschamberlain.footballteamtracker.team

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.objects.Player
import com.jameschamberlain.footballteamtracker.databinding.ItemPlayerDetailsBinding
import java.util.*

class TeamRecyclerAdapter internal constructor(private val players: ArrayList<Player>, private val parentFragment: Fragment) : RecyclerView.Adapter<TeamRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPlayerDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPlayer = players[position]
        holder.name.text = currentPlayer.name
        holder.goals.text = String.format(Locale.ENGLISH, "Goals: %d", currentPlayer.goals)
        holder.assists.text = String.format(Locale.ENGLISH, "Assists: %d", currentPlayer.assists)
//        holder.parentLayout.setOnClickListener(PlayerOnClickListener(parentFragment, currentPlayer))
    }

    override fun getItemCount(): Int {
        return players.size
    }

    inner class ViewHolder(itemBinding: ItemPlayerDetailsBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var name: TextView = itemBinding.nameTextView
        var goals: TextView = itemBinding.goalsTextView
        var assists: TextView = itemBinding.assistsTextView
        var parentLayout: ConstraintLayout = itemBinding.root

    }

}