package com.example.notedesk.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.notedesk.databinding.FragmentLoginBinding
import com.example.notedesk.domain.usecase.ValidateNewEmail
import com.example.notedesk.domain.usecase.ValidationResult
import com.example.notedesk.domain.usecase.CheckUserAuthentication
import com.example.notedesk.presentation.activity.MainActivity
import com.example.notedesk.presentation.login.listener.Navigation
import com.example.notedesk.presentation.onBoarding.activity.BoardingScreen
import com.example.notedesk.presentation.signup.activity.CreateAccount
import com.example.notedesk.presentation.util.*
import com.example.notedesk.util.keys.Keys.USER_ID
import com.example.notedesk.util.sharedPreference.SharedPreference
import kotlinx.coroutines.*


class LoginFragment : Fragment() {

    private var navigationLisenter: Navigation? = null
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by lazy { ViewModelProvider(requireActivity())[LoginViewModel::class.java] }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Navigation)
            navigationLisenter = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backPressed()
        textWatcherInitialzation()
        setUpFocusChangeListeners()
        removeError()

        binding.toSignup.setOnClickListener {
            clearData()
            navigationLisenter?.navigate(Intent(requireContext(), CreateAccount::class.java))
        }
        binding.tiEtPassword.actionDone()
        binding.btnLogin.setOnClickListener {


            lifecycleScope.launch()
            {
                if (validateData()) {


                    if (viewModel.checkUserIsOnBoarded(viewModel.userId)) {
                        navigationLisenter?.navigate(
                            Intent(
                                requireContext(),
                                MainActivity::class.java
                            )
                        )

                    } else {
                        navigationLisenter?.navigate(
                            Intent(
                                requireContext(),
                                BoardingScreen::class.java
                            )
                        )
                    }


                }


            }


        }

    }

    private fun removeError() {
        binding.tiEtEmail.setOnClickListener {
            binding.tvLoginName.clearError()
        }
        binding.tiEtPassword.setOnClickListener {
            binding.tvLoginPassword.clearError()
        }
    }

    private fun clearData() {
        binding.tiEtEmail.clearText()
        binding.tiEtPassword.clearText()
        binding.tvLoginName.clearError()
        binding.tvLoginPassword.clearError()
    }


    private fun setUpFocusChangeListeners() {

        binding.tiEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tvLoginName.clearError()


        }
        binding.tiEtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tvLoginPassword.clearError()
        }


    }


    private suspend fun validateData(): Boolean {
        val email = binding.tiEtEmail.getStringLower()
        val password = binding.tiEtPassword.getString()
        var pass: ValidationResult
        var res: ValidationResult


        withContext(Dispatchers.IO)
        {
            res = ValidateNewEmail.execute(email) {
                viewModel.isExistingEmail(email)
            }
            pass = CheckUserAuthentication.execute(email, password) { email, password ->
                viewModel.checkUserAuthentication(email, password)
            }

        }


        if (!res.successful) {
            binding.tvLoginName.setErrorMessage(res.errorMessage)
            return false
        }

        if (!pass.successful) {
            binding.tvLoginPassword.setErrorMessage(pass.errorMessage)
            return false
        }
        withContext(Dispatchers.Main) {


            viewModel.setUserId(viewModel.getUserIDFromEmail(email))
            SharedPreference(requireActivity()).putIntSharePreferenceInt(
                USER_ID,
                viewModel.userId
            )

        }

        return true
    }


    override fun onDetach() {
        super.onDetach()
        navigationLisenter = null
    }


    private fun textWatcherInitialzation() {
        binding.tiEtEmail.addTextChangedListener(textWatcher)
        binding.tiEtPassword.addTextChangedListener(textWatcher)
    }


    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {


        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            binding.btnLogin.isEnabled =
                (binding.tiEtEmail.checkNotEmpty() && binding.tiEtPassword.checkNotEmpty())


        }
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