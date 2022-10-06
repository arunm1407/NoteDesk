package com.example.notedesk.presentation.login

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentSignUpBinding
import com.example.notedesk.presentation.signup.PasswordStrengthCalculator
import com.example.notedesk.presentation.signup.SignUpViewModel
import com.example.notedesk.presentation.signup.StrengthLevel
import com.example.notedesk.presentation.util.hideKeyboard
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val myCalendar = Calendar.getInstance()
    private lateinit var date: OnDateSetListener
    private var color: Int = R.color.weak

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater)
        val languages = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown, languages)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)


        val passwordStrengthCalculator = PasswordStrengthCalculator()
        binding.tilEtPassword.addTextChangedListener(passwordStrengthCalculator)

        // Observers
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


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFocusChangeListeners()
        date =
            OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }


        binding.btnSignup.setOnClickListener {
            clearFormFocuses()
            initiateSignup()

        }

        binding.tilEtDOB.setOnClickListener {
            Log.i("arun", "inside set listener")
            initializeDatePicker()
            binding.tilEtDOB.setOnFocusChangeListener()
            {  _, hasFocus ->

                setIconColor(binding.tilDOB,hasFocus)
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


        //observor


    }

    private fun initiateSignup() {
        val firstName = binding.tilEtFirstName.text
        val lastName = binding.tilEtLastName.text
        val dob = binding.tilEtDOB.text
        val email = binding.tilEtMobile.text
        val gender = binding.autoCompleteTextView.text
        val password = binding.tilEtPassword.text
        val cpassword = binding.tilEtCPassword.text
        val address1 = binding.tilEtaddress1.text
        val address2 = binding.tilEtaddress1.text


    }


    private fun setUpFocusChangeListeners() {




        binding.tilEtFirstName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLastName.error = null
            setIconColor(
                binding.tilFirstName,
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


        binding.tilEtDOB.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLastName.error = null
            setIconColor(
                binding.tilDOB,
                hasFocus
            )


        }
        binding.tilEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.error = null
            setIconColor(
                binding.tilEmail,
                hasFocus
            )
        }
        binding.tilEtMobile.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilMobile.error = null
            setIconColor(
                binding.tilMobile,
                hasFocus
            )
        }


        binding.tilEtaddress1.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLastName.error = null
            setIconColor(
                binding.tilAddressLine1,
                hasFocus
            )
        }


        binding.tilEtaddress2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLastName.error = null
            setIconColor(
                binding.tilAddressLine2,
                hasFocus
            )
        }


        binding.tilEtcity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLastName.error = null
            setIconColor(
                binding.tilCity,
                hasFocus
            )
        }

        binding.tilEtPinCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLastName.error = null
            setIconColor(
                binding.tilPinCode,
                hasFocus
            )
        }
        binding.tilEtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword.error = null
            setIconColor(
                binding.tilPassword,
                hasFocus
            )
        }
        binding.tilEtCPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilCPassword.error = null
            setIconColor(
                binding.tilCPassword,
                hasFocus
            )
        }


        binding.tilDOB.setOnFocusChangeListener()
        {
                _, b ->
            setIconColor(
                binding.tilDOB,
                b
            )

        }

        binding.tilGender.setOnFocusChangeListener { _, b ->

            setIconColor(
                binding.tilGender,
                b
            )

        }
    }


    private fun clearFormFocuses() {
        binding.tilEtEmail.clearFocus()
        binding.tilEtMobile.clearFocus()
        binding.tilEtLastName.clearFocus()
        binding.tilEtFirstName.clearFocus()
        binding.tilEtDOB.clearFocus()
        binding.tilEtaddress1.clearFocus()
        binding.tilEtaddress2.clearFocus()
        binding.tilEtcity.clearFocus()
        binding.tilEtPinCode.clearFocus()
        binding.tilEtPassword.clearFocus()
        binding.tilEtCPassword.clearFocus()
    }


    private fun setIconColor(textInputLayout: TextInputLayout, hasFocus: Boolean) {
        val colorFocussed = ResourcesCompat.getColor(resources, R.color.color_primary, null)
        val colorNonFocussed = ResourcesCompat.getColor(resources, R.color.unselected, null)
        val color = if (hasFocus) colorFocussed else colorNonFocussed
        textInputLayout.setStartIconTintList(ColorStateList.valueOf(color))
    }





    private fun initializeDatePicker() {
        DatePickerDialog(
            requireActivity(),
            date,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateLabel() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        Log.i("arun", "${dateFormat.format(myCalendar.time)}")
        binding.tilEtDOB.setText(dateFormat.format(myCalendar.time))

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


}