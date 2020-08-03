package com.jameschamberlain.footballteamtracker.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.Player
import com.jameschamberlain.footballteamtracker.databinding.ItemPlayerDetailsBinding
import com.jameschamberlain.footballteamtracker.databinding.ItemPlayerRankingBinding
import com.jameschamberlain.footballteamtracker.team.PlayerOnClickListener
import java.util.*

class StatAdapter(options: FirestoreRecyclerOptions<Player>, private val isGoals: Boolean, private val parentFragment: StatsFragment) : FirestoreRecyclerAdapter<Player, StatAdapter.StatHolder>(options) {

    override fun onDataChanged() {
        if (itemCount == 0)
            parentFragment.addNoStatsLayout()
        else
            parentFragment.removeNoStatsLayout()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatHolder {
        val itemBinding = ItemPlayerRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: StatHolder, position: Int, model: Player) {
        holder.rank.text = String.format(Locale.ENGLISH, "%d", position + 1)
        holder.name.text = model.name
        if (isGoals) {
            holder.value.text = String.format(Locale.ENGLISH, "%d", model.goals)
        } else {
            holder.value.text = String.format(Locale.ENGLISH, "%d", model.assists)
        }
        holder.parentLayout.setOnClickListener(StatOnClickListener(parentFragment, this@StatAdapter, model, position))
    }

    inner class StatHolder(itemBinding: ItemPlayerRankingBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var rank: TextView = itemBinding.rankTextView
        var name: TextView = itemBinding.nameTextView
        var value: TextView = itemBinding.valueTextView
        var parentLayout: ConstraintLayout = itemBinding.root
    }

}