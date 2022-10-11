package com.example.notedesk.presentation.profilePage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentProfileBinding
import com.example.notedesk.presentation.activity.MainActivity
import com.example.notedesk.presentation.attachmentPreview.AttachmentPerviewFragment
import com.example.notedesk.presentation.home.listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.profilePage.profile_preview.AccountFragment
import com.example.notedesk.presentation.profilePage.profile_preview.PersonalFragment
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.presentation.util.setup
import com.example.notedesk.presentation.util.withArgs
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.util.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance(userID: Int) =
            ProfileFragment().withArgs {

                putInt(Keys.USER_ID, userID)


            }
    }


    private lateinit var binding: FragmentProfileBinding
    private lateinit var mViewPager: ViewPager
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null
    private val viewModel: ProfileViewModel by viewModels()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentNavigationLisenter) {
            fragmentNavigationLisenter = context
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        getArgumentParcelable()
        return binding.root
    }

    private fun initializeTableLayout() {
        mViewPager = binding.viewPager2
        binding.tabLayout.setupWithViewPager(mViewPager)
        val titleList = mutableListOf("Account", "Personal")

        val accountList = ArrayList<ProfileDetails>(
        ).apply {

            add(ProfileDetails(R.drawable.ic_name, "First Name", viewModel.user.firstName))
            add(ProfileDetails(R.drawable.ic_name, "Last Name", viewModel.user.lastName))
            add(ProfileDetails(R.drawable.ic_baseline_email_24, "Email ID", viewModel.user.email))
            add(ProfileDetails(R.drawable.ic_bio, "Bio ", viewModel.user.bio))
            add(ProfileDetails(R.drawable.ic_gender, "Gender ", viewModel.user.gender.toString()))
            add(ProfileDetails(R.drawable.ic_dob, "Date of Birth ", viewModel.user.dob))
            add(
                ProfileDetails(
                    R.drawable.ic_baseline_phone_android_24,
                    "Mobile Number ",
                    viewModel.user.mobileNumber
                )
            )
            add(
                ProfileDetails(
                    R.drawable.ic_baseline_home_24,
                    "Address Line 1 ",
                    viewModel.user.addressLine1
                )
            )
            add(
                ProfileDetails(
                    R.drawable.ic_baseline_home_24,
                    "Address Line 2 ",
                    viewModel.user.addressLine2
                )
            )
            add(ProfileDetails(R.drawable.ic_baseline_location_on_24, "City ", viewModel.user.city))

            add(
                ProfileDetails(
                    R.drawable.ic_baseline_location_on_24,
                    "Pincode ",
                    viewModel.user.pinCode
                )
            )

        }


        val personalList = ArrayList<ProfileDetails>().apply {
            (add(
                ProfileDetails(
                    R.drawable.ic_baseline_verified_user_24,
                    "USER ID",
                    "NOTEDESK0100${viewModel.user.userId}"
                )
            ))
            add(ProfileDetails(R.drawable.ic_name, "First Name", viewModel.user.firstName))
            add(ProfileDetails(R.drawable.ic_name, "Last Name", viewModel.user.lastName))
            add(ProfileDetails(R.drawable.ic_baseline_email_24, "Email ID", viewModel.user.email))


        }

        Log.i("kum", "data profile $personalList  $accountList")

        val fragment = mutableListOf(
            AccountFragment.newInstance(accountList),
            PersonalFragment.newInstance(personalList)
        )


        mViewPager.adapter = VpAdaptor(childFragmentManager, titleList, fragment)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backPressed()
        initializationComponents()
        setData()
        eventHandler()


    }

    private fun eventHandler() {
        profileViewListener()
    }

    private fun profileViewListener() {
        binding.ivProfilePicture.setOnClickListener {

            val name = viewModel.user.image
            if (name != "") {
                if (name != null) {
                    fragmentNavigationLisenter?.navigate(
                        AttachmentPerviewFragment.newInstance(name),
                        BackStack.ATTACHMENT_PREVIEW
                    )
                }
            } else {
                Toast.makeText(requireContext(), "Pls set the Profile Image", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun initializationComponents() {
        initializeToolBar()
        initializeTableLayout()
        initializeMenu()
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
                    R.id.menu_edit -> {
                        fragmentNavigationLisenter?.navigate(
                            EditProfileFragment.newInstance(
                                viewModel.user
                            ), BackStack.PROFILE_EDIT
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }


    private fun setData() {
        if (viewModel.user.image != "") {

            lifecycleScope.launch(Dispatchers.IO)
            {
                withContext(Dispatchers.IO)
                {
                    val bitmap = viewModel.user.image?.let {
                        Storage.getPhotosFromInternalStorage(
                            requireActivity(),
                            it
                        )?.bmp
                    }
                    launch(Dispatchers.Main) {
                        if (bitmap != null) binding.ivProfilePicture.setImageBitmap(bitmap)
                        else binding.ivProfilePicture.setImageResource(R.drawable.ic_profile_picture)

                    }


                }

            }
        }
        binding.Name.text = viewModel.user.firstName
        binding.email.text = viewModel.user.email


    }


    private fun getArgumentParcelable() {

        lifecycleScope.launch()
        {
            withContext(Dispatchers.IO)

            {
                viewModel.userId = (requireActivity() as MainActivity).getUserID()
                viewModel.user = viewModel.getUser(viewModel.userId)

                viewModel.oldImagePath = viewModel.user.image
            }
        }

    }


    private fun initializeToolBar() {
        val toolbar: Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.setup(requireActivity(), "Profile Page")

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