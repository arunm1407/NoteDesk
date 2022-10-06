package com.example.notedesk.presentation.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notedesk.presentation.login.activity.LoginActivity
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.util.sharedPreference.SharedPreference
import com.example.notedesk.R
import com.example.notedesk.databinding.ActivitySplashScreen1Binding


class FlashScreen : AppCompatActivity() {


    private lateinit var appLogo: ImageView
    private lateinit var appName: TextView
    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation
    private lateinit var binding: ActivitySplashScreen1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreen1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initAnimation()
    }

    private fun initViews() {
        appLogo = binding.appLogo
        appName = binding.appName
        tv1 = binding.tv1
        tv2 = binding.tv2
    }

    private fun initAnimation() {

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_bottom_animation)
    }

    override fun onStart() {
        super.onStart()

        appLogo.animation = topAnimation
        appName.animation = bottomAnimation
        tv1.animation = bottomAnimation
        tv2.animation = bottomAnimation

//        if (checkOnBoardingCompleted()) {
//
//            startActivityNavigation(Intent(this, BoardingScreen::class.java))
//        } else {
//            startActivityNavigation(Intent(this, BoardingScreen::class.java))
////            startActivityNavigation(Intent(this, BoardingScreen::class.java))
//
//        }

//
        if (checkIsLogin()) {
//            startActivityNavigation(Intent(this, MainActivity::class.java))
            startActivityNavigation(Intent(this, LoginActivity::class.java))

        } else {
            startActivityNavigation(Intent(this, LoginActivity::class.java))

        }


    }


    private fun checkIsLogin(): Boolean {
        return SharedPreference(this).getBooleanSharedPreference(Keys.IS_LOGIN)

    }

    private fun startActivityNavigation(intent: Intent) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }, 1)
    }


    private fun checkOnBoardingCompleted(): Boolean {

        return SharedPreference(this).getBooleanSharedPreference(Keys.ONBOARDING)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}