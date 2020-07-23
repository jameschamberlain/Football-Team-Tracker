package com.jameschamberlain.footballteamtracker.team

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
import java.util.*

class TeamRecyclerAdapter internal constructor(private val players: ArrayList<Player>, private val parentFragment: Fragment) : RecyclerView.Adapter<TeamRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_details, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPlayer = players[position]
        holder.name.text = currentPlayer.name
        holder.goals.text = String.format(Locale.ENGLISH, "Goals: %d", currentPlayer.goals)
        holder.assists.text = String.format(Locale.ENGLISH, "Assists: %d", currentPlayer.assists)
        holder.parentLayout.setOnClickListener(PlayerOnClickListener(parentFragment, currentPlayer))
    }

    override fun getItemCount(): Int {
        return players.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name_text_view)
        var goals: TextView = itemView.findViewById(R.id.goals_text_view)
        var assists: TextView = itemView.findViewById(R.id.assists_text_view)
        var parentLayout: ConstraintLayout = itemView.findViewById(R.id.parent_layout)

    }

}