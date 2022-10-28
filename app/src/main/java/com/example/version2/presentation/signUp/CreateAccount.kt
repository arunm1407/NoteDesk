package com.example.version2.presentation.signUp


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.version2.R
import com.example.version2.databinding.ActivityCreateAccountBinding
import com.example.version2.presentation.login.activity.LoginActivity
import com.example.version2.presentation.signUp.listener.Navigate
import com.example.version2.presentation.util.BackStack
import com.example.version2.presentation.util.inTransaction
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.openActivity
import com.shuhart.stepview.StepView

class CreateAccount : AppCompatActivity(), Navigate {

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

    private fun navigate(fragment: Fragment) {

        supportFragmentManager.inTransaction(BackStack.HOME) {
            replace(R.id.fragmentContainerView, fragment)
        }
    }


    private fun startNavigation(name: String) {
        when (name) {

            BackStack.PASSWORD_PAGE -> navigate(PasswordFragment())
            BackStack.ADDRESS_PAGE -> navigate(AddressFragment())
            BackStack.PERSONAL_INFO -> navigate(PersonalInfoFragment())
            else -> navigate(AccountInfoFragment())

        }


    }

    override fun navigateToPersonalPage() {
        startNavigation(BackStack.PERSONAL_INFO)
    }

    override fun navigateToAddressPage() {
        startNavigation(BackStack.ADDRESS_PAGE)
    }

    override fun navigateToPasswordPage() {
        startNavigation(BackStack.PASSWORD_PAGE)
    }


    override fun navigateToLoginScreen() {
        openActivity(
            Intent(
                this,
                LoginActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    override fun navigateToPreviousScreen() {
        supportFragmentManager.popBackStack()
    }

    override fun navigateToSettingScreen() {
        openAppSettings()
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


    private fun openAppSettings() {


        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts(Keys.PACKAGE, packageName, null)
        })
    }

}