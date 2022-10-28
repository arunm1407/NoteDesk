package com.example.version2.presentation.login

import android.content.Context
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
import com.example.version2.domain.usecase.ValidationResult
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.sharedPreference.SharedPreference
import com.example.version2.databinding.FragmentLoginBinding
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.login.listener.Navigation
import com.example.version2.presentation.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {


    private var navigationLisenter: Navigation? = null
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(
            this,
            (requireActivity().applicationContext as NotesApplication).loginFactory
        )[LoginViewModel::class.java]
    }

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
        setUpListeners()
        textWatcherInitialzation()
    }

    private fun setUpListeners() {
        signUpListener()
        loginListener()
        setUpFocusChangeListeners()
        onBackPressListener()
        removeError()
        binding.tiEtPassword.actionDone()

    }

    private fun signUpListener() {
        binding.toSignup.setOnClickListener {
            clearData()
            navigationLisenter?.navigateToSignUp()
        }
    }

    private fun loginListener() {
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch()
            {
                if (validateData()) {


                    if (viewModel.checkUserIsOnBoarded(viewModel.userId)) {
                        navigationLisenter?.navigateToNoteScreen()

                    } else {
                        navigationLisenter?.navigateToBoardingScreen()
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
            res = viewModel.validateEmail(email)
            pass = viewModel.checkUserAuth(email, password)

        }


        when (res) {
            is ValidationResult.Error -> {
                binding.tvLoginName.setErrorMessage((res as ValidationResult.Error).message)
                return false
            }
            ValidationResult.Successful -> {

            }
        }


        when (pass) {
            is ValidationResult.Error -> {
                binding.tvLoginPassword.setErrorMessage((pass as ValidationResult.Error).message)
                return false
            }
            ValidationResult.Successful -> {

            }
        }


        withContext(Dispatchers.Main) {


            viewModel.setUserId(viewModel.getUserIDFromEmail(email))
            SharedPreference(requireActivity()).putIntSharePreferenceInt(
                Keys.USER_ID,
                viewModel.userId
            )

        }

        return true
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

    override fun onDetach() {
        super.onDetach()
        navigationLisenter = null
    }

    private fun onBackPressListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                   navigationLisenter?.exitTheScreen()

                }
            })
    }


}