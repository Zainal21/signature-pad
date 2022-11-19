package com.muhamadzain.signature_pad.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.muhamadzain.signature_pad.R
import com.muhamadzain.signature_pad.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun getLayoutResource(): Int = R.layout.activity_splash
    private val SPLASH_TIME_OUT = 2000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(
            {
                val intentActivity = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intentActivity)
                finish()
            }, SPLASH_TIME_OUT)
    }
}