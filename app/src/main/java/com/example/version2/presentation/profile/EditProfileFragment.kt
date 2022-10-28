package com.example.version2.presentation.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.activity.result.launch
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.storage.Storage
import com.example.version2.R
import com.example.version2.databinding.FragmentEditProfileBinding
import com.example.version2.domain.model.Gender
import com.example.version2.domain.model.User
import com.example.version2.domain.usecase.ValidationResult
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.common.NoteScreen
import com.example.version2.presentation.createNote.dialog.CameraSettingsDialog
import com.example.version2.presentation.createNote.dialog.StorageSettings
import com.example.version2.presentation.createNote.enums.AddImage
import com.example.version2.presentation.createNote.listener.DialogLisenter
import com.example.version2.presentation.homeScreen.listener.FragmentNavigationLisenter
import com.example.version2.presentation.util.*
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.util.*


class EditProfileFragment : Fragment(), DialogLisenter {


    companion object {


        fun newInstance(user: User): EditProfileFragment = EditProfileFragment().withArgs {
            putSerializable(Keys.USER, user)
        }
    }

    private lateinit var binding: FragmentEditProfileBinding
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null
    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProvider(
            this,
            (requireActivity().applicationContext as NotesApplication).editProfileFactory
        )[EditProfileViewModel::class.java]
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentNavigationLisenter) {
            fragmentNavigationLisenter = context
        }
        getArgumentData()

    }


    private fun getArgumentData() {
        val bundle = arguments
        viewModel.user = bundle?.getSerializable(Keys.USER) as User
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        setDataToView()
        dateListener()
        profileViewListener()

    }

    private fun initialization() {

        initializeToolBar()
        setUpFocusChangeListeners()
        initializeMenu()
        backPressed()
        setupProfilePicture()
        setupCustomSpinner()
        removeError()
        binding.tilEtPinCode.actionDone()
    }


    private fun profileViewListener() {
        binding.ivProfilePicture.setOnClickListener {

            val name = viewModel.user.image
            if (name != "") {
                if (name != null) {
                    fragmentNavigationLisenter?.navigateToAttachmentPreviewScreen(name)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.pls_Set_profile),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

        }
    }


    private fun retrieveGenderToView(gender: Gender) {
        viewModel.gender = gender
        when (gender) {
            Gender.MEN -> {
                binding.tilGender.setSelection(0)
            }

            Gender.WOMEN -> {
                binding.tilGender.setSelection(1)

            }
            Gender.NOT_SPECIFIED -> {
                binding.tilGender.setSelection(2)
            }


        }
    }


    private fun setUpFocusChangeListeners() {


        binding.tilEtLastName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLastName.clearError()

        }
        binding.tilEtFirstName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilFirstName.clearError()
        }
        binding.tilEtBio.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) binding.tilBio.clearError()
        }
        binding.tilEtMobile.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) binding.tilMobile.clearError()
        }

        binding.tilEtaddress1.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) binding.tilAddressLine1.clearError()
        }
        binding.tilEtaddress2.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) binding.tilAddressLine2.clearError()
        }
        binding.tilEtcity.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) binding.tilCity.clearError()
        }
        binding.tilEtPinCode.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) binding.tilPinCode.clearError()
        }
    }
    @SuppressLint("ResourceAsColor")
    private fun initializeDatePicker() {

        val constraints =
            CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())

        val picker = MaterialDatePicker.Builder.datePicker().also {
            it.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            it.setTitleText(getString(R.string.select_date))
            it.setCalendarConstraints(constraints.build())
        }

        val builder = picker.build()
        builder.show(parentFragmentManager, getString(R.string.date_picker))
        builder.addOnPositiveButtonClickListener {
            val dob = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            binding.tilEtDOB.setText(dob.toString())

        }
    }


    private fun removeError() {
        binding.tilEtFirstName.clearErrorOnClick(binding.tilFirstName)
        binding.tilEtLastName.clearErrorOnClick(binding.tilLastName)
        binding.tilEtMobile.clearErrorOnClick(binding.tilMobile)
        binding.tilEtPinCode.clearErrorOnClick(binding.tilPinCode)
        binding.tilEtBio.clearErrorOnClick(binding.tilBio)
        binding.tilEtMobile.clearErrorOnClick(binding.tilMobile)
        binding.tilEtaddress1.clearErrorOnClick(binding.tilAddressLine1)
        binding.tilEtaddress2.clearErrorOnClick(binding.tilAddressLine2)
        binding.tilEtcity.clearErrorOnClick(binding.tilCity)
        binding.tilEtPinCode.clearErrorOnClick(binding.tilPinCode)

    }


    private fun setupCustomSpinner() {


        val languages = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdowntext, languages)
        binding.tilGender.adapter = arrayAdapter

        binding.tilGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {


                when (position) {

                    0 -> viewModel.gender = Gender.MEN
                    1 -> viewModel.gender = Gender.WOMEN
                    2 -> viewModel.gender = Gender.NOT_SPECIFIED

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


    }


    private fun setDataToView() {
        viewModel.user.apply {
            binding.tilEtFirstName.setText(firstName)
            binding.tilEtLastName.setText(lastName)
            binding.tilEtEmail.setText(email)
            binding.tilEtBio.setText(bio)
            retrieveGenderToView(gender)
            binding.tilEtDOB.setText(dob)
            binding.tilEtMobile.setText(mobileNumber)
            binding.tilEtaddress1.setText(addressLine1)
            binding.tilEtaddress2.setText(addressLine2)
            binding.tilEtcity.setText(city)
            binding.tilEtPinCode.setText(pinCode.toPincode())
            lifecycleScope.launch()
            {
                withContext(Dispatchers.IO)
                {
                    val bitmap =
                        image?.let { Storage.getPhotosFromInternalStorage(requireActivity(), it) }
                    launch(Dispatchers.Main) {
                        bitmap?.let {
                            binding.ivProfilePicture.setImageBitmap(bitmap.bmp)
                        }
                    }


                }


            }


        }
    }


    private fun initializeToolBar() {
        val toolbar: Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.setup(requireActivity(), getString(R.string.edit_profile_page))

    }

    private fun initializeMenu() {

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_create, menu)

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.menu_done -> {
                        if (validateForm())
                        {
                            updateData()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }



    private fun validateForm(): Boolean {
        var flag = true
        when (val res = viewModel.validateField(
            binding.tilEtFirstName.text.toString(),
            getString(R.string.first_name)
        )) {
            is ValidationResult.Error -> {

                binding.tilFirstName.setErrorMessage(res.message)
                binding.tilEtFirstName.clearFocus()
                flag = false

            }
            ValidationResult.Successful -> {

            }
        }


        when (val res = viewModel.validateField(
            binding.tilEtLastName.text.toString(),
            getString(R.string.last_name)
        )) {
            is ValidationResult.Error -> {

                binding.tilLastName.setErrorMessage(res.message)
                binding.tilEtLastName.clearFocus()

                flag = false

            }
            ValidationResult.Successful -> {

            }
        }


        val mobile = binding.tilEtMobile.getString()
        if (mobile.isNotEmpty()) {

            when (val res = viewModel.validateMobileNumber(mobile)) {
                is ValidationResult.Error -> {
                    binding.tilMobile.setErrorMessage(res.message)
                    flag = false
                }
                ValidationResult.Successful -> {

                }
            }


        }

        val address1 = binding.tilEtaddress1.getString()

        if (address1.isNotEmpty()) {
            when (val res = viewModel.validateString(address1, 40, "Address")) {
                is ValidationResult.Error -> {
                    binding.tilAddressLine1.setErrorMessage(res.message)
                    binding.tilEtaddress1.clearFocus()
                    flag = false
                }
                ValidationResult.Successful -> {


                }
            }

        }
        val address2 = binding.tilEtaddress2.getString()
        if (address2.isNotEmpty()) {
            when (val res = viewModel.validateString(address2, 40, "Address")) {
                is ValidationResult.Error -> {
                    binding.tilAddressLine2.setErrorMessage(res.message)
                    binding.tilEtaddress2.clearFocus()
                    flag = false
                }
                ValidationResult.Successful -> {


                }
            }

        }

        val pincode = binding.tilEtPinCode.getString()
        if (pincode.isNotEmpty()) {


            when (val res = viewModel.validatePinCode(pincode)) {
                is ValidationResult.Error -> {
                    binding.tilPinCode.setErrorMessage(res.message)
                    flag = false

                }
                ValidationResult.Successful -> {
                }
            }
        }
        return flag

    }


    private fun updateData() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.confirm_to_update))
            .setIcon(R.drawable.ic_baseline_update_24)
            .setMessage(getString(R.string.sure_Want_to_update))
            .setPositiveButton(
                getString(R.string.yes)

            ) { _, _ ->
                lifecycleScope.launch(Dispatchers.IO)
                {


                    withContext(Dispatchers.Main)
                    {

                        val alertDialog = setProgressDialog()
                        alertDialog.show()
                        getCurrentUserData()
                        launch(Dispatchers.IO) {
                            viewModel.updateData(
                                viewModel.user,
                                (requireActivity() as NoteScreen).getUserID()
                            )
                        }
                        alertDialog.dismiss()
                        fragmentNavigationLisenter?.navigateToProfileScreen((requireActivity() as NoteScreen).getUserID())

                    }


                }


            }
            .setNegativeButton(getString(R.string.no), null)


        builder.create().apply {
            show()
            getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
        }

    }

    private fun getCurrentUserData() {

        val user = User(
            firstName = binding.tilEtFirstName.getString(),
            lastName = binding.tilEtLastName.getString(),
            bio = binding.tilEtBio.getString(),
            gender = viewModel.gender,
            dob = binding.tilEtDOB.getString(),
            mobileNumber = binding.tilEtMobile.getString(),
            addressLine1 = binding.tilEtaddress1.getString(),
            addressLine2 = binding.tilEtaddress2.getString(),
            city = binding.tilEtcity.getString(),
            pinCode = binding.tilEtPinCode.getPinCode(),
            email = viewModel.user.email,
            password = viewModel.user.password,
            image = viewModel.image,
            isOnBoarded = viewModel.user.isOnBoarded

        )

        viewModel.user = user

    }


    private fun setProgressDialog(): AlertDialog {


        val layoutParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.gravity = Gravity.CENTER
        }


        val progressBar = ProgressBar(requireContext()).apply {
            isIndeterminate = true
            setPadding(0, 0, 30, 0)
            layoutParams = layoutParam
        }


        val tvText = TextView(requireContext()).apply {
            text = context.getString(R.string.updating_data)
            textSize = 20f
            layoutParams = layoutParam
        }

        val linearLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(30, 30, 30, 30)
            gravity = Gravity.CENTER
            layoutParams = layoutParam
            addView(progressBar)
            addView(tvText)
        }


        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setView(linearLayout)

        val dialog: AlertDialog = builder.create().apply {
            show()
            getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
        }


        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
        return dialog
    }

    private fun setupProfilePicture() {
        binding.btnProfilePicture.setOnClickListener {
            showDialogFragment(EditProfileImageDialog())

        }
    }


    private fun showDialogFragment(dialog: DialogFragment) {
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog.activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show(childFragmentManager, "2")
dialog.isCancelable=false

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
                deleteImage()
            }
        }
    }

    private fun deleteImage() {
        viewModel.setImage(null)
        binding.ivProfilePicture.setImageResource(R.drawable.ic_profile_picture)
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
            else showDialogFragment(CameraSettingsDialog())
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
                    viewModel.setImage("$filename.jpg")
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
                viewModel.setImage("$filename.jpg")
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

    private fun dateListener() {
        binding.tilEtDOB.setOnClickListener {
            initializeDatePicker()
        }
    }


    private val galleryPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->


            if (isGranted) selectImageFromGallery()
            else showDialogFragment(StorageSettings())
        }


    private fun backPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle(getString(R.string.confirm_to_exit))
                        .setMessage(getString(R.string.sure_to_Exit))
                        .setPositiveButton(
                            getString(R.string.yes)

                        )
                        { _, _ ->
                            fragmentNavigationLisenter?.navigateToPreviousScreen()
                        }
                        .setNegativeButton(getString(R.string.no), null)


                    builder.create().apply {
                        show()
                        getButton(DialogInterface.BUTTON_NEGATIVE)
                            .setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.color_primary
                                )
                            )
                        getButton(DialogInterface.BUTTON_POSITIVE)
                            .setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.color_primary
                                )
                            )
                    }

                }
            })
    }

}