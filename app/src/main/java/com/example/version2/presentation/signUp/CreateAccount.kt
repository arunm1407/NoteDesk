package com.example.version2.presentation.signUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.version2.R
import com.example.version2.databinding.ActivityCreateAccountBinding
import com.example.version2.presentation.login.listener.Navigation
import com.example.version2.presentation.signUp.listener.Navigate
import com.example.version2.presentation.util.BackStack
import com.example.version2.presentation.util.inTransaction
import com.example.version2.presentation.util.openActivity
import com.shuhart.stepview.StepView

class CreateAccount : AppCompatActivity(), Navigate, Navigation {

    private lateinit var binding: ActivityCreateAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            setUpFragmentToContainer()
        }
        setStateForStepView()
        navigationListener()

    }

    private fun setUpFragmentToContainer() {
        supportFragmentManager.inTransaction(BackStack.ACCOUNT_INFO) {
            replace(R.id.fragmentContainerView, AccountInfoFragment())
        }
    }


    private fun navigationListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun navigate(fragment: Fragment) {

        supportFragmentManager.inTransaction(BackStack.HOME) {
            replace(R.id.fragmentContainerView, fragment)
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