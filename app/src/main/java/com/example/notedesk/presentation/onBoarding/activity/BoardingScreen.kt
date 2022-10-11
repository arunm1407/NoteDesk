package com.example.notedesk.presentation.onBoarding.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.notedesk.presentation.onBoarding.FinishFragment
import com.example.notedesk.presentation.onBoarding.OnBoardingMainFragment
import com.example.notedesk.presentation.onBoarding.listener.Navigation
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.R
import com.example.notedesk.databinding.ActivityBoardingScreenBinding
import com.example.notedesk.presentation.onBoarding.OnBoardingViewModel
import com.example.notedesk.presentation.util.inTransaction
import com.example.notedesk.presentation.util.openActivity
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.util.sharedPreference.SharedPreference

class BoardingScreen : AppCompatActivity(), Navigation {


    private lateinit var binding: ActivityBoardingScreenBinding
    private val viewModel: OnBoardingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction(BackStack.HOME) {
                replace(R.id.fragmentContainerView, OnBoardingMainFragment())
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
        supportFragmentManager.inTransaction(BackStack.HOME)
        {
            replace(R.id.fragmentContainerView, FinishFragment())
        }

    }

    override fun navigateScreen(intent: Intent) {
        viewModel.setOnBoarded(SharedPreference(this).getSharedPreferenceInt(Keys.USER_ID))
        openActivity(intent)
        finish()
    }


}