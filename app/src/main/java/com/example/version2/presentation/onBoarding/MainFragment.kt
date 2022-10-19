package com.example.version2.presentation.onBoarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.viewpager2.widget.ViewPager2
import com.example.version2.databinding.FragmentMainBinding
import com.example.version2.presentation.onBoarding.adaptor.OnboardingViewPagerAdapter
import com.example.version2.presentation.onBoarding.listener.Navigation
import com.google.android.material.tabs.TabLayoutMediator


class MainFragment : Fragment() {


    private lateinit var mViewPager: ViewPager2
    private lateinit var binding: FragmentMainBinding
    private var navigationLisenter: Navigation? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Navigation)
            navigationLisenter = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        mViewPager = binding.viewPager
        mViewPager.adapter = OnboardingViewPagerAdapter(this)
        TabLayoutMediator(binding.page, mViewPager) { _, _ -> }.attach()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressListener()
        eventHandler()

    }

    private fun eventHandler() {
        skipListener()
        nextListener()
    }

    private fun nextListener() {
        binding.btnNextStep.setOnClickListener {
            if (getItem() > mViewPager.childCount) {
                navigateFinish()
            } else {
                mViewPager.setCurrentItem(setItem(), true)
            }
        }
    }

    private fun skipListener() {
        binding.textSkip.setOnClickListener {
            navigateFinish()
        }

    }


    private fun getItem(): Int {
        return mViewPager.currentItem
    }


    private fun setItem(): Int {
        return mViewPager.currentItem + 1
    }


    private fun navigateFinish() {
        navigationLisenter?.navigate()
    }


    private fun onBackPressListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()

                }
            })
    }


    override fun onDetach() {
        super.onDetach()
        navigationLisenter = null
    }

}