package com.example.version2.presentation.signUp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.version2.R
import com.example.version2.databinding.FragmentAccountInfoBinding
import com.example.version2.domain.model.Gender
import com.example.version2.domain.model.User
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.signUp.listener.Navigate
import com.example.version2.presentation.util.clearError
import com.example.version2.presentation.util.getString
import com.example.version2.presentation.util.getStringLower
import com.shuhart.stepview.StepView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class AccountInfoFragment : Fragment() {


    private lateinit var binding: FragmentAccountInfoBinding
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

        binding = FragmentAccountInfoBinding.inflate(inflater)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListener()
        eventHandler()

    }

    private fun setUpListener() {
        backPressedListener()
        setUpFocusChangeListeners()
        removeError()
    }

    private fun removeError() {
        binding.tilEtEmail.setOnClickListener {
            binding.tilEmail.clearError()
        }
        binding.tilEtFirstName.setOnClickListener {
            binding.tilFirstName.clearError()
        }
        binding.tilEtLastName.setOnClickListener {
            binding.tilLastName.clearError()
        }
    }


    private fun eventHandler() {
        stepViewAction()
        nextButtonListener()


    }

    private fun nextButtonListener() {
        binding.btnNext.setOnClickListener {
            lifecycleScope.launch {

                withContext(Dispatchers.Main)
                {
                    if (validateData()) {
                        removeError()
                        navigationLisenter?.navigate(PersonalInfoFragment())
                    }

                }

            }


        }
    }


    private fun stepViewAction() {
        requireActivity().findViewById<StepView>(R.id.stepView).go(0, true)
    }


    private suspend fun validateData(): Boolean {
        val email = binding.tilEtEmail.getStringLower()
        val firstName = binding.tilEtFirstName.getString()
        val lastName = binding.tilEtLastName.getString()
        var flag = false

        if (firstName.isEmpty()) {
            binding.tilFirstName.error = "Field is Empty"
        }
        if (lastName.isEmpty()) {
            binding.tilLastName.error = "Field is Empty"

        }

        withContext(Dispatchers.IO)
        {
            val res = viewModel.checkEmailExist(email)
            if (res.successful) {
                flag = true
                updateData(email, firstName, lastName)
            } else {
                withContext(Dispatchers.Main) {
                    binding.tilEmail.error = res.errorMessage
                }

            }

        }
        return flag
    }

    private fun updateData(email: String, firstName: String, lastName: String) {
        viewModel.userData = User(
            firstName,
            lastName,
            email.lowercase(Locale.ROOT),
            "",
            "",
            Gender.NOT_SPECIFIED,
            "",
            null,
            "",
            "",
            "",
            0,
            "",
            false
        )

    }


    private fun setUpFocusChangeListeners() {

        binding.tilEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.clearError()

        }
        binding.tilEtLastName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLastName.clearError()

        }
        binding.tilEtFirstName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilFirstName.clearError()
        }


    }


    override fun onDetach() {
        super.onDetach()
        navigationLisenter = null
    }

    private fun backPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

}