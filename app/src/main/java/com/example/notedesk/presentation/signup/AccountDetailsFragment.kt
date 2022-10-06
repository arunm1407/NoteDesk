package com.example.notedesk.presentation.signup

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.notedesk.domain.usecase.ValidateEmail
import com.example.notedesk.presentation.signup.listener.Navigate
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentAccountDetailsBinding
import com.google.android.material.textfield.TextInputLayout
import com.shuhart.stepview.StepView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class AccountDetailsFragment : Fragment() {


    private lateinit var binding: FragmentAccountDetailsBinding
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

        binding = FragmentAccountDetailsBinding.inflate(inflater)
        backPressed()
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textWatcherInitialzation()
        setUpFocusChangeListeners()
        eventHandler()
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
                        navigationLisenter?.navigate(PersonalDetailFragment())
                    }
                }

            }


        }
    }


    private fun stepViewAction() {
        requireActivity().findViewById<StepView>(R.id.stepView).go(0, true)
    }


    private suspend fun validateData(): Boolean {
        val email = binding.tilEtEmail.text.toString().lowercase(Locale.ROOT)
        val firstName = binding.tilEtFirstName.text.toString()
        val lastName = binding.tilEtLastName.text.toString()
        var flag = false
        val job = lifecycleScope.launch()
        {
            withContext(Dispatchers.IO)
            {
                val res = ValidateEmail.execute(email, viewModel)
                if (res.successful) {
                    flag = true
                    updateData(email, firstName, lastName)
                } else {
                    withContext(Dispatchers.Main) {
                        binding.tilEmail.error = res.errorMessage
                    }

                }

            }
        }
        job.join()
        return flag
    }

    private fun updateData(email: String, firstName: String, lastName: String) {
        viewModel.userData.apply {
            this.email = email.lowercase(Locale.ROOT)
            this.lastName = lastName
            this.firstName = firstName

        }
    }


    private fun textWatcherInitialzation() {
        binding.tilEtFirstName.addTextChangedListener(textWatcher)
        binding.tilEtLastName.addTextChangedListener(textWatcher)
        binding.tilEtEmail.addTextChangedListener(textWatcher)
    }


    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            binding.btnNext.isEnabled =
                (binding.tilEtFirstName.text.toString().trim().isNotEmpty() &&
                        binding.tilEtLastName.text.toString().trim().isNotEmpty() &&
                        binding.tilEtEmail.text.toString().trim().isNotEmpty()
                        )


        }
    }


    private fun setUpFocusChangeListeners() {

        binding.tilEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.error = null
            setIconColor(
                binding.tilEmail,
                hasFocus
            )
        }
        binding.tilEtLastName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLastName.error = null
            setIconColor(
                binding.tilLastName,
                hasFocus
            )
        }
        binding.tilEtFirstName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilFirstName.error = null
            setIconColor(
                binding.tilFirstName,
                hasFocus
            )
        }


    }

    private fun setIconColor(textInputLayout: TextInputLayout, hasFocus: Boolean) {
        val colorFocussed = ResourcesCompat.getColor(resources, R.color.color_primary, null)
        val colorNonFocussed = ResourcesCompat.getColor(resources, R.color.unselected, null)
        val color = if (hasFocus) colorFocussed else colorNonFocussed
        textInputLayout.setStartIconTintList(ColorStateList.valueOf(color))
    }

    override fun onDetach() {
        super.onDetach()
        navigationLisenter = null
    }

    private fun backPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

}