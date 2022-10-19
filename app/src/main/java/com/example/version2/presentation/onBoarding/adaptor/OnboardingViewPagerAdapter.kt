package com.example.version2.presentation.onBoarding.adaptor

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.version2.R
import com.example.version2.presentation.onBoarding.BoardingFragment
import com.example.version2.presentation.onBoarding.MainFragment


class OnboardingViewPagerAdapter(
    fragmentActivity: MainFragment
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BoardingFragment.newInstance(
                "Easy to Add & Use Notes",
                "Take Notes and access them AnyTime,NoteDesk provide safe and privacy for Data ...",
                R.raw.first
            )
            1 -> BoardingFragment.newInstance(
                "Not Just Texts",
                "Add Images ,   WebLinks,     Voice-To-Text  , Search Notes , Sort Notes , prioritize Notes and   Choose Color  to your Notes",
                R.raw.second
            )
            else -> BoardingFragment.newInstance(
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