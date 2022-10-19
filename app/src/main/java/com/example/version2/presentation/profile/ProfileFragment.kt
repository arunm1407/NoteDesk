package com.example.version2.presentation.profile

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.storage.Storage
import com.example.version2.R
import com.example.version2.databinding.FragmentProfileBinding
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.common.NoteScreen
import com.example.version2.presentation.attachmentPreview.AttachmentPreviewFragment
import com.example.version2.presentation.homeScreen.listener.FragmentNavigationLisenter
import com.example.version2.presentation.profile.preview.AccountFragment
import com.example.version2.presentation.profile.preview.PersonalFragment
import com.example.version2.presentation.util.BackStack
import com.example.version2.presentation.util.setup
import com.example.version2.presentation.util.toPincode
import com.example.version2.presentation.util.withArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {

    companion object {
        fun newInstance(userID: Int) =
            ProfileFragment().withArgs {

                putInt(Keys.USER_ID, userID)


            }
    }

    private lateinit var binding: FragmentProfileBinding
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null
    private val viewModel: ProfilePreviewViewModel by lazy {
        ViewModelProvider(
            this,
            (requireActivity().applicationContext as NotesApplication).profilePreview
        )[ProfilePreviewViewModel::class.java]
    }


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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backPressedListener()
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
            if (name != null) {
                fragmentNavigationLisenter?.navigate(
                    AttachmentPreviewFragment.newInstance(name),
                    BackStack.ATTACHMENT_PREVIEW
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.pls_set_profile_Data),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        }
    }

    private fun initializationComponents() {
        initializeToolBar()
        initializeTableLayout()
        initializeMenu()
    }


    private fun initializeTableLayout() {
        val mViewPager = binding.viewPager2
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
                    viewModel.user.pinCode.toPincode()
                )
            )

        }


        val personalList = ArrayList<ProfileDetails>().apply {
            (add(
                ProfileDetails(
                    R.drawable.ic_baseline_verified_user_24,
                    "USER ID",
                    "NOTEDESK0100${viewModel.userId}"
                )
            ))
            add(ProfileDetails(R.drawable.ic_name, "First Name", viewModel.user.firstName))
            add(ProfileDetails(R.drawable.ic_name, "Last Name", viewModel.user.lastName))
            add(ProfileDetails(R.drawable.ic_baseline_email_24, "Email ID", viewModel.user.email))


        }


        mViewPager.adapter = ViewPagerAdaptor(
            childFragmentManager, titleList, mutableListOf(
                PersonalFragment.newInstance(personalList),
                AccountFragment.newInstance(accountList)

            )
        )
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
        runBlocking(Dispatchers.IO) {
            viewModel.setUserId((requireActivity() as NoteScreen).getUserID())
            viewModel.user = viewModel.getUser(viewModel.userId)
        }


    }


    private fun initializeToolBar() {
        val toolbar: Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.setup(requireActivity(), "")

    }


    private fun backPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()

                }
            })
    }


}