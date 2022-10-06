package com.example.notedesk.presentation.onBoarding.adaptor

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.notedesk.presentation.onBoarding.OnBoardingMainFragment
import com.example.notedesk.presentation.onBoarding.OnboardingFragment
import com.example.notedesk.R


class OnboardingViewPagerAdapter(
    fragmentActivity: OnBoardingMainFragment
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment.newInstance(
                "Easy to Add & Use Notes",
                "Take Notes and access them AnyTime,NoteDesk provide safe and privacy for Data ...",
                R.raw.first
            )
            1 -> OnboardingFragment.newInstance(
                "Not Just Texts",
                "Add Images ,   WebLinks,     Voice-To-Text  , Search Notes , Sort Notes , prioritize Notes and   Choose Color  to your Notes",
                R.raw.second
            )
            else -> OnboardingFragment.newInstance(
                "Stay Organised",
                "Keep Track your Progress with the NoteDesk and Be Organised for better Efficiency and Productivity in you work... ",
                R.raw.third
            )
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}