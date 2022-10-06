package com.example.notedesk.presentation.attachmentPreview

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentAttachmentPerviewBinding
import com.example.notedesk.util.keys.Keys.MAIN
import com.example.notedesk.util.storage.InternalStoragePhoto
import com.example.notedesk.util.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AttachmentPerviewFragment : Fragment() {


    companion object {


        fun newInstance(name: String) =
            AttachmentPerviewFragment().apply {
                val bundle = Bundle()
                bundle.putString(MAIN, name)
                arguments = bundle

            }
    }

    private lateinit var binding: FragmentAttachmentPerviewBinding
    private val viewModel: AttachmentPreviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAttachmentPerviewBinding.inflate(layoutInflater, container, false)
        getArgumentParcelable()
        lifecycleScope.launch()
        {
            withContext(Dispatchers.Main)
            {
                binding.animationView.playAnimation()
                val internalStoragePhoto: InternalStoragePhoto
                withContext(Dispatchers.IO)
                {
                        internalStoragePhoto = Storage.getPhotosFromInternalStorage(requireActivity(),viewModel.name)!!
                }

                binding.animationView.cancelAnimation()
                binding.animationView.visibility = View.GONE
                binding.imagePreview.setImageBitmap(internalStoragePhoto.bmp)
            }

        }

        initializeMenu()
        return binding.root
    }


    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        viewModel.name = bundle.getString(MAIN)!!

    }

    private fun initializeToolBar() {
        val toolbar:Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.menu.clear()
        toolbar.title =requireContext().getString(R.string.attachmentName,viewModel.name)
        (activity as AppCompatActivity).apply {
            this.setSupportActionBar(toolbar)
            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        toolbar.navigationIcon =  ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_arrow_back_24)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeToolBar()
        backPressed()
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








    private fun backPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()

                }
            })
    }

}










