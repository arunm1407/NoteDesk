package com.example.notedesk.presentation.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.notedesk.presentation.login.activity.LoginActivity
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.util.sharedPreference.SharedPreference
import com.example.notedesk.R
import com.example.notedesk.databinding.ActivitySplashScreen1Binding
import com.example.notedesk.presentation.activity.MainActivity
import com.example.notedesk.presentation.onBoarding.activity.BoardingScreen
import com.example.notedesk.presentation.util.openActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class FlashScreen : AppCompatActivity() {


    private lateinit var appLogo: ImageView
    private lateinit var appName: TextView
    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation
    private lateinit var binding: ActivitySplashScreen1Binding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var handler:Handler
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



        if (checkIsLogin() != 0 && checkOnBoardingCompleted()) {
            startActivityNavigation(Intent(this, MainActivity::class.java))

        } else if (checkIsLogin() != 0 && !checkOnBoardingCompleted()) {
            startActivityNavigation(Intent(this, BoardingScreen::class.java))
        } else {
            startActivityNavigation(Intent(this, LoginActivity::class.java))
        }


    }


    private fun checkIsLogin(): Int {
        return SharedPreference(this).getSharedPreferenceInt(Keys.USER_ID)

    }

    private fun startActivityNavigation(intent: Intent) {
       handler= Handler(Looper.getMainLooper()).apply {
           postDelayed({
           openActivity(intent)
           finish()
       }, 2000)
       }
    }


    private fun checkOnBoardingCompleted(): Boolean {
        val flag: Boolean
        val userId = SharedPreference(this).getSharedPreferenceInt(Keys.USER_ID)
        if (userId == 0) return false
        runBlocking(Dispatchers.IO) { flag = viewModel.getOnBoardedStatus(userId) }
        return flag


    }


    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}