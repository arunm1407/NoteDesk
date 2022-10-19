package com.example.version2.presentation.login.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.version2.R
import com.example.version2.presentation.login.LoginFragment
import com.example.version2.presentation.login.listener.Navigation
import com.example.version2.presentation.util.BackStack
import com.example.version2.presentation.util.inTransaction
import com.example.version2.presentation.util.openActivity

class LoginActivity : AppCompatActivity(), Navigation {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction(BackStack.LOGIN)
            {
                replace(R.id.fragmentContainerView, LoginFragment())
            }

        }
    }


    override fun navigate(intent: Intent) {
        openActivity(intent)
        finish()
    }
}