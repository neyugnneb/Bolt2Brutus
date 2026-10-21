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
                is AuthUiState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    authViewModel.errorShown()
                }
                else -> Unit
            }
        }
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
