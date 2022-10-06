package com.example.notedesk.presentation.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notedesk.databinding.FragmentOnboarding1Binding


class OnboardingFragment : Fragment() {


    companion object {

        private const val TITLE = "title"
        private const val DESCRIPTION = "description"
        private const val IMAGE = "image"
        fun newInstance(
            title: String?,
            description: String?,
            imageResource: Int
        ): OnboardingFragment =
            OnboardingFragment().apply {
                val args = Bundle()
                args.putString(TITLE, title)
                args.putString(DESCRIPTION, description)
                args.putInt(IMAGE, imageResource)
                arguments = args
            }


    }


    private var title: String? = null
    private var description: String? = null
    private var imageResource = 0
    private lateinit var binding: FragmentOnboarding1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(TITLE)
            description = requireArguments().getString(DESCRIPTION)
            imageResource = requireArguments().getInt(IMAGE)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOnboarding1Binding.inflate(inflater, container, false)
        binding.textOnboardingTitle.text = title
        binding.textOnboardingDescription.text = description
        binding.imageOnboarding.setAnimation(imageResource)

        return binding.root
    }


}
