package com.example.notedesk.presentation.profilePage

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.notedesk.R
import com.example.notedesk.data.data_source.User
import com.example.notedesk.databinding.FragmentProfileBinding
import com.example.notedesk.presentation.createNote.dailog.AddImageDailog
import com.example.notedesk.presentation.createNote.dailog.CameraSettingDailog
import com.example.notedesk.presentation.createNote.dailog.StorageSettings
import com.example.notedesk.presentation.createNote.enums.AddImage
import com.example.notedesk.presentation.createNote.listener.DialogLisenter
import com.example.notedesk.presentation.util.set
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.util.storage.InternalStoragePhoto
import com.example.notedesk.util.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ProfileFragment : Fragment(), DialogLisenter {

    companion object {
        fun newInstance(user: User) =
            ProfileFragment().apply {
                val bundle = Bundle()
                bundle.putSerializable(Keys.USER, user)
                arguments = bundle

            }
    }


    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getArgumentParcelable()
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeToolBar()
        initializeMenu()
        backPressed()
        restoreDataToView()
        setupProfilePicture()

    }


    private fun initializeMenu() {

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.profile, menu)

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.menu_done -> {

                        updateData()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun updateData() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Confirm to Update ?")
            .setIcon(R.drawable.ic_baseline_update_24)
            .setMessage("Are you want Update the Profile")
            .setPositiveButton(
                "Yes"

            ) { _, _ ->
                lifecycleScope.launch(Dispatchers.IO)
                {
                    Storage.deletePhotoFromInternalStorage(
                        viewModel.oldImagePath!!,
                        requireContext()
                    )
                    viewModel.updateData(viewModel.user)

                }

                parentFragmentManager.popBackStack()


            }
            .setNegativeButton("No", null)


        val alert: AlertDialog = builder.create()
        alert.show()
    }


    private fun restoreDataToView() {

        viewModel.user.bio.apply {
            if (this.isNotEmpty()) {
                binding.aboutMe.visibility = View.VISIBLE
                binding.tvContentBio.set(this)
            } else {
                binding.aboutMe.visibility = View.GONE
            }

        }



        binding.tvFirstNameValue.set(viewModel.user.firstName)
        binding.tvLastNameValue.set(viewModel.user.lastName)

        viewModel.user.dob.apply {

            if (this.isNotEmpty()) binding.tvDObValue.set(this)
            else binding.tvDObValue.set(getString(R.string.none))

        }
        binding.tvGenderValue.set(viewModel.user.gender.toString())

        viewModel.user.mobileNumber.apply {
            if (this.isNotEmpty()) binding.tvContactValue.set(this)
            else binding.tvContactValue.set(getString(R.string.none))
        }
        binding.tvEmailValue.set(viewModel.user.email)

        if (viewModel.user.addressLine1.isEmpty() && viewModel.user.addressLine2.isEmpty()) {
            binding.tvAddressValue.set(getString(R.string.none))
        } else {
            binding.tvAddressValue.set("${viewModel.user.addressLine1}  , ${viewModel.user.addressLine2}")
        }
        viewModel.user.city.apply {
            if (this.isNotEmpty()) binding.tvCityValue.set(this)
            else binding.tvCityValue.set(getString(R.string.none))

        }
        viewModel.user.pinCode.apply {
            if (this != 0) binding.tvPinCodeValue.set(this.toString())
            else binding.tvPinCodeValue.set(getString(R.string.none))

        }
        viewModel.user.image?.apply {

            var bitmap: InternalStoragePhoto?
            lifecycleScope.launch(Dispatchers.IO)
            {
                withContext(Dispatchers.IO) {

                    bitmap = this.let {
                        Storage.getPhotosFromInternalStorage(
                            requireActivity(),
                            this@apply
                        )
                    }

                }

                withContext(Dispatchers.Main)
                {
                    viewModel.user.firstName.let {

                        binding.Name.set(it)
                    }
                    bitmap?.let {

                        binding.ivProfilePicture.setImageBitmap(it.bmp)
                    }
                }
            }

        }

    }


    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        viewModel.user = bundle.getSerializable(Keys.USER) as User
        viewModel.oldImagePath = viewModel.user.image
    }






    private fun initializeToolBar() {
        val toolbar: Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.title = "Profile Page"
        (activity as AppCompatActivity).apply {
            this.setSupportActionBar(toolbar)

            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)

        }
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_arrow_back_24)


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
                binding.ivProfilePicture.setImageBitmap(bitmap)
                viewModel.user.image = filename
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



    private fun backPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()

                }
            })
    }


}