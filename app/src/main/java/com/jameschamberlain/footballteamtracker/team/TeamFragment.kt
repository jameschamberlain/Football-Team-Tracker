package com.jameschamberlain.footballteamtracker.team

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.MenuFragment
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentTeamBinding
import com.jameschamberlain.footballteamtracker.objects.AccountType
import com.jameschamberlain.footballteamtracker.objects.Player
import java.util.*


private const val TAG = "TeamFragment"


class TeamFragment : MenuFragment(), View.OnClickListener {

    private lateinit var adapter: PlayerAdapter
    private lateinit var teamName: String

    private lateinit var binding: FragmentTeamBinding

    private val maxNameLength = 20


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Utils.showBottomNav(requireActivity())

        binding = FragmentTeamBinding.inflate(layoutInflater)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        setHasOptionsMenu(true)


        teamName = Utils.getTeamNameTest()

        val playersRef = Utils.teamRef.collection("players")
        val query: Query = playersRef
        val options = FirestoreRecyclerOptions.Builder<Player>()
                .setQuery(query, Player::class.java)
                .build()
        adapter = PlayerAdapter(options, this@TeamFragment)

        binding.playersRecyclerView.adapter = adapter
        binding.playersRecyclerView.setHasFixedSize(true)
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(activity)

        if (Utils.accountType == AccountType.ADMIN) {
            binding.fab.setOnClickListener(this)
        } else {
            binding.fab.visibility = View.GONE
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    fun addNoPlayersLayout() {
        binding.noPlayersLayout.visibility = View.VISIBLE
    }

    fun removeNoPlayersLayout() {
        binding.noPlayersLayout.visibility = View.GONE
    }

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
                    playersRef.document(name.toLowerCase(Locale.ROOT)).set(Player(name))
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully added") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }

                    adapter.notifyDataSetChanged()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}