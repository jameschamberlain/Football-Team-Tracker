package com.jameschamberlain.footballteamtracker.fixtures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.R
import java.util.*

class EditRecyclerAdapter internal constructor(private val fixture: Fixture, private val isGoals: Boolean) : RecyclerView.Adapter<EditRecyclerAdapter.ViewHolder>() {
    private var players: ArrayList<String> = if (isGoals) {
        fixture.goalscorers
    } else {
        fixture.assists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_edit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = players[position]
        holder.name.text = player
        holder.imageView.setOnClickListener(EditOnClickListener(fixture, isGoals, player, this))
    }

    override fun getItemCount(): Int {
        return players.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.clear_image_view)
        var name: TextView = itemView.findViewById(R.id.name_text_view)

    }

}