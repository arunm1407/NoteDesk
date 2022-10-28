package com.example.version2.presentation.splashScreen

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.version2.R
import com.example.version2.databinding.ActivityFlashScreen1Binding
import com.example.version2.presentation.common.NoteScreen
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.login.LoginViewModel
import com.example.version2.presentation.login.activity.LoginActivity
import com.example.version2.presentation.onBoarding.activity.BoardingScreen
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.openActivity
import com.example.version2.presentation.util.sharedPreference.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class FlashScreen : AppCompatActivity() {
    private lateinit var appLogo: ImageView
    private lateinit var appName: TextView
    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation
    private lateinit var binding: ActivityFlashScreen1Binding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(
            this,
            (application as NotesApplication).loginFactory
        )[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashScreen1Binding.inflate(layoutInflater)
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

        topAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                nextAction()
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })


    }


    private fun nextAction() {
        if (checkIsLogin() != 0 && checkOnBoardingCompleted()) {
            startActivityNavigation(Intent(this, NoteScreen::class.java))

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

        openActivity(intent)
        finish()

    }


    private fun checkOnBoardingCompleted(): Boolean {
        val flag: Boolean
        val userId = SharedPreference(this).getSharedPreferenceInt(Keys.USER_ID)
        if (userId == 0) return false
        runBlocking(Dispatchers.IO) { flag = viewModel.getOnBoardedStatus(userId) }
        return flag


    }


}