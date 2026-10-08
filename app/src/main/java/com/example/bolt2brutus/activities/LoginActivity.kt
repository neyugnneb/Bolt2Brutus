package com.example.bolt2brutus.activities

import android.util.Log
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bolt2brutus.LoginFragment
import com.example.bolt2brutus.R
import com.example.bolt2brutus.SignupFragment

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val tag = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginFragment = LoginFragment()
        val signupFragment = SignupFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, loginFragment)
            commit()
        }

        findViewById<Button>(R.id.logInButton).setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, loginFragment)
                commit()
            }
        }

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, signupFragment)
                commit()
            }
        }

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