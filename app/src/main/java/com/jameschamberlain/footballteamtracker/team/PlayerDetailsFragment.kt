package com.jameschamberlain.footballteamtracker.team

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.objects.Player
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentPlayerDetailsBinding
import com.jameschamberlain.footballteamtracker.objects.AccountType
import java.util.*

private const val TAG = "PlayerDetailsFragment"


class PlayerDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPlayerDetailsBinding

    private lateinit var player: Player
    /**
     * The id of the selected player.
     */
    private lateinit var playerId: String

    private val args: PlayerDetailsFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().findViewById<View>(R.id.nav_view).visibility = View.GONE
        val containerLayout = requireActivity().findViewById<FrameLayout>(R.id.nav_host_fragment)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        containerLayout.layoutParams = params

        player = args.player
        playerId = args.id

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""

        binding.nameTextView.text = player.name
        binding.goalsTextView.text = String.format(Locale.ENGLISH, "%d", player.goals)
        binding.assistsTextView.text = String.format(Locale.ENGLISH, "%d", player.assists)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (Utils.accountType == AccountType.ADMIN)
            inflater.inflate(R.menu.player_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // User chose the "Back" item, go back.
                NavHostFragment.findNavController(this@PlayerDetailsFragment).navigateUp()
                true
            }
            R.id.action_delete -> {
                // User chose the "Delete" action, delete the fixture.
                MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.delete_this_player))
                        .setPositiveButton(getString(R.string.delete)) { _, _ ->
                            Utils.teamRef.collection("players").document(playerId)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                        val fm = requireActivity().supportFragmentManager
                                        if (fm.backStackEntryCount > 0) {
                                            fm.popBackStack()
                                        }
                                    }
                                    .addOnFailureListener {
                                        e -> Log.w(TAG, "Error deleting document", e)
                                        Toast.makeText(activity, "Error deleting document", Toast.LENGTH_SHORT).show()
                                    }
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                true
            }
            else ->                 // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        Utils.showBottomNav(requireActivity())
    }
}