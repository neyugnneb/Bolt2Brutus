package com.example.bolt2brutus.ui.race

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.bolt2brutus.R
import com.example.bolt2brutus.ui.auth.LoginActivity
import com.example.bolt2brutus.ui.challenge.ChallengesFragment
import com.example.bolt2brutus.ui.leaderboard.LeaderboardFragment
import com.example.bolt2brutus.ui.map.StatueMapFragment
import com.example.bolt2brutus.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/** Hosts the signed-in screens. Returns to [LoginActivity] when the user signs out. */
class RaceActivity : AppCompatActivity() {

    companion object {
        private const val tag = "RaceActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
        setContentView(R.layout.activity_race)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            findViewById<View>(R.id.flFragment).setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            findViewById<View>(R.id.bottomNavigationView).setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_races -> {
                    showFragment(RaceFragment())
                    true
                }
                R.id.nav_leaderboard -> {
                    showFragment(LeaderboardFragment())
                    true
                }
                R.id.nav_challenges -> {
                    showFragment(ChallengesFragment())
                    true
                }
                R.id.nav_map -> {
                    showFragment(StatueMapFragment())
                    true
                }
                R.id.nav_profile -> {
                    showFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            showFragment(RaceFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

    fun returnToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
