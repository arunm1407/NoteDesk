package com.example.version2.presentation.attachmentPreview

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.storage.InternalStoragePhoto
import com.example.version2.presentation.util.storage.Storage
import com.example.version2.R
import com.example.version2.databinding.FragmentAttachmentPreviewBinding
import com.example.version2.presentation.homeScreen.listener.FragmentNavigationLisenter
import com.example.version2.presentation.util.setup
import com.example.version2.presentation.util.withArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AttachmentPreviewFragment : Fragment() {


    companion object {
        fun newInstance(name: String) = AttachmentPreviewFragment().withArgs {
            putString(Keys.MAIN, name)
        }

    }


    private lateinit var binding: FragmentAttachmentPreviewBinding
    private val viewModel: AttachmentPreviewViewModel by viewModels()
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentNavigationLisenter) {
            fragmentNavigationLisenter = context
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgumentParcelable()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAttachmentPreviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeToolBar()
        initializeMenu()
        fetchData()
        setupBackPressedListener()
    }


    private fun initializeMenu() {

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.attachment, menu)

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.menu_perview_done -> {
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }


    private fun fetchData() {
        lifecycleScope.launch()
        {
            withContext(Dispatchers.Main)
            {
                binding.animationView.playAnimation()
                val internalStoragePhoto: InternalStoragePhoto
                withContext(Dispatchers.IO)
                {
                    internalStoragePhoto =
                        Storage.getPhotosFromInternalStorage(requireActivity(), viewModel.name)!!
                }

                binding.animationView.cancelAnimation()
                binding.animationView.visibility = View.GONE
                binding.imagePreview.setImageBitmap(internalStoragePhoto.bmp)
            }

        }
    }

    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        viewModel.name = bundle.getString(Keys.MAIN)!!
    }

    private fun initializeToolBar() {
        val toolbar: Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.setup(
            requireActivity(),
            requireContext().getString(R.string.attachmentName, viewModel.name)
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        fragmentNavigationLisenter = null
    }

    private fun setupBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    fragmentNavigationLisenter?.navigateToPreviousScreen()

                }
            })
    }
}