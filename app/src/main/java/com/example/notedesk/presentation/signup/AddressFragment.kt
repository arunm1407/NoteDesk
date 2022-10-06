package com.example.notedesk.presentation.signup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.example.notedesk.domain.usecase.ValidatePinCode
import com.example.notedesk.presentation.signup.listener.Navigate
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentAddressBinding
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
        eventHandler()
    }

    private fun eventHandler() {
        stepViewAction()
        nextListener()

    }

    private fun nextListener() {
        binding.btnNext.setOnClickListener {
            if (validateData())
                navigationLisenter?.navigate(PasswordFragment())

        }
    }

    private fun stepViewAction() {
        requireActivity().findViewById<StepView>(R.id.stepView).go(2, true)
    }

    private fun validateData(): Boolean {
        val pinCode = binding.tilEtPinCode.text.toString()
        val addressLine1 = binding.tilEtaddress1.text.toString()
        val addressLine2 = binding.tilEtaddress2.text.toString()
        val city = binding.tilEtcity.text.toString()
        if (pinCode.isNotEmpty()) {
            val res = ValidatePinCode.execute(pinCode)

            if (!res.successful) {
                binding.tilPinCode.error = res.errorMessage
                return false
            }
        }
        updateData(addressLine1, addressLine2, city, pinCode)
        return true

    }

    private fun updateData(
        addressLine1: String,
        addressLine2: String,
        city: String,
        pinCode: String
    ) {

        viewModel.userData.apply {
            when {
                pinCode.isNotEmpty() -> this.pinCode = pinCode.toInt()
                addressLine1.isNotEmpty() -> this.addressLine1 = addressLine1
                addressLine2.isNotEmpty() -> this.addressLine2 = addressLine2
                city.isNotEmpty() -> this.city = city
            }


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