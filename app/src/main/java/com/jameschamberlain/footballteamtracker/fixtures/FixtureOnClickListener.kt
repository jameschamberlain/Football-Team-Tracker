package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.team

class FixtureOnClickListener internal constructor(private val parentFragment: Fragment, position: Int) : View.OnClickListener {
    private val currentFixture: Fixture = team.fixtures[position]
    override fun onClick(v: View) {
        loadFragment(FixtureDetailsFragment(), currentFixture)
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private fun loadFragment(fragment: Fragment, fixture: Fixture) {
        val bundle = Bundle()
        bundle.putParcelable("fixture", fixture)
        // set arguments
        fragment.arguments = bundle
        //This is required to communicate between two fragments. Similar to startActivityForResult
        fragment.setTargetFragment(parentFragment, FIXTURE_REQUEST)
        // load fragment
        val transaction = parentFragment.activity!!.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        private const val FIXTURE_REQUEST = 1
    }

}