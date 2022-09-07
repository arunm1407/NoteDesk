package com.example.notedesk.presentation.util

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.notedesk.presentation.createNote.CreateNotesFragment
import com.example.notedesk.presentation.home.Listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.home.Listener.SettingsLisenter
import com.example.notedesk.presentation.previewNote.PreviewFragment
import com.example.notesappfragment.R
import com.example.notesappfragment.databinding.ActivityMainBinding
import com.example.notesappfragment.features.presentation.home.HomeFragment


class MainActivity : AppCompatActivity(), FragmentNavigationLisenter, SettingsLisenter {

    private lateinit var binding: ActivityMainBinding
    private lateinit var perviousFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState==null)
        {
            navigate(HomeFragment(), BackStack.HOME)
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
            when (name) {
                BackStack.HOME -> {
                    perviousFragment = fragment
                    supportFragmentManager.popBackStack(
                        BackStack.HOME, FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                    replace(R.id.fragmentContainerView, fragment).addToBackStack(name).commit()
                }
                BackStack.ATTACHMENT_PREVIEW -> {
                    setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                    hide(perviousFragment)
                    add(R.id.fragmentContainerView, fragment).addToBackStack(name).commit()
                }
                BackStack.POLICY, BackStack.EDIT , BackStack.CREATE-> {
                    setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                    replace(R.id.fragmentContainerView, fragment).addToBackStack(name).commit()
                }
            }
        }


    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun settings() {
        openAppSettings()
    }


    override fun onBackPressed() {
        when (val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)) {
            is CreateNotesFragment -> {
                fragment.displayExitDailog()
            }
            is HomeFragment -> {
                finish()
            }
            is PreviewFragment -> {
                supportFragmentManager.popBackStack()
            }
            else -> {
                super.onBackPressed()
            }
        }


    }

    private fun getCallerFragment(): String? {
        val fm: FragmentManager = supportFragmentManager
        val count = fragmentManager.backStackEntryCount
        return fm.getBackStackEntryAt(count - 2).name
    }

}