package com.example.version2.presentation.onBoarding.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.sharedPreference.SharedPreference
import com.example.version2.R
import com.example.version2.databinding.ActivityBoardingScreenBinding
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.login.LoginViewModel
import com.example.version2.presentation.onBoarding.FinishFragment
import com.example.version2.presentation.onBoarding.MainFragment
import com.example.version2.presentation.onBoarding.listener.Navigation
import com.example.version2.presentation.util.BackStack
import com.example.version2.presentation.util.inTransaction
import com.example.version2.presentation.util.openActivity

class BoardingScreen : AppCompatActivity(), Navigation {

    private lateinit var binding: ActivityBoardingScreenBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(
            this,
            (application as NotesApplication).loginFactory
        )[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction(BackStack.HOME) {
                replace(R.id.fragmentContainerView, MainFragment())
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
        viewModel.setOnBoardedStatus(
            true,
            SharedPreference(this).getSharedPreferenceInt(Keys.USER_ID)
        )
        openActivity(intent)
        finish()
    }

}