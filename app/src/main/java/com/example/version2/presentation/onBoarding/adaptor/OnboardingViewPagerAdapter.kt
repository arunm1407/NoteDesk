package com.example.version2.presentation.onBoarding.adaptor

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.version2.R
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.onBoarding.BoardingFragment
import com.example.version2.presentation.onBoarding.MainFragment


class OnboardingViewPagerAdapter(
    fragmentActivity: MainFragment
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BoardingFragment.newInstance(
                NotesApplication.context?.getString(R.string.title1),
                NotesApplication.context?.getString(R.string.content1),
                R.raw.first
            )
            1 -> BoardingFragment.newInstance(
                NotesApplication.context?.getString(R.string.title2),
                NotesApplication.context?.getString(R.string.content2),
                R.raw.second
            )
            else -> BoardingFragment.newInstance(
                NotesApplication.context?.getString(R.string.title3),
                NotesApplication.context?.getString(R.string.content3),
                R.raw.third
            )
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}