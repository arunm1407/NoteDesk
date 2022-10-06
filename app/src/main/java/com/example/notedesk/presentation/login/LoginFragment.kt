package com.example.notedesk.presentation.login

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentLoginBinding
import com.example.notedesk.domain.usecase.ValidateNewEmail
import com.example.notedesk.domain.usecase.ValidationResult
import com.example.notedesk.domain.usecase.CheckUserAuthentication
import com.example.notedesk.presentation.activity.MainActivity
import com.example.notedesk.presentation.login.listener.Navigation
import com.example.notedesk.util.keys.Keys.IS_LOGIN
import com.example.notedesk.util.keys.Keys.USER_ID
import com.example.notedesk.util.sharedPreference.SharedPreference
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.util.*


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
        textWatcherInitialzation()
        setUpFocusChangeListeners()


        binding.toSignup.setOnClickListener {
            clearData()
            navigationLisenter?.navigate()
        }

        binding.btnLogin.setOnClickListener {


            lifecycleScope.launch()
            {
                if (validateData()) {
                    startActivity(
                        Intent(requireContext(), MainActivity::class.java).apply {
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }


                    )
                    requireActivity().finish()

                }


            }


        }

    }

    private fun clearData() {
        binding.tiEtEmail.text?.clear()
        binding.tiEtPassword.text?.clear()
        binding.tvLoginName.error = null
        binding.tvLoginPassword.error = null
    }


    private fun setUpFocusChangeListeners() {

        binding.tiEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tvLoginName.error = null
            setIconColor(
                binding.tvLoginName,
                hasFocus
            )
        }
        binding.tiEtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tvLoginPassword.error = null
            setIconColor(
                binding.tvLoginPassword,
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

    private suspend fun validateData(): Boolean {
        val email = binding.tiEtEmail.text.toString().lowercase(Locale.ROOT)
        val password = binding.tiEtPassword.text.toString()
        var pass: ValidationResult
        var res: ValidationResult


        withContext(Dispatchers.IO)
        {
            res = ValidateNewEmail.execute(email) {
                validateEmail(it)
            }
            pass = CheckUserAuthentication.execute(email, password) { email, password ->
                viewModel.checkUserAuthentication(email, password)
            }

        }


        if (!res.successful) {
            binding.tvLoginName.error = res.errorMessage
            return false
        }

        if (!pass.successful) {
            binding.tvLoginPassword.error = pass.errorMessage
            return false
        }
        withContext(Dispatchers.Main) {
            SharedPreference(requireActivity()).putBooleanSharedPreference(IS_LOGIN, true)
            SharedPreference(requireActivity()).putIntSharePreferenceInt(
                USER_ID,
                viewModel.getUserIDFromEmail(email)
            )

        }

        return true
    }


    private suspend fun validateEmail(email: String): Boolean {

        return viewModel.isExistingEmail(email)


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


            binding.btnLogin.isEnabled = (binding.tiEtEmail.text.toString().trim()
                .isNotEmpty() && binding.tiEtPassword.text.toString().trim().isNotEmpty())


        }
    }


}