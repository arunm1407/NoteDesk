package com.example.notedesk.presentation.onBoarding.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.notedesk.presentation.onBoarding.FinishFragment
import com.example.notedesk.presentation.onBoarding.OnBoardingMainFragment
import com.example.notedesk.presentation.onBoarding.listener.Navigation
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.util.sharedPreference.SharedPreference
import com.example.notedesk.R
import com.example.notedesk.databinding.ActivityBoardingScreenBinding

class BoardingScreen : AppCompatActivity(), Navigation {


    private lateinit var binding: ActivityBoardingScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, OnBoardingMainFragment()).addToBackStack(
                    BackStack.HOME
                )
                    .commit()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }

    override fun navigate() {
        SharedPreference(this).putBooleanSharedPreference(Keys.ONBOARDING, true)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, FinishFragment()).addToBackStack(
                BackStack.HOME
            )
            commit()
        }

    }




}