package com.announce.presenter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.announce.R
import com.google.android.gms.wallet.AutoResolveHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e("UncaughtException", e.message.toString(), e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1) {
            when(resultCode) {
                RESULT_OK -> {
                    showToast("OK")
                }
                RESULT_CANCELED -> {
                    showToast("Canceled")
                }
                AutoResolveHelper.RESULT_ERROR -> {
                    showToast("Error")
                }
            }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG)
            .show()
    }
}