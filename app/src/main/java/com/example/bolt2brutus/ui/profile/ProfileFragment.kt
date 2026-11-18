package com.example.bolt2brutus.ui.profile

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bolt2brutus.BuildConfig
import com.example.bolt2brutus.R
import com.example.bolt2brutus.ui.race.RaceActivity
import com.example.bolt2brutus.viewmodel.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
        val statueVisitsText = view.findViewById<TextView>(R.id.statueVisitsText)
        val usernameEditText = view.findViewById<EditText>(R.id.usernameEditText)

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            welcomeText.text = getString(R.string.signed_in_as, user?.username.orEmpty())
            val visits = user?.statueVisits.orEmpty()
            statueVisitsText.text = getString(R.string.statues_visited, visits.size, visits.values.sum())
        }

        view.findViewById<Button>(R.id.updateUsernameButton).setOnClickListener {
            profileViewModel.updateUsername(usernameEditText.text.toString())
            usernameEditText.text.clear()
        }

        view.findViewById<Button>(R.id.changeEmailButton).setOnClickListener {
            showChangeDialog(
                R.string.change_email,
                R.string.new_email,
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                profileViewModel::updateEmail,
            )
        }

        view.findViewById<Button>(R.id.changePasswordButton).setOnClickListener {
            showChangeDialog(
                R.string.change_password,
                R.string.new_password,
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
                profileViewModel::updatePassword,
            )
        }

        if (BuildConfig.DEBUG) {
            view.findViewById<View>(R.id.debugTools).visibility = View.VISIBLE
            view.findViewById<Button>(R.id.addTestDataButton).setOnClickListener {
                profileViewModel.addTestLeaderboardData()
            }
            view.findViewById<Button>(R.id.removeTestDataButton).setOnClickListener {
                profileViewModel.removeTestLeaderboardData()
            }
        }

        view.findViewById<Button>(R.id.logOutButton).setOnClickListener {
            profileViewModel.logOut()
        }

        view.findViewById<Button>(R.id.deleteAccountButton).setOnClickListener {
            profileViewModel.deleteAccount()
        }

        profileViewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                profileViewModel.messageShown()
            }
        }

        profileViewModel.signedOut.observe(viewLifecycleOwner) { signedOut ->
            if (signedOut) {
                (requireActivity() as RaceActivity).returnToLogin()
            }
        }
    }

    /** Asks for a new email or password plus the current password, then calls [onSubmit]. */
    private fun showChangeDialog(
        @StringRes title: Int,
        @StringRes newValueHint: Int,
        newValueInputType: Int,
        onSubmit: (currentPassword: String, newValue: String) -> Unit,
    ) {
        val content = layoutInflater.inflate(R.layout.dialog_change_account, null)
        val newValueEditText = content.findViewById<EditText>(R.id.newValueEditText).apply {
            setHint(newValueHint)
            inputType = newValueInputType
        }
        val currentPasswordEditText = content.findViewById<EditText>(R.id.currentPasswordEditText)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(content)
            .setPositiveButton(R.string.update) { _, _ ->
                onSubmit(currentPasswordEditText.text.toString(), newValueEditText.text.toString())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
