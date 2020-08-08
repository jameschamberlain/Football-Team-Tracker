package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.objects.Fixture

class FixtureOnClickListener
internal constructor(
        private val parentFragment: Fragment,
        private val adapter: FixtureAdapter,
        private val fixture: Fixture,
        private val position: Int
) : View.OnClickListener {


    override fun onClick(v: View) {

        loadFragment(FixtureDetailsFragment(), fixture, adapter.snapshots.getSnapshot(position).id)
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private fun loadFragment(fragment: Fragment, fixture: Fixture, id: String) {
        val bundle = Bundle()
        bundle.putParcelable("fixture", fixture)
        bundle.putString("id", id)
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