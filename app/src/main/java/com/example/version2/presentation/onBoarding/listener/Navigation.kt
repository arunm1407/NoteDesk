package com.example.version2.presentation.onBoarding.listener

import android.content.Intent

interface Navigation {

   fun navigate()
   fun navigateScreen(intent: Intent)
}