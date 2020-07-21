package com.jameschamberlain.footballteamtracker.fixtures

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EditOnClickListener internal constructor(private val fixture: Fixture, private val isGoals: Boolean, private val player: String, private val adapter: RecyclerView.Adapter<*>) : View.OnClickListener {
    override fun onClick(v: View) {
        if (isGoals) {
            fixture.goalscorers.remove(player)
        } else {
            fixture.assists.remove(player)
        }
        adapter.notifyDataSetChanged()
    }

}