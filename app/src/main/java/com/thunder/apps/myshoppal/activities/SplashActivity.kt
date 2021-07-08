package com.thunder.apps.myshoppal.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.firestore.FireStoreClass

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        Handler(Looper.myLooper()!!).postDelayed({
            if(FireStoreClass().getCurrentUserId().isNotEmpty()){
                startActivity(Intent(this,DashboardActivity::class.java))
            }else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        },2000)
    }
}