package com.example.notedesk.presentation.login.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notedesk.presentation.login.LoginFragment
import com.example.notedesk.presentation.login.listener.Navigation
import com.example.notedesk.presentation.signup.activity.CreateAccount
import com.example.notedesk.R

class LoginActivity : AppCompatActivity(), Navigation {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, LoginFragment()).commit()
            }
        }
    }

    override fun navigate() {
        startActivity(
            Intent(this, CreateAccount::class.java)
        )
    }


}