package com.jameschamberlain.footballteamtracker.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.Player
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.team.PlayerOnClickListener
import java.util.*

class StatRecyclerAdapter internal constructor(private val players: ArrayList<Player>, private val isGoals: Boolean, private val parentFragment: Fragment) : RecyclerView.Adapter<StatRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_ranking, parent, false)
        return ViewHolder(view)
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
        holder.parentLayout.setOnClickListener(PlayerOnClickListener(parentFragment, currentPlayer))
    }

    override fun getItemCount(): Int {
        return players.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rank: TextView = itemView.findViewById(R.id.rank_text_view)
        var name: TextView = itemView.findViewById(R.id.name_text_view)
        var value: TextView = itemView.findViewById(R.id.value_text_view)
        var parentLayout: ConstraintLayout = itemView.findViewById(R.id.parent_layout)

    }

}