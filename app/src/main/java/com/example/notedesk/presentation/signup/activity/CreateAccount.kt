package com.example.notedesk.presentation.signup.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.notedesk.presentation.signup.AccountDetailsFragment
import com.example.notedesk.presentation.signup.listener.Navigate
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.R
import com.example.notedesk.databinding.ActivityCreateAccountBinding
import com.example.notedesk.presentation.login.listener.Navigation
import com.example.notedesk.presentation.util.inTransaction
import com.example.notedesk.presentation.util.openActivity
import com.shuhart.stepview.StepView


class CreateAccount : AppCompatActivity(), Navigate, Navigation {


    private lateinit var binding: ActivityCreateAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction(null) {
                replace(
                    R.id.fragmentContainerView,
                    AccountDetailsFragment()
                )
            }
        }
        setStateForStepView()
        navigationListener()

    }

    private fun navigationListener() {
        binding.ivImage.setOnClickListener {
            finish()
        }
    }

    override fun navigate(fragment: Fragment) {

        supportFragmentManager.inTransaction(null) {
            replace(R.id.fragmentContainerView, fragment).addToBackStack(
                BackStack.HOME
            )
        }
    }


    private fun setStateForStepView() {
        binding.stepView.state
            .selectedTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorAccent
                )
            )
            .animationType(StepView.ANIMATION_CIRCLE)
            .selectedCircleColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorAccent
                )
            )
            .selectedStepNumberColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
            .steps(object : ArrayList<String?>() {
                init {
                    add(getString(R.string.account))
                    add(getString(R.string.personal))
                    add(getString(R.string.address))
                    add(getString(R.string.password1))
                }
            })
            .stepsNumber(4)
            .animationDuration(resources.getInteger(android.R.integer.config_shortAnimTime))
            .commit()


    }

    override fun navigate(intent: Intent) {
        openActivity(intent)
        finish()
    }


}
