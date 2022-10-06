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
import com.example.notedesk.util.keys.Keys.USER_ID
import com.example.notedesk.util.sharedPreference.SharedPreference


class MainActivity : AppCompatActivity(), FragmentNavigationLisenter, SettingsLisenter {

    private lateinit var binding: ActivityMainBinding
    private var userId:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId=SharedPreference(this).getSharedPreferenceInt(USER_ID)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, HomeFragment()).addToBackStack(BackStack.HOME)
                    .commit()
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
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            when (name) {


                BackStack.SEARCH -> {
                    replace(
                        R.id.fragmentContainerView,
                        fragment
                    ).addToBackStack(BackStack.SEARCH)
                        .commit()
                }

                BackStack.PREVIEW -> {

                    replace(
                        R.id.fragmentContainerView,
                        fragment
                    ).addToBackStack(BackStack.PREVIEW)
                        .commit()
                }


                BackStack.HOME -> {
                    supportFragmentManager.popBackStack(
                        BackStack.HOME, 0
                    )
                }
                BackStack.ATTACHMENT_PREVIEW -> {

                    replace(
                        R.id.fragmentContainerView,
                        fragment
                    ).addToBackStack(BackStack.ATTACHMENT_PREVIEW).commit()
                }
                BackStack.POLICY -> {
                    replace(R.id.fragmentContainerView, fragment).addToBackStack(BackStack.POLICY)
                        .commit()
                }
                BackStack.CREATE -> {
                    replace(R.id.fragmentContainerView, fragment).addToBackStack(BackStack.CREATE)
                        .commit()

                }
                BackStack.EDIT -> {

                    replace(R.id.fragmentContainerView, fragment).addToBackStack(BackStack.EDIT)
                        .commit()

                }
                BackStack.PROFILE->{
                    replace(R.id.fragmentContainerView, fragment).addToBackStack(BackStack.PROFILE)
                        .commit()
                }

            }
        }


    }


    fun getUserID():Int
    {
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