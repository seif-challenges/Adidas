package tn.seif.adidaschallenge.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tn.seif.adidaschallenge.ui.main.MainActivity
import tn.seif.adidaschallenge.ui.splash.SplashActivity.Companion.SPLASH_DELAY_MS

/**
 * Activity representing a starting point of the application.
 * Makes a delay of [SPLASH_DELAY_MS] then starts the [MainActivity].
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            delay(SPLASH_DELAY_MS)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }
    }

    companion object {
        private const val SPLASH_DELAY_MS = 1000L
    }
}