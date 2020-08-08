package com.jameschamberlain.footballteamtracker.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.objects.Player
import com.jameschamberlain.footballteamtracker.databinding.ItemPlayerRankingBinding
import java.util.*

class StatRecyclerAdapter internal constructor(private val players: ArrayList<Player>, private val isGoals: Boolean, private val parentFragment: Fragment) : RecyclerView.Adapter<StatRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPlayerRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPlayer = players[position]
        holder.rank.text = String.format(Locale.ENGLISH, "%d", players.indexOf(currentPlayer) + 1)
        holder.name.text = currentPlayer.name
        if (isGoals) {
            holder.value.text = String.format(Locale.ENGLISH, "%d", currentPlayer.goals)
        } else {
            holder.value.text = String.format(Locale.ENGLISH, "%d", currentPlayer.assists)
        }
//        holder.parentLayout.setOnClickListener(PlayerOnClickListener(parentFragment, currentPlayer))
    }

    override fun getItemCount(): Int {
        return players.size
    }

    inner class ViewHolder(itemBinding: ItemPlayerRankingBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var rank: TextView = itemBinding.rankTextView
        var name: TextView = itemBinding.nameTextView
        var value: TextView = itemBinding.valueTextView
        var parentLayout: ConstraintLayout = itemBinding.root

    }

}