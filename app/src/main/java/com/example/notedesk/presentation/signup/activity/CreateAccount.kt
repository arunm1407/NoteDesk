package com.example.notedesk.presentation.signup.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.notedesk.presentation.signup.AccountDetailsFragment
import com.example.notedesk.presentation.signup.listener.Navigate
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.R
import com.example.notedesk.databinding.ActivityCreateAccountBinding
import com.shuhart.stepview.StepView


class CreateAccount : AppCompatActivity(), Navigate {


    private lateinit var binding: ActivityCreateAccountBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragmentContainerView,
                    AccountDetailsFragment()
                ).commit()
            }
        }




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

    override fun navigate(fragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            replace(R.id.fragmentContainerView, fragment).addToBackStack(
                BackStack.HOME
            )
            commit()
        }
    }


}
