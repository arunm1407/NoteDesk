package com.example.notedesk.presentation.signup

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentAddressBinding
import com.example.notedesk.domain.usecase.ValidatePinCode
import com.example.notedesk.presentation.signup.listener.Navigate
import com.example.notedesk.presentation.util.clearError
import com.example.notedesk.presentation.util.getString
import com.example.notedesk.presentation.util.setErrorMessage
import com.shuhart.stepview.StepView


class AddressFragment : Fragment() {


    private lateinit var binding: FragmentAddressBinding
    private var navigationLisenter: Navigate? = null
    private val viewModel: SignUpViewModel by lazy { ViewModelProvider(requireActivity())[SignUpViewModel::class.java] }


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
        backPressed()
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
            val res = ValidatePinCode.execute(pinCode)
            if (!res.successful) {
                binding.tilPinCode.setErrorMessage(res.errorMessage)
                binding.tilEtPinCode.clearFocus()
                return false
            }
        }
        viewModel.userData.apply {

            this.city = binding.tilEtcity.getString()
            this.addressLine1 = binding.tilEtaddress1.getString()
            this.addressLine2 = binding.tilEtaddress2.getString()
            this.pinCode = binding.tilEtPinCode.getString()


        }
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


    private fun backPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()

                }
            })
    }

}