package com.example.currencyconverter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageView = findViewById<ImageView>(R.id.splashImage)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        imageView.startAnimation(fadeInAnimation)

        // Timer untuk splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Pindah ke MainActivity setelah 3 detik
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Hapus SplashActivity dari stack
        }, 3000) // 3000 ms = 3 detik
    }
}
