package com.announce.presenter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }
}