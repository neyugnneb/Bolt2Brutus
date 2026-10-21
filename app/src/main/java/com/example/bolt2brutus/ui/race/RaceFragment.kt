package com.example.bolt2brutus.ui.race

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolt2brutus.R
import com.example.bolt2brutus.model.Race
import com.example.bolt2brutus.viewmodel.ProfileViewModel
import com.example.bolt2brutus.viewmodel.RaceViewModel

/** Profile header, race picker, and the user's race attempts. */
class RaceFragment : Fragment() {

    companion object {
        private const val tag = "RaceFragment"
    }

    private val raceViewModel: RaceViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private var races: List<Race> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(tag, "onCreateView")
        return inflater.inflate(R.layout.fragment_race, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindProfile(view)
        bindRaces(view)
    }

    private fun bindProfile(view: View) {
        val welcomeText = view.findViewById<TextView>(R.id.welcomeText)
        val usernameEditText = view.findViewById<EditText>(R.id.usernameEditText)

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            welcomeText.text = getString(R.string.signed_in_as, user?.username.orEmpty())
        }
        view.findViewById<Button>(R.id.updateUsernameButton).setOnClickListener {
            profileViewModel.updateUsername(usernameEditText.text.toString())
            usernameEditText.text.clear()
        }
        view.findViewById<Button>(R.id.logOutButton).setOnClickListener {
            profileViewModel.logOut()
        }
        view.findViewById<Button>(R.id.deleteAccountButton).setOnClickListener {
            profileViewModel.deleteAccount()
        }

        profileViewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                profileViewModel.messageShown()
            }
        }
        profileViewModel.signedOut.observe(viewLifecycleOwner) { signedOut ->
            if (signedOut) (requireActivity() as RaceActivity).returnToLogin()
        }
    }

    private fun bindRaces(view: View) {
        val raceSpinner = view.findViewById<Spinner>(R.id.raceSpinner)
        val spinnerAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, mutableListOf()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        raceSpinner.adapter = spinnerAdapter

        raceViewModel.races.observe(viewLifecycleOwner) { list ->
            races = list
            spinnerAdapter.clear()
            spinnerAdapter.addAll(list.map { it.raceName })
        }

        view.findViewById<Button>(R.id.startRaceButton).setOnClickListener {
            races.getOrNull(raceSpinner.selectedItemPosition)?.let(raceViewModel::startRace)
        }

        val attemptAdapter = RaceAttemptAdapter(
            onFinish = raceViewModel::finishRace,
            onDelete = raceViewModel::deleteAttempt,
        )
        view.findViewById<RecyclerView>(R.id.attemptsRecyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = attemptAdapter
        }
        raceViewModel.myAttempts.observe(viewLifecycleOwner, attemptAdapter::submitList)

        raceViewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                raceViewModel.messageShown()
            }
        }
    }
}
