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
import com.example.notedesk.presentation.activity.MainActivity
import com.example.notesappfragment.R


class FlashScreen : AppCompatActivity() {


    private lateinit var appLogo: ImageView
    private lateinit var appName: TextView
    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen1)
        initViews()
        initAnimation()
    }

    private fun initViews() {
        appLogo = findViewById(R.id.app_logo)
        appName = findViewById(R.id.app_name)
        tv1 = findViewById(R.id.tv1)
        tv2 = findViewById(R.id.tv2)
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

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        },  2000)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}