package com.example.notedesk.presentation.attachmentPreview

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.notesappfragment.R
import com.example.notesappfragment.databinding.FragmentAttachmentPerviewBinding
import com.example.notedesk.domain.util.storage.InternalStoragePhoto


class AttachmentPerviewFragment private constructor() : Fragment() {


    companion object {

        private const val INTERNAL_STORAGE_PHOTO = "InternalStoragePhoto"
        fun newInstance(internalStoragePhoto: InternalStoragePhoto) =
            AttachmentPerviewFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(INTERNAL_STORAGE_PHOTO, internalStoragePhoto)
                arguments = bundle

            }
    }

    private lateinit var binding: FragmentAttachmentPerviewBinding
    private lateinit var internalStoragePhoto: InternalStoragePhoto
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAttachmentPerviewBinding.inflate(layoutInflater, container, false)
        getArgumentParcelable()
        binding.imagePreview.setImageBitmap(internalStoragePhoto.bmp)
        initializeMenu()
        return binding.root
    }


    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        internalStoragePhoto = bundle.getParcelable(INTERNAL_STORAGE_PHOTO)!!

    }

    private fun initializeToolBar() {
        toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.menu.clear()
        toolbar.title = "Img_${internalStoragePhoto.name}"
        (activity as AppCompatActivity).apply {
            this.setSupportActionBar(toolbar)
            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeToolBar()
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

}










