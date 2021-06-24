package com.kotlin.mvvm.structure.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.kotlin.mvvm.structure.R
import com.kotlin.mvvm.structure.base.BaseActivity
import com.kotlin.mvvm.structure.databinding.ActivitySplashBinding
import com.kotlin.mvvm.structure.utils.AppConstants


class ActivitySplash : BaseActivity() {

    private lateinit var activitySplashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySplashBinding = putContentView(R.layout.activity_splash) as ActivitySplashBinding

        //Setting up the toolbar configuration
        setToolbarConfiguration(false)

        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this@ActivitySplash, ActivityDashboard::class.java)
            startActivity(i)
            finishAffinity()

        }, 2000)

    }

}