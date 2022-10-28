package com.example.version2.presentation.signUp

import android.content.Context
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
import com.example.version2.R
import com.example.version2.databinding.FragmentPasswordBinding
import com.example.version2.domain.model.User
import com.example.version2.domain.usecase.ValidationResult
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.signUp.dialog.ConfirmationDialog
import com.example.version2.presentation.signUp.listener.ConfirmationListener
import com.example.version2.presentation.signUp.listener.Navigate
import com.example.version2.presentation.signUp.util.PasswordStrengthCalculator
import com.example.version2.presentation.signUp.util.StrengthLevel
import com.example.version2.presentation.util.*
import com.shuhart.stepview.StepView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PasswordFragment : Fragment(), ConfirmationListener {


    private lateinit var binding: FragmentPasswordBinding
    private var color: Int = R.color.weak
    private val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            (requireActivity().applicationContext as NotesApplication).signUpFactory
        )[SignUpViewModel::class.java]
    }
    private lateinit var passwordStrengthCalculator: PasswordStrengthCalculator
    private lateinit var navigationListener: Navigate


    override fun onAttach(context: Context) {
        super.onAttach(context)

        val parent = parentFragment ?: context
        if (parent is Navigate)
            navigationListener = parent
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordBinding.inflate(inflater)
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
        passwordStrengthCalculator.specialChar.observe(viewLifecycleOwner)
        {
            displayPasswordSuggestions(it, binding.specialCharImg, binding.specialCharTxt)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textWatcherInitialzation()
        initializeStepView()
        setUpListener()
        eventHandler()
    }

    private fun setUpPasswordListener() {
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


    private fun setUpRemoveError() {
        binding.tilEtPassword.clearErrorOnClick(binding.tilPassword)
        binding.tilEtCPassword.clearErrorOnClick(binding.tilCPassword)
    }

    private fun setupSignUpListener() {
        binding.btnSignup.setOnClickListener {


            if (checkPasswordMatch()) {

                lifecycleScope.launch(Dispatchers.Main)
                {
                    if (validateDate()) {
                        requireActivity().findViewById<StepView>(R.id.stepView).done(true)
                        showDialogFragment(ConfirmationDialog())
                    } else {
                        binding.tilCPassword.setErrorMessage(getString(R.string.password_does_not_match1))
                    }
                }


            } else {
                if (binding.tilEtPassword.getString().isBlank()) {
                    binding.tilPassword.setErrorMessage(getString(R.string.password_empty))
                } else if (binding.tilEtCPassword.getString().isBlank()) {
                    binding.tilCPassword.setErrorMessage(getString(R.string.confirm_password_empty))
                }

            }


        }
    }

    private fun initializeStepView() {
        requireActivity().findViewById<StepView>(R.id.stepView).go(3, true)
    }

    private fun setUpListener() {
        setUpFocusChangeListeners()
        backPressedListener()
        setupSignUpListener()
        setUpPasswordListener()
        setUpRemoveError()
    }


    private suspend fun validateDate(): Boolean {
        val password = binding.tilEtPassword.getString()
        val confirmPassword = binding.tilEtCPassword.getString()
        when (val res = viewModel.validatePassword(password, confirmPassword)) {
            is ValidationResult.Error -> {
                binding.tilCPassword.error = res.message
                return false
            }
            ValidationResult.Successful -> {

            }
        }

        updateData(password)
        withContext(Dispatchers.IO)
        {
            viewModel.userID = viewModel.createAccount(viewModel.userData).toInt()
        }
        return true
    }

    private fun updateData(password: String) {

        val user: User
        viewModel.userData.apply {
            user = User(
                firstName,
                lastName,
                email,
                bio,
                dob,
                gender,
                mobileNumber,
                image,
                addressLine1,
                addressLine2,
                city,
                pinCode,
                password,
                isOnBoarded
            )
        }
        viewModel.userData = user

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


            if (checkPasswordStrength() && checkBothPasswordNotEmpty()) {
                binding.btnSignup.isEnabled = true

            } else {

                binding.btnSignup.isEnabled = true


            }


        }

    }


    private fun checkBothPasswordNotEmpty(): Boolean {
        return (binding.tilEtCPassword.checkNotEmpty() && binding.tilEtPassword.checkNotEmpty())
    }


    private fun checkPasswordMatch(): Boolean {
        return (binding.tilEtPassword.checkNotEmpty() &&
                binding.tilEtCPassword.checkNotEmpty())

    }


    private fun checkPasswordStrength(): Boolean {
        return passwordStrengthCalculator.strengthLevel.value != StrengthLevel.WEAK

    }

    private fun displayPasswordSuggestions(value: Int, imageView: ImageView, textView: TextView) {
        if (value == 1) {
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
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

    private fun backPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigationListener.navigateToPreviousScreen()

                }
            })
    }

    override fun done() {

        lifecycleScope.launch()
        {
            withContext(Dispatchers.Main)
            {
                navigationListener.navigateToLoginScreen()
            }


        }
    }


    private fun showDialogFragment(dialog: DialogFragment) {
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog.activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show(childFragmentManager, "2")
        dialog.isCancelable = false


    }


    private fun setUpFocusChangeListeners() {
        binding.tilEtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword.clearError()
        }
        binding.tilEtCPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilCPassword.clearError()

        }

    }


}