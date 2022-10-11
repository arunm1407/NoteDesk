package com.example.notedesk.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.notedesk.R
import com.example.notedesk.databinding.ActivityMainBinding
import com.example.notedesk.util.keys.Keys.PACKAGE
import com.example.notedesk.presentation.home.listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.home.listener.SettingsLisenter
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.presentation.home.HomeFragment
import com.example.notedesk.presentation.util.checkNull
import com.example.notedesk.presentation.util.inTransaction
import com.example.notedesk.presentation.util.startActivity
import com.example.notedesk.util.keys.Keys.USER_ID
import com.example.notedesk.util.sharedPreference.SharedPreference


class MainActivity : AppCompatActivity(), FragmentNavigationLisenter, SettingsLisenter {

    private lateinit var binding: ActivityMainBinding
    private var userId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = SharedPreference(this).getSharedPreferenceInt(USER_ID)
        if (savedInstanceState.checkNull()) {
            supportFragmentManager.inTransaction(BackStack.HOME)
            {
                replace(R.id.fragmentContainerView, HomeFragment())
            }

        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }


    override fun navigate(fragment: Fragment, name: String) {
        var name1: String? = null
        when (name) {
            BackStack.HOME -> {
                name1 = BackStack.HOME

                supportFragmentManager.popBackStack(
                    BackStack.HOME, 0
                )
            }
            BackStack.SEARCH -> name1 = BackStack.SEARCH
            BackStack.PREVIEW -> name1 = BackStack.PREVIEW
            BackStack.ATTACHMENT_PREVIEW -> name1 = BackStack.ATTACHMENT_PREVIEW
            BackStack.POLICY -> name1 = BackStack.POLICY
            BackStack.CREATE -> name1 = BackStack.CREATE
            BackStack.EDIT -> name1 = BackStack.EDIT
            BackStack.PROFILE -> {
                name1 = BackStack.PROFILE
                supportFragmentManager.popBackStack(
                    BackStack.PROFILE, 1
                )
            }
            BackStack.PROFILE_EDIT -> name1 = BackStack.PROFILE_EDIT


        }
        supportFragmentManager.inTransaction(name1)
        {
            replace(R.id.fragmentContainerView, fragment)
        }


    }

    override fun <T> navigateActivity(it: Class<T>) {
        startActivity(it)
        finishAffinity()
    }


    fun getUserID(): Int {
        return userId
    }

    private fun openAppSettings() {


        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts(PACKAGE, packageName, null)
        })
    }


    override fun settings() {
        openAppSettings()
    }


}