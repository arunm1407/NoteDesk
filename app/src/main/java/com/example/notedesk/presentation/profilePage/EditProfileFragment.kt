package com.example.notedesk.presentation.profilePage

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
import androidx.activity.result.launch
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.notedesk.R
import com.example.notedesk.data.data_source.User
import com.example.notedesk.databinding.FragmentEditProfileBinding
import com.example.notedesk.domain.usecase.CheckField
import com.example.notedesk.domain.usecase.ValidateMobileNumber
import com.example.notedesk.domain.usecase.ValidatePinCode
import com.example.notedesk.presentation.attachmentPreview.AttachmentPerviewFragment
import com.example.notedesk.presentation.createNote.dailog.CameraSettingDailog
import com.example.notedesk.presentation.createNote.dailog.StorageSettings
import com.example.notedesk.presentation.createNote.enums.AddImage
import com.example.notedesk.presentation.createNote.listener.DialogLisenter
import com.example.notedesk.presentation.home.listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.util.*
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.util.storage.Storage
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
    private val viewModel: EditNoteViewModel by viewModels()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentNavigationLisenter) {
            fragmentNavigationLisenter = context
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        initializeMenu()
        backPressed()
        setupProfilePicture()
        dropDownIntialization()
        removeError()
        binding.tilEtPinCode.actionDone()
    }


    private fun profileViewListener() {
        binding.ivProfilePicture.setOnClickListener {

            val name = viewModel.user.image
            if (name != "") {
                if (name != null) {
                    AttachmentPerviewFragment.newInstance(name)
                    fragmentNavigationLisenter?.navigate(
                        AttachmentPerviewFragment.newInstance(name),
                        BackStack.ATTACHMENT_PREVIEW
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Pls set the Profile Image",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

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

    private fun dropDownIntialization() {
        val languages = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown, languages)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }


    private fun removeError() {
        binding.tilEtFirstName.clearErrorOnClick(binding.tilFirstName)
        binding.tilEtLastName.clearErrorOnClick(binding.tilLastName)
        binding.tilEtMobile.clearErrorOnClick(binding.tilMobile)
        binding.tilEtPinCode.clearErrorOnClick(binding.tilPinCode)

    }

    private fun setDataToView() {
        viewModel.user.apply {
            binding.tilEtFirstName.setText(firstName)
            binding.tilEtLastName.setText(lastName)
            binding.tilEtEmail.setText(email)
            binding.tilEtBio.setText(bio)
            binding.autoCompleteTextView.setText(gender.toString())
            binding.tilEtDOB.setText(dob)
            binding.tilEtMobile.setText(mobileNumber)
            binding.tilEtaddress1.setText(addressLine1)
            binding.tilEtaddress2.setText(addressLine2)
            binding.tilEtcity.setText(city)
            binding.tilEtPinCode.setText(pinCode)
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
                        if (validateForm()) updateData()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun validateForm(): Boolean {
        var flag = true
        var res = CheckField.execute(binding.tilEtFirstName.text.toString(), "FirstName")
        if (!res.successful) {
            binding.tilFirstName.setErrorMessage(res.errorMessage)
            flag = false
        }
        res = CheckField.execute(binding.tilEtLastName.text.toString(), "LastName")
        if (!res.successful) {
            binding.tilLastName.setErrorMessage(res.errorMessage)
            flag = false
        }
        val mobile = binding.tilEtMobile.getString()
        if (mobile.isNotEmpty()) {
            res = ValidateMobileNumber.execute(mobile)
            if (!res.successful) {
                binding.tilMobile.setErrorMessage(res.errorMessage)
                flag = false
            }
        }
        val pincode = binding.tilEtPinCode.getString()
        if (pincode.isNotEmpty()) {
            res = ValidatePinCode.execute(pincode)
            if (!res.successful) {
                binding.tilPinCode.setErrorMessage(res.errorMessage)
                flag = false
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
                            viewModel.updateData(viewModel.user)
                        }
                        alertDialog.dismiss()
                        fragmentNavigationLisenter?.navigate(ProfileFragment(), BackStack.PROFILE)

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
        viewModel.user.apply {
            firstName = binding.tilEtFirstName.getString()
            lastName = binding.tilEtLastName.getString()
            bio = binding.tilEtBio.getString()
            gender = binding.autoCompleteTextView.getString().getGender()
            dob = binding.tilEtDOB.getString()
            mobileNumber = binding.tilEtMobile.getString()
            addressLine1 = binding.tilEtaddress1.getString()
            addressLine2 = binding.tilEtaddress2.getString()
            city = binding.tilEtcity.getString()
            pinCode = binding.tilEtPinCode.getString()


        }

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
        viewModel.user.image = ""
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
            else showDialogFragment(CameraSettingDailog())
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
                    viewModel.user.image = "$filename.jpg"
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
                viewModel.user.image = "$filename.jpg"
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
                            parentFragmentManager.popBackStack()
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
            })
    }
}

