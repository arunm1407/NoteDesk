package com.example.version2.presentation.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.version2.databinding.FragmentBoardingBinding
import com.example.version2.presentation.util.withArgs


class BoardingFragment : Fragment() {

    companion object {

        private const val TITLE = "title"
        private const val DESCRIPTION = "description"
        private const val IMAGE = "image"
        fun newInstance(
            title: String?,
            description: String?,
            imageResource: Int
        ): BoardingFragment = BoardingFragment().withArgs {
            putString(TITLE, title)
            putString(DESCRIPTION, description)
            putInt(IMAGE, imageResource)

        }


    }


    private lateinit var binding: FragmentBoardingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments.apply {
            binding.textOnboardingTitle.text = requireArguments().getString(TITLE)
            binding.textOnboardingDescription.text = requireArguments().getString(DESCRIPTION)
            binding.imageOnboarding.setAnimation(requireArguments().getInt(IMAGE))


        }
    }


}