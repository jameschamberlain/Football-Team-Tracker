package com.jameschamberlain.footballteamtracker.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.objects.Player
import com.jameschamberlain.footballteamtracker.databinding.ItemPlayerRankingBinding
import com.jameschamberlain.footballteamtracker.objects.AccountType

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
        holder.rank.text = (position + 1).toString()
        holder.name.text = model.name
        if (isGoals) {
            holder.value.text = model.goals.toString()
        } else {
            holder.value.text = model.assists.toString()
        }
        if (Utils.accountType == AccountType.ADMIN)
            holder.parentLayout.setOnClickListener {
                val playerId = this.snapshots.getSnapshot(position).id
                val action = StatsFragmentDirections
                        .actionNavigationStatsToNavigationPlayerDetails(model, playerId)
                NavHostFragment
                        .findNavController(parentFragment)
                        .navigate(action)
            }
    }

    inner class StatHolder(itemBinding: ItemPlayerRankingBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var rank: TextView = itemBinding.rankTextView
        var name: TextView = itemBinding.nameTextView
        var value: TextView = itemBinding.valueTextView
        var parentLayout: ConstraintLayout = itemBinding.root
    }

}