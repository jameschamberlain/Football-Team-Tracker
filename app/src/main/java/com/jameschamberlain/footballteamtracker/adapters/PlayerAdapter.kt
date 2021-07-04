package com.jameschamberlain.footballteamtracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.data.Player
import com.jameschamberlain.footballteamtracker.databinding.ItemPlayerDetailsBinding
import com.jameschamberlain.footballteamtracker.data.AccountType
import com.jameschamberlain.footballteamtracker.team.TeamFragment
import com.jameschamberlain.footballteamtracker.team.TeamFragmentDirections

class PlayerAdapter(options: FirestoreRecyclerOptions<Player>, private val parentFragment: TeamFragment) : FirestoreRecyclerAdapter<Player, PlayerAdapter.PlayerHolder>(options) {

    override fun onDataChanged() {
        if (itemCount == 0)
            parentFragment.addNoPlayersLayout()
        else
            parentFragment.removeNoPlayersLayout()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
        val itemBinding = ItemPlayerDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PlayerHolder, position: Int, model: Player) {
        holder.name.text = model.name
        holder.goals.text = parentFragment.getString(R.string.goals_stats, model.goals)
        holder.assists.text = parentFragment.getString(R.string.assists_stats, model.assists)
        if (Utils.getAccountType(parentFragment.requireActivity()) == AccountType.ADMIN)
            holder.parentLayout.setOnClickListener {
                val playerId = this.snapshots.getSnapshot(position).id
                val action = TeamFragmentDirections.actionTeamFragmentToPlayerDetailsFragment(model, playerId)
                NavHostFragment
                        .findNavController(parentFragment)
                        .navigate(action)
            }
    }

    inner class PlayerHolder(itemBinding: ItemPlayerDetailsBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var name: TextView = itemBinding.nameTextView
        var goals: TextView = itemBinding.goalsTextView
        var assists: TextView = itemBinding.assistsTextView
        var parentLayout: ConstraintLayout = itemBinding.root
    }

}