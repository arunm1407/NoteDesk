package com.example.version2.presentation.signUp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.example.version2.R
import com.example.version2.databinding.FragmentAddressBinding
import com.example.version2.domain.model.User
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.signUp.listener.Navigate
import com.example.version2.presentation.util.clearError
import com.example.version2.presentation.util.getPinCode
import com.example.version2.presentation.util.getString
import com.example.version2.presentation.util.setErrorMessage
import com.shuhart.stepview.StepView


class AddressFragment : Fragment() {


    private lateinit var binding: FragmentAddressBinding
    private var navigationLisenter: Navigate? = null
    private val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            (requireActivity().applicationContext as NotesApplication).signUpFactory
        )[SignUpViewModel::class.java]
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Navigate)
            navigationLisenter = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddressBinding.inflate(inflater)
        backPressedListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFocusChangeListeners()
        eventHandler()
        removeError()

    }

    private fun eventHandler() {
        stepViewAction()
        nextListener()

    }

    private fun nextListener() {
        binding.btnNext.setOnClickListener {
            if (validateData()) {
                removeError()

                navigationLisenter?.navigate(PasswordFragment())
            }


        }
    }

    private fun removeError() {
        binding.tilAddressLine1.clearError()
        binding.tilAddressLine2.clearError()
        binding.tilCity.clearError()
        binding.tilPinCode.clearError()
    }

    private fun stepViewAction() {
        requireActivity().findViewById<StepView>(R.id.stepView).go(2, true)
    }

    private fun validateData(): Boolean {
        val pinCode = binding.tilEtPinCode.getString()
        if (pinCode.isNotEmpty()) {
            val res = viewModel.validatePinCode(pinCode)
            if (!res.successful) {
                binding.tilPinCode.setErrorMessage(res.errorMessage)
                binding.tilEtPinCode.clearFocus()
                return false
            }
        }


        val user = User(
            viewModel.userData.firstName,
            viewModel.userData.lastName,
            viewModel.userData.email,
            viewModel.userData.bio,
            viewModel.userData.dob,
            viewModel.userData.gender,
            viewModel.userData.mobileNumber,
            viewModel.userData.image,
            binding.tilEtaddress1.getString(),
            binding.tilEtaddress2.getString(),
            binding.tilEtcity.getString(),
            binding.tilEtPinCode.getPinCode(),
            "",
            false
        )
        viewModel.userData = user
        return true

    }


    private fun setUpFocusChangeListeners() {

        binding.tilEtaddress1.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilAddressLine1.clearError()

        }
        binding.tilEtaddress2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilAddressLine2.clearError()
        }
        binding.tilEtcity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilCity.clearError()

        }

        binding.tilEtPinCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPinCode.clearError()
        }


    }


    private fun backPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()

                }
            })
    }


}