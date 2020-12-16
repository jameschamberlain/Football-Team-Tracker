package com.jameschamberlain.footballteamtracker.team

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.BaseFragment
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.adapters.PlayerAdapter
import com.jameschamberlain.footballteamtracker.databinding.FragmentTeamBinding
import com.jameschamberlain.footballteamtracker.data.AccountType
import com.jameschamberlain.footballteamtracker.data.Player
import com.jameschamberlain.footballteamtracker.data.Team
import com.jameschamberlain.footballteamtracker.viewmodels.PlayersViewModel
import java.util.*


private const val TAG = "TeamFragment"


class TeamFragment : BaseFragment(), View.OnClickListener {

    private lateinit var adapter: PlayerAdapter
    private lateinit var teamName: String

    private var _binding: FragmentTeamBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: PlayersViewModel by activityViewModels()

    private val maxNameLength = 20


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentTeamBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.appbar.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        setHasOptionsMenu(true)


        teamName = Team.name

        val options = FirestoreRecyclerOptions.Builder<Player>()
                .setSnapshotArray(viewModel.players)
                .setLifecycleOwner(this@TeamFragment)
                .build()
        adapter = PlayerAdapter(options, this@TeamFragment)
        binding.playersRecyclerView.adapter = adapter

        if (Utils.accountType == AccountType.ADMIN) {
            binding.fab.setOnClickListener(this)
        } else {
            binding.fab.visibility = View.GONE
        }
    }

    fun addNoPlayersLayout() {
        binding.noPlayersLayout.visibility = View.VISIBLE
        if (Utils.accountType == AccountType.ADMIN)
            binding.noPlayersTextView.text = getString(R.string.no_players_manager_desc)
    }

    fun removeNoPlayersLayout() { binding.noPlayersLayout.visibility = View.GONE }

    override fun onClick(v: View) {
        val editText = EditText(v.context)
        editText.filters = arrayOf<InputFilter>(LengthFilter(maxNameLength))
        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        editText.hint = getString(R.string.name)
        val scale = resources.displayMetrics.density
        val dpAsPixels = (20 * scale + 0.5f).toInt()
        val container = FrameLayout(requireContext())
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, 0)
        editText.layoutParams = lp
        container.addView(editText)
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.add_player))
                .setView(container)
                .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                    val name = editText.text.toString()
                    val playersRef = Utils.teamRef.collection("players")
                    playersRef.document(name.toLowerCase(Locale.ROOT)).get()
                            .addOnCompleteListener { doc ->
                                if (doc.isSuccessful) {
                                    if (!doc.result!!.exists()) {
                                        playersRef.document(name.toLowerCase(Locale.ROOT)).set(Player(name))
                                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully added") }
                                                .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
                                    } else {
                                        Toast.makeText(requireContext(), "Can't add player: \"$name\" already exists.", Toast.LENGTH_LONG).show()
                                    }
                                }
                                else {
                                    Toast.makeText(requireContext(), "Can't add player: network error.", Toast.LENGTH_LONG).show()
                                }
                            }
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_team_code -> {
                completeTeamCodeAction()
                true
            }
            R.id.action_settings -> {
                val action = TeamFragmentDirections
                        .actionTeamFragmentToSettingsFragment()
                NavHostFragment
                        .findNavController(this@TeamFragment)
                        .navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}