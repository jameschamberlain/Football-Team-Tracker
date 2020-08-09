package com.jameschamberlain.footballteamtracker.team

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Query
import com.jameschamberlain.footballteamtracker.objects.Player
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentTeamBinding
import java.util.*


private const val TAG = "TeamFragment"


class TeamFragment : Fragment(), View.OnClickListener {

    private lateinit var adapter: PlayerAdapter
    private lateinit var teamName: String

    private lateinit var binding: FragmentTeamBinding

    private val maxNameLength = 15


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Select correct bottom nav item.
        val navView: BottomNavigationView = activity!!.findViewById(R.id.bottom_nav)
        navView.menu.getItem(3).isChecked = true
        navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
        navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
        navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
        navView.menu.getItem(3).setIcon(R.drawable.ic_strategy)
        activity!!.findViewById<View>(R.id.bottom_nav).visibility = View.VISIBLE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        val pixels = 56 * context!!.resources.displayMetrics.density
        params.setMargins(0, 0, 0, pixels.toInt())
        containerLayout.layoutParams = params

        binding = FragmentTeamBinding.inflate(layoutInflater)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""


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

        binding.fab.setOnClickListener(this)
//        checkTeamHasPlayers()

        // Inflate the layout for this fragment
        return binding.root
    }

    fun addNoPlayersLayout() { binding.noPlayersLayout.visibility = View.VISIBLE }

    fun removeNoPlayersLayout() { binding.noPlayersLayout.visibility = View.GONE }

    override fun onClick(v: View) {
        val editText = EditText(v.context)
        editText.filters = arrayOf<InputFilter>(LengthFilter(maxNameLength))
        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        editText.hint = getString(R.string.name)
        val scale = resources.displayMetrics.density
        val dpAsPixels = (20 * scale + 0.5f).toInt()
        val container = FrameLayout(context!!)
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, 0)
        editText.layoutParams = lp
        container.addView(editText)
        MaterialAlertDialogBuilder(context)
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