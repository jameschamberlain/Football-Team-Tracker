package com.jameschamberlain.footballteamtracker.fixtures

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.databinding.ItemPlayerStatBinding
import java.util.*

class SimpleRecyclerAdapter internal constructor(private val players: ArrayList<String>, isGoals: Boolean, context: Context) : RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder>() {
    private val uniquePlayers = ArrayList<String?>()
    private val isGoals: Boolean
    var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPlayerStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
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

    inner class ViewHolder(itemBinding: ItemPlayerStatBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var name: TextView = itemBinding.nameTextView
        var icon: ImageView = itemBinding.icon
        var numOfGoals: TextView = itemBinding.numGoalsTextView

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