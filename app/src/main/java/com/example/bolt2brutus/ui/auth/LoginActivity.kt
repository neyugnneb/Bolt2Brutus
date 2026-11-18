package com.example.bolt2brutus.ui.auth

import android.content.Intent
import android.util.Log
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.bolt2brutus.R
import com.example.bolt2brutus.ui.race.RaceActivity
import com.example.bolt2brutus.viewmodel.AuthUiState
import com.example.bolt2brutus.viewmodel.AuthViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val tag = "LoginActivity"
    }

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")

        if (authViewModel.isSignedIn) {
            openRaceScreen()
            return
        }

        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            showFragment(LoginFragment())
        }

        findViewById<Button>(R.id.logInButton).setOnClickListener {
            showFragment(LoginFragment())
        }

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            showFragment(SignupFragment())
        }

        authViewModel.uiState.observe(this) { state ->
            when (state) {
                is AuthUiState.SignedIn -> openRaceScreen()
                is AuthUiState.VerifyEmail -> {
                    showVerifyEmailDialog(state)
                    authViewModel.messageShown()
                }
                is AuthUiState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    authViewModel.messageShown()
                }
                else -> Unit
            }
        }
    }

    private fun showVerifyEmailDialog(state: AuthUiState.VerifyEmail) {
        if (supportFragmentManager.findFragmentById(R.id.flFragment) is SignupFragment) {
            showFragment(LoginFragment())
        }
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.verify_email_title)
            .setPositiveButton(android.R.string.ok, null)
        if (state.sent) {
            builder.setMessage(getString(R.string.verify_email_sent, state.email))
        } else {
            builder.setMessage(getString(R.string.verify_email_needed, state.email))
                .setNeutralButton(R.string.verify_email_resend) { _, _ -> authViewModel.resendVerification() }
        }
        builder.show()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

    private fun openRaceScreen() {
        startActivity(Intent(this, RaceActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy")
    }


}
