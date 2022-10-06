package com.example.notedesk.presentation.signup

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
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
import androidx.core.content.res.ResourcesCompat
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
import com.google.android.material.textfield.TextInputLayout
import com.shuhart.stepview.StepView
import java.text.SimpleDateFormat
import java.util.*


class PersonalDetailFragment : Fragment(), DialogLisenter {

    private lateinit var binding: FragmentPersonalDetailBinding
    private var navigationLisenter: Navigate? = null
    private lateinit var myCalendar: Calendar
    private lateinit var date: DatePickerDialog.OnDateSetListener
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
        setUpFocusChangeListeners()
        setupProfilePicture()

    }

    private fun eventHandler() {
        stepViewAction()
        nextListener()
        dropDownIntialization()
        dateEvent()
        binding.tilEtDOB.setOnClickListener {
            initializeDatePicker()
            binding.tilEtDOB.setOnFocusChangeListener()
            { _, hasFocus ->

                setIconColor(binding.tilDOB, hasFocus)
            }

        }
    }

    private fun dateEvent() {
        myCalendar = Calendar.getInstance()
        date =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }

    }

    private fun dropDownIntialization() {
        val languages = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown, languages)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    private fun nextListener() {
        binding.btnNext.setOnClickListener {

            if (validateData())
                navigationLisenter?.navigate(AddressFragment())


        }
    }

    private fun stepViewAction() {
        requireActivity().findViewById<StepView>(R.id.stepView).go(1, true)
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

    private fun validateData(): Boolean {
        val mobileNumber = binding.tilEtMobile.text.toString()
        val bio = binding.tilEtBio.text.toString()
        val gender = binding.autoCompleteTextView.text.toString()
        val date = binding.tilEtDOB.text.toString()


        if (mobileNumber.isNotEmpty()) {
            val res = ValidateMobileNumber.execute(mobileNumber)
            if (!res.successful) {
                binding.tilMobile.error = res.errorMessage
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
            if (this.image!=null)
            {
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
            else -> Gender.NOT_INTERESTED

        }

    }


    private fun updateLabel() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        binding.tilEtDOB.setText(dateFormat.format(myCalendar.time))

    }

    override fun choice(choice: AddImage) {
        when (choice) {
            AddImage.TAKE_PHOTO -> {
                takePhoto()
            }
            AddImage.CHOOSE_PHOTO -> {
                chooseImage()
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
                binding.ivProfilePicture.setImageBitmap(bitmap)
                viewModel.imageFileName = filename
                if (isSavedSuccessfully) {

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
            binding.ivProfilePicture.setImageBitmap(it)
            viewModel.imageFileName = filename
            if (isSavedSuccessfully == true) {

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
            if (hasFocus) binding.tilBio.error = null
            setIconColor(
                binding.tilDOB,
                hasFocus
            )
        }


        binding.tilEtDOB.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilDOB.error = null
            setIconColor(
                binding.tilDOB,
                hasFocus
            )
        }




        binding.tilDOB.setOnFocusChangeListener()
        { _, b ->
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


    private fun setIconColor(textInputLayout: TextInputLayout, hasFocus: Boolean) {
        val colorFocussed = ResourcesCompat.getColor(resources, R.color.color_primary, null)
        val colorNonFocussed = ResourcesCompat.getColor(resources, R.color.unselected, null)
        val color = if (hasFocus) colorFocussed else colorNonFocussed
        textInputLayout.setStartIconTintList(ColorStateList.valueOf(color))
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