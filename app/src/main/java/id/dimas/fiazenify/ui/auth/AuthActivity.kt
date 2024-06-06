package id.dimas.fiazenify.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.R
import id.dimas.fiazenify.databinding.ActivityAuthBinding

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = true


        supportFragmentManager.beginTransaction().apply {
            replace(R.id.auth_nav_host, LoginFragment())
            commit()
        }
    }
}