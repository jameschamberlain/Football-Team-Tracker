package com.jameschamberlain.footballteamtracker.fixtures

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.R
import java.util.*

class SimpleRecyclerAdapter internal constructor(private val players: ArrayList<String>, isGoals: Boolean, context: Context) : RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder>() {
    private val uniquePlayers = ArrayList<String?>()
    private val isGoals: Boolean
    var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_stat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = uniquePlayers[position]
        holder.name.text = player
        if (isGoals) {
            holder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_football))
        } else {
            holder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_shoe))
        }
        val numGoalsText: String = "x" + Collections.frequency(players, player)
        holder.numOfGoals.text = numGoalsText
    }

    override fun getItemCount(): Int {
        return uniquePlayers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name_text_view)
        var icon: ImageView = itemView.findViewById(R.id.icon)
        var numOfGoals: TextView = itemView.findViewById(R.id.num_goals_text_view)

    }

    init {
        for (player in players) {
            if (!uniquePlayers.contains(player)) {
                uniquePlayers.add(player)
            }
        }
        this.isGoals = isGoals
        this.context = context
    }
}