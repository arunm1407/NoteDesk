package com.example.notedesk.presentation.signup

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.notedesk.domain.usecase.ValidateMobileNumber
import com.example.notedesk.presentation.createNote.dailog.AddImageDailog
import com.example.notedesk.presentation.createNote.dailog.CameraSettingDailog
import com.example.notedesk.presentation.createNote.dailog.StorageSettings
import com.example.notedesk.presentation.createNote.enums.AddImage
import com.example.notedesk.presentation.createNote.listener.DialogLisenter
import com.example.notedesk.presentation.login.Gender
import com.example.notedesk.presentation.signup.listener.Navigate
import com.example.notedesk.util.storage.Storage
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentPersonalDetailBinding
import com.example.notedesk.presentation.util.checkNull
import com.example.notedesk.presentation.util.clearError
import com.example.notedesk.presentation.util.getString
import com.example.notedesk.presentation.util.setErrorMessage
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.shuhart.stepview.StepView
import java.time.Instant
import java.time.ZoneId
import java.util.*


class PersonalDetailFragment : Fragment(), DialogLisenter {

    private lateinit var binding: FragmentPersonalDetailBinding
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


        binding = FragmentPersonalDetailBinding.inflate(inflater)
        backPressed()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventHandler()
        removeError()
        setUpFocusChangeListeners()
        setupProfilePicture()
        dropDownIntialization()
    }

    private fun removeError() {
        binding.tilMobile.clearError()
        binding.tilEtMobile.setOnClickListener {
            binding.tilMobile.clearError()
        }
    }

    private fun eventHandler() {
        stepViewAction()
        nextListener()
        dateListener()

    }


    private fun dateListener() {
        binding.tilEtDOB.setOnClickListener {
            initializeDatePicker()
        }
    }


    private fun dropDownIntialization() {
        val languages = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown, languages)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        binding.autoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            binding.tilEtDOB.requestFocus()


        }


    }

    private fun nextListener() {
        binding.btnNext.setOnClickListener {

            if (validateData())
            { removeError()
                navigationLisenter?.navigate(AddressFragment()) }


        }
    }

    private fun stepViewAction() {
        requireActivity().findViewById<StepView>(R.id.stepView).go(1, true)
    }


    @SuppressLint("ResourceAsColor")
    private fun initializeDatePicker() {

        val constraints =
            CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())

        val picker = MaterialDatePicker.Builder.datePicker().also {
            it.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            it.setTitleText("select date")
            it.setCalendarConstraints(constraints.build())
        }

        val builder = picker.build()
        builder.show(parentFragmentManager, "date picker")
        builder.addOnPositiveButtonClickListener {
            val dob = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            binding.tilEtDOB.setText(dob.toString())

        }
    }

    private fun validateData(): Boolean {
        val mobileNumber = binding.tilEtMobile.getString()
        val bio = binding.tilEtBio.getString()
        val gender = binding.autoCompleteTextView.getString()
        val date = binding.tilEtDOB.getString()


        if (mobileNumber.isNotEmpty()) {
            val res = ValidateMobileNumber.execute(mobileNumber)
            if (!res.successful) {
                binding.tilMobile.setErrorMessage(res.errorMessage)
                return false
            }
        }
        updateData(bio, validateGender(gender), date, mobileNumber)
        return true
    }

    private fun updateData(bio: String, gender: Gender, date: String, mobileNumber: String) {
        viewModel.userData.apply {
            this.bio = bio
            this.gender = gender
            this.dob = date
            this.mobileNumber = mobileNumber
            if (!viewModel.imageFileName.checkNull()) {
                this.image = "${viewModel.imageFileName}.jpg"
            }


        }


    }


    private fun setupProfilePicture() {
        binding.btnProfilePicture.setOnClickListener {
            showDialogFragment(AddImageDailog())

        }
    }


    private fun showDialogFragment(dialog: DialogFragment) {
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog.activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show(childFragmentManager, "2")


    }


    private fun validateGender(gender: String): Gender {
        return when (gender) {
            resources.getStringArray(R.array.gender)[0] -> Gender.MEN
            resources.getStringArray(R.array.gender)[1] -> Gender.WOMEN
            else -> Gender.NOT_SPECIFIED

        }

    }


    override fun choice(choice: AddImage) {
        when (choice) {
            AddImage.TAKE_PHOTO -> {
                takePhoto()
            }
            AddImage.CHOOSE_PHOTO -> {
                chooseImage()
            }
            else -> {

            }
        }
    }

    private fun chooseImage() {
        if (checkAndRequestGalleryPermissions()) {
            selectImageFromGallery()
        }
    }

    private fun takePhoto() {
        if (checkAndRequestCameraPermissions()) {
            takePhoto.launch()
        }
    }


    private fun selectImageFromGallery() =
        selectImageFromGalleryResult.launch(getString(R.string.img))


    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(requireContext().contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                val filename = UUID.randomUUID().toString()
                val isSavedSuccessfully =
                    Storage.savePhotoToInternalStorage(filename, bitmap, requireActivity())

                if (isSavedSuccessfully) {
                    binding.ivProfilePicture.setImageBitmap(bitmap)
                    viewModel.imageFileName = filename
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.upload_success),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.failed_to_save),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {

            val filename = UUID.randomUUID().toString()
            val isSavedSuccessfully =
                it?.let { it1 ->
                    Storage.savePhotoToInternalStorage(filename, it1, requireActivity())
                }

            if (isSavedSuccessfully == true) {
                binding.ivProfilePicture.setImageBitmap(it)
                viewModel.imageFileName = filename
                Toast.makeText(
                    requireContext(),
                    getString(R.string.saved_success),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.failed_to_save_photo),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private fun checkAndRequestGalleryPermissions(): Boolean {

        val galleryPermission = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return if (galleryPermission == PackageManager.PERMISSION_GRANTED) {
            true
        } else {


            galleryPermissionResultLauncher.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            false
        }


    }


    private val galleryPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->


            if (isGranted) selectImageFromGallery()
            else showDialogFragment(StorageSettings())
        }


    private fun setUpFocusChangeListeners() {


        binding.tilEtBio.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilBio.clearError()
        }


        binding.tilEtMobile.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilMobile.clearError()

        }


        binding.tilGender.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilMobile.clearError()
            binding.autoCompleteTextView.showDropDown()
        }

    }

    private fun checkAndRequestCameraPermissions(): Boolean {

        val galleryPermission: Int =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        return if (galleryPermission == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            cameraPermissionResultLauncher.launch(
                Manifest.permission.CAMERA
            )
            false
        }

    }


    private val cameraPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->

            if (isGranted) takePhoto.launch()
            else showDialogFragment(CameraSettingDailog())
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