package com.example.notedesk.presentation.signup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.notedesk.domain.usecase.ValidatePassword
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentPasswordBinding
import com.example.notedesk.presentation.signup.listener.SuccessListener
import com.example.notedesk.presentation.util.hideKeyboard
import com.shuhart.stepview.StepView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasswordFragment : Fragment(), SuccessListener {


    private lateinit var binding: FragmentPasswordBinding
    private var color: Int = R.color.weak
    private val viewModel: SignUpViewModel by lazy { ViewModelProvider(requireActivity())[SignUpViewModel::class.java] }
    private lateinit var passwordStrengthCalculator: PasswordStrengthCalculator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPasswordBinding.inflate(inflater)
        backPressed()
        eventHandler()
        return binding.root
    }

    private fun eventHandler() {
        textListener()
    }

    private fun textListener() {
        passwordStrengthCalculator = PasswordStrengthCalculator()
        binding.tilEtPassword.addTextChangedListener(passwordStrengthCalculator)

        passwordStrengthCalculator.strengthLevel.observe(viewLifecycleOwner)
        {
            displayStrengthLevel(it)
        }


        passwordStrengthCalculator.strengthColor.observe(viewLifecycleOwner)
        {
            color = it
        }
        passwordStrengthCalculator.lowerCase.observe(viewLifecycleOwner)
        {
            displayPasswordSuggestions(it, binding.lowerCaseImg, binding.lowerCaseTxt)
        }

        passwordStrengthCalculator.upperCase.observe(viewLifecycleOwner)
        {
            displayPasswordSuggestions(it, binding.upperCaseImg, binding.upperCaseTxt)
        }


        passwordStrengthCalculator.digit.observe(viewLifecycleOwner)
        {
            displayPasswordSuggestions(it, binding.digitImg, binding.digitTxt)
        }
        passwordStrengthCalculator.digit.observe(viewLifecycleOwner)
        {
            displayPasswordSuggestions(it, binding.specialCharImg, binding.specialCharTxt)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textWatcherInitialzation()
        requireActivity().findViewById<StepView>(R.id.stepView).go(3, true)
        binding.btnSignup.setOnClickListener {
            requireActivity().findViewById<StepView>(R.id.stepView).done(true)

            if (validateDate()) {
                showDialogFragment(SuccessDailog())
            }


        }

        binding.tilEtCPassword.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    v.clearFocus()
                    v.hideKeyboard()
                    true
                }
                else -> false
            }
        }
    }


    private fun validateDate(): Boolean {
        val password = binding.tilEtPassword.text?.trim().toString()
        val confirmPassword = binding.tilEtCPassword.text?.trim().toString()
        val res = ValidatePassword.execute(password, confirmPassword)
        if (!res.successful) {
            binding.tilCPassword.error = res.errorMessage
            return false
        }
        updateData(password)
        lifecycleScope.launch(Dispatchers.IO)
        {
            viewModel.createAccount(viewModel.userData)
        }
        return true
    }

    private fun updateData(password: String) {

        viewModel.userData.apply {

            this.password = password

        }
    }


    private fun textWatcherInitialzation() {
        binding.tilEtPassword.addTextChangedListener(textWatcher)
        binding.tilEtCPassword.addTextChangedListener(textWatcher)

    }


    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            if (checkPasswordMatch()) {
                binding.btnSignup.isEnabled = true

            } else {
                requireActivity().findViewById<StepView>(R.id.stepView).go(3, true)
                binding.btnSignup.isEnabled = false


            }


        }

    }


    private fun checkPasswordMatch(): Boolean {
        return (binding.tilEtPassword.text.toString().trim().isNotEmpty() &&
                binding.tilEtCPassword.text.toString().trim()
                    .isNotEmpty() && (binding.tilEtPassword.text.toString() == binding.tilEtCPassword.text.toString()) && checkPasswordStrength())

    }

    private fun checkPasswordStrength(): Boolean {
        return passwordStrengthCalculator.strengthLevel.value != StrengthLevel.WEAK


    }

    private fun displayPasswordSuggestions(value: Int, imageView: ImageView, textView: TextView) {
        if (value == 1) {
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.bulletproof))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.bulletproof))
        } else {
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.darkGray))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGray))
        }
    }

    private fun displayStrengthLevel(strengthLevel: StrengthLevel) {

        binding.strengthLevelTxt.text = strengthLevel.name
        binding.strengthLevelTxt.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.strengthLevelIndicator.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                color
            )
        )
    }

    private fun backPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    parentFragmentManager.popBackStack()

                }
            })
    }

    override fun done() {
        requireActivity().finish()
    }


    private fun showDialogFragment(dialog: DialogFragment) {
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog.activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show(childFragmentManager, "2")


    }


}