package com.jameschamberlain.footballteamtracker.team

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.objects.Player
import com.jameschamberlain.footballteamtracker.databinding.ItemPlayerDetailsBinding
import com.jameschamberlain.footballteamtracker.objects.AccountType
import java.util.*

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
        holder.goals.text = String.format(Locale.ENGLISH, "Goals: %d", model.goals)
        holder.assists.text = String.format(Locale.ENGLISH, "Assists: %d", model.assists)
        if (Utils.accountType == AccountType.ADMIN)
            holder.parentLayout.setOnClickListener(PlayerOnClickListener(parentFragment, this@PlayerAdapter, model, position))
    }

    inner class PlayerHolder(itemBinding: ItemPlayerDetailsBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var name: TextView = itemBinding.nameTextView
        var goals: TextView = itemBinding.goalsTextView
        var assists: TextView = itemBinding.assistsTextView
        var parentLayout: ConstraintLayout = itemBinding.root
    }

}