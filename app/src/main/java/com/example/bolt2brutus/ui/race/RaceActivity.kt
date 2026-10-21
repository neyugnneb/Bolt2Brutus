package com.example.bolt2brutus.ui.race

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bolt2brutus.R
import com.example.bolt2brutus.ui.auth.LoginActivity

/** Hosts the signed-in screens. Returns to [LoginActivity] when the user signs out. */
class RaceActivity : AppCompatActivity() {

    companion object {
        private const val tag = "RaceActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
        setContentView(R.layout.activity_race)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, RaceFragment())
                commit()
            }
        }
    }

    fun returnToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
