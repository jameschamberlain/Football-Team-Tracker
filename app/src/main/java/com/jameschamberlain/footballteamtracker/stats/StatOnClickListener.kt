package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jameschamberlain.footballteamtracker.objects.Player
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.team.PlayerDetailsFragment

class StatOnClickListener
internal constructor(
        private val parentFragment: Fragment,
        private val adapter: StatAdapter,
        private val player: Player,
        private val position: Int
) : View.OnClickListener {
    override fun onClick(v: View) {
        loadFragment(PlayerDetailsFragment(), player, adapter.snapshots.getSnapshot(position).id)
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private fun loadFragment(fragment: Fragment, player: Player, id: String) {
        val bundle = Bundle()
        bundle.putParcelable("player", player)
        bundle.putString("id", id)
        // set arguments
        fragment.arguments = bundle
        // load fragment
        val transaction = parentFragment.activity!!.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}