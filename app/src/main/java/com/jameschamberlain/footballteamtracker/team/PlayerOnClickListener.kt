package com.jameschamberlain.footballteamtracker.team

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jameschamberlain.footballteamtracker.Player
import com.jameschamberlain.footballteamtracker.R

class PlayerOnClickListener(private val parentFragment: Fragment, private val currentPlayer: Player) : View.OnClickListener {
    override fun onClick(v: View) {
        loadFragment(PlayerDetailsFragment(), currentPlayer)
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private fun loadFragment(fragment: Fragment, player: Player) {
        val bundle = Bundle()
        bundle.putParcelable("player", player)
        // set arguments
        fragment.arguments = bundle
        // load fragment
        val transaction = parentFragment.activity!!.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}