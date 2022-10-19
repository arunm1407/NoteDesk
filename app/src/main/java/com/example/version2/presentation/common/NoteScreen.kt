package com.example.version2.presentation.common

import com.example.version2.presentation.homeScreen.HomeFragment
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.sharedPreference.SharedPreference
import com.example.version2.R
import com.example.version2.databinding.ActivityNoteScreenBinding
import com.example.version2.presentation.homeScreen.listener.FragmentNavigationLisenter
import com.example.version2.presentation.homeScreen.listener.SettingsLisenter
import com.example.version2.presentation.util.BackStack
import com.example.version2.presentation.util.inTransaction
import com.example.version2.presentation.util.startActivity

class NoteScreen : AppCompatActivity(), FragmentNavigationLisenter, SettingsLisenter {


    private lateinit var binding: ActivityNoteScreenBinding
    private var userId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = SharedPreference(this).getSharedPreferenceInt(Keys.USER_ID)
        if (savedInstanceState == null) {
            fragmentTransaction(BackStack.HOME, HomeFragment())
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
        fragmentTransaction(name1, fragment)
    }


    private fun fragmentTransaction(name: String?, fragment: Fragment) {
        supportFragmentManager.inTransaction(name)
        {
            replace(R.id.fragmentContainerView, fragment)
        }
    }

    fun getUserID(): Int {
        return userId
    }

    override fun <T> navigateActivity(it: Class<T>) {
        startActivity(it)
        finishAffinity()
    }

    override fun settings() {
        openAppSettings()
    }


    private fun openAppSettings() {


        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts(Keys.PACKAGE, packageName, null)
        })
    }
}