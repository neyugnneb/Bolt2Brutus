package com.example.bolt2brutus.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bolt2brutus.R
import com.example.bolt2brutus.ui.race.RaceActivity
import com.example.bolt2brutus.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            if (signedOut) {
                (requireActivity() as RaceActivity).returnToLogin()
            }
        }
    }
}
