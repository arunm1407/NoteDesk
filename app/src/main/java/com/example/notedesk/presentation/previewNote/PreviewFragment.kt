package com.example.notedesk.presentation.previewNote

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notedesk.domain.util.storage.InternalStoragePhoto
import com.example.notesappfragment.R
import com.example.notedesk.presentation.attachmentPreview.adaptor.AttachmentAdaptor
import com.example.notesappfragment.databinding.FragmentPerviewBinding
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.domain.util.keys.Constants
import com.example.notedesk.domain.util.keys.IndentKeys
import com.example.notedesk.domain.util.storage.Storage
import com.example.notedesk.domain.util.date.DateUtil.getDateAndTime
import com.example.notedesk.presentation.attachmentPreview.listener.AttachmentLisenter
import com.example.notedesk.presentation.createNote.adaptor.UrlAdaptor
import com.example.notedesk.presentation.createNote.listener.UrlListener
import com.example.notedesk.presentation.home.Listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.attachmentPreview.AttachmentPerviewFragment
import com.example.notedesk.presentation.createNote.CreateNotesFragment
import com.example.notedesk.presentation.home.enums.MenuActions
import com.example.notedesk.presentation.util.BackStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PreviewFragment : Fragment(), AttachmentLisenter, UrlListener {


    companion object {
        private const val SAVED_NOTES = "notes"
        fun newInstance(data: Notes) = PreviewFragment().apply {
            val bundle = Bundle()
            bundle.putParcelable(SAVED_NOTES, data)
            arguments = bundle
        }
    }


    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var binding: FragmentPerviewBinding
    private lateinit var notes: Notes
    private lateinit var filenames: List<String>
    private val viewModel: PerviewViewModel by viewModels()
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPerviewBinding.inflate(layoutInflater, container, false)
        getArgumentParcelable()

        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment ?: context
        if (parent is FragmentNavigationLisenter) {
            fragmentNavigationLisenter = parent
        }

    }

    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        notes = bundle.getParcelable(SAVED_NOTES)!!
        lifecycleScope.launch(Dispatchers.IO)
        {
            filenames = viewModel.getFileName(notes.id)
            withContext(Dispatchers.Main)
            {
                initAttachmentTitle()
                triggerAttachmentList()
            }


        }

    }


    private fun displayUrl(list: MutableList<String>) {
        val recyclerView = binding.rvUrl
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = UrlAdaptor(list, this)
    }


    @SuppressLint("Range")
    private fun restoreDataToView() {
        binding.createdTime.text = "Created Time :${getDateAndTime(notes.createdTime)}"
        if (notes.modifiedTime.toInt() != notes.createdTime.toInt()) {
            binding.updatedTime.text = "Updated Time :${getDateAndTime(notes.modifiedTime)}"
            binding.updatedTime.visibility = View.VISIBLE
        } else {
            binding.updatedTime.visibility = View.GONE
        }
        binding.inputNoteTitle.text = notes.title
        binding.inputNoteSubtitle.text = notes.subtitle
        binding.inputNote.text = notes.noteText
        binding.preview.setBackgroundColor(Color.parseColor(notes.color))

        if (notes.weblink.isNotEmpty()) {
            displayUrl(notes.weblink)
            binding.rvUrl.visibility = View.VISIBLE
        }
        displayUrl(notes.weblink)
        if (notes.favorite) {
            binding.yesAFaviortie.visibility = View.VISIBLE
            binding.notAFaviorte.visibility = View.GONE
        } else {
            binding.notAFaviorte.visibility = View.VISIBLE
            binding.yesAFaviortie.visibility = View.GONE


        }
        when (notes.priority) {
            IndentKeys.GREEN ->
                binding.apply {
                    green.setImageResource(R.drawable.ic_done)
                    yellow.setImageResource(0)
                    red.setImageResource(0)

                }
            IndentKeys.RED ->
                binding.apply {
                    red.setImageResource(R.drawable.ic_done)
                    yellow.setImageResource(0)
                    green.setImageResource(0)

                }


            IndentKeys.YELLOW -> {
                binding.apply {
                    yellow.setImageResource(R.drawable.ic_done)
                    green.setImageResource(0)
                    red.setImageResource(0)

                }
            }

        }


    }

    private fun triggerAttachmentList() {

        if (filenames.isNotEmpty()) {
            var list: MutableList<InternalStoragePhoto>?
            lifecycleScope.launch {
                list = Storage.getPhotosFromInternalStorage(filenames, requireActivity())
                displayAttachment(list!!)
            }


        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeMenu()
        initializeToolBar()
        restoreDataToView()


    }


    private fun initializeMenu() {

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.perview, menu)

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        navigate()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun initAttachmentTitle() {
        if (filenames.isNotEmpty()) {
            binding.tvAttachmentTitle.visibility = View.VISIBLE
            binding.tvAttachmentTitle.text = "Attachment (${filenames.size})"
            hideRv()
        } else {
            binding.tvAttachmentTitle.visibility = View.GONE

        }

    }

    private fun navigate() {
        val fragment = CreateNotesFragment.newInstance(notes, MenuActions.NOACTION)
        fragmentNavigationLisenter?.navigate(fragment, BackStack.EDIT)
    }


    private fun hideRv() {
        binding.rvAttachmentProgress.visibility = View.VISIBLE
        binding.rvAttachment.visibility = View.GONE
        val parent = binding.layout1
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)
        constraintSet.connect(
            R.id.updated_time,
            ConstraintSet.TOP, R.id.rv_attachment_progress,
            ConstraintSet.BOTTOM
        )
        constraintSet.applyTo(parent)

    }

    private fun displayRv() {
        binding.rvAttachmentProgress.visibility = View.GONE
        binding.rvAttachment.visibility = View.VISIBLE
        val parent = binding.layout1
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)
        constraintSet.connect(
            R.id.updated_time,
            ConstraintSet.TOP,
            R.id.rv_attachment,
            ConstraintSet.BOTTOM
        )
        constraintSet.applyTo(parent)

    }

    private fun initializeToolBar() {
        toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.title = Constants.PREVIEW_FRAGMENT
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setDisplayShowHomeEnabled(true)
            }


        }
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)

    }


    private fun displayAttachment(list: MutableList<InternalStoragePhoto>) {
        val adaptor = AttachmentAdaptor(
            list, this,
            isDelete = false
        )
        val recyclerView = binding.rvAttachment
        recyclerView.let {
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            it.adapter = adaptor
            displayRv()
        }


    }


    override fun onAttachmentClicked(internalStoragePhoto: InternalStoragePhoto) {
        val fragment = AttachmentPerviewFragment.newInstance(internalStoragePhoto)
        fragmentNavigationLisenter?.navigate(fragment, BackStack.ATTACHMENT_PREVIEW)
    }

    override fun onDelete(internalStoragePhoto: InternalStoragePhoto, position: Int) {

    }

    override fun removeUrl(string: String) {

    }


}