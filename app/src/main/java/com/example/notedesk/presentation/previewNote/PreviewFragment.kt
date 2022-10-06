package com.example.notedesk.presentation.previewNote

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notedesk.R
import com.example.notedesk.presentation.attachmentPreview.adaptor.AttachmentAdaptor
import com.example.notedesk.databinding.FragmentPerviewBinding
import com.example.notedesk.domain.model.Note
import com.example.notedesk.presentation.activity.MainActivity
import com.example.notedesk.util.keys.Constants
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.util.date.DateUtil.getDateAndTime
import com.example.notedesk.util.keys.Keys.SAVED_NOTES
import com.example.notedesk.presentation.attachmentPreview.listener.AttachmentLisenter
import com.example.notedesk.presentation.createNote.adaptor.UrlAdaptor
import com.example.notedesk.presentation.createNote.listener.UrlListener
import com.example.notedesk.presentation.home.listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.attachmentPreview.AttachmentPerviewFragment
import com.example.notedesk.presentation.createNote.CreateNotesFragment
import com.example.notedesk.presentation.home.enums.MenuActions
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.presentation.util.initRecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PreviewFragment : Fragment(), AttachmentLisenter, UrlListener {


    companion object {

        fun newInstance(data: Note) = PreviewFragment().apply {
            val bundle = Bundle()
            bundle.putParcelable(SAVED_NOTES, data)
            arguments = bundle
        }
    }

    private val viewModel: PreviewViewModel by viewModels()
    private lateinit var binding: FragmentPerviewBinding
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        getArgumentParcelable()

        val parent = parentFragment ?: context
        if (parent is FragmentNavigationLisenter) {
            fragmentNavigationLisenter = parent
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPerviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        viewModel.notes = (bundle.getParcelable(SAVED_NOTES)!!)


        lifecycleScope.launch()
        {
            withContext(Dispatchers.IO)
            {
                viewModel.filenames = viewModel.getFileName(
                    viewModel.notes.id,
                    (requireActivity() as MainActivity).getUserID()
                )

            }
        }


    }


    private fun displayUrl(list: MutableList<String>) {
        binding.rvUrl.initRecyclerView(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            ), UrlAdaptor(list, this, false), true
        )

    }


    @SuppressLint("Range")
    private fun restoreDataToView() {
        binding.createdTime.text =
            resources.getString(R.string.createdtime, getDateAndTime(viewModel.notes.createdTime))
        if (viewModel.notes.modifiedTime.toInt() != viewModel.notes.createdTime.toInt()) {
            binding.updatedTime.text = resources.getString(
                R.string.createdtime,
                getDateAndTime(viewModel.notes.modifiedTime)
            )
            binding.updatedTime.visibility = View.VISIBLE
        } else {
            binding.updatedTime.visibility = View.GONE
        }
        binding.inputNoteTitle.text = viewModel.notes.title
        binding.inputNoteSubtitle.text = viewModel.notes.subtitle
        binding.inputNote.text = viewModel.notes.noteText
        binding.preview.setBackgroundColor(Color.parseColor(viewModel.notes.color))

        if (viewModel.notes.weblink.isNotEmpty()) {
            displayUrl(viewModel.notes.weblink)
            binding.rvUrl.visibility = View.VISIBLE
        }
        displayUrl(viewModel.notes.weblink)
        binding.like.isChecked = viewModel.notes.favorite
        when (viewModel.notes.priority) {
            Keys.GREEN ->
                binding.apply {
                    green.setImageResource(R.drawable.ic_done)
                    yellow.setImageResource(0)
                    red.setImageResource(0)

                }
            Keys.RED ->
                binding.apply {
                    red.setImageResource(R.drawable.ic_done)
                    yellow.setImageResource(0)
                    green.setImageResource(0)

                }


            Keys.YELLOW -> {
                binding.apply {
                    yellow.setImageResource(R.drawable.ic_done)
                    green.setImageResource(0)
                    red.setImageResource(0)

                }
            }

        }


    }

    private fun triggerAttachmentList() {
        if (viewModel.filenames.isNotEmpty()) {
            displayAttachment(viewModel.filenames)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAttachmentTitle()
        triggerAttachmentList()
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
        if (viewModel.filenames.isNotEmpty()) {
            binding.tvAttachmentTitle.visibility = View.VISIBLE
            binding.tvAttachmentTitle.text =
                resources.getString(R.string.attachmentCount, viewModel.filenames.size)
            hideRv()
        } else {
            binding.tvAttachmentTitle.visibility = View.GONE

        }

    }

    private fun navigate() {
        val fragment = CreateNotesFragment.newInstance(viewModel.notes, MenuActions.NOACTION)
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
        val toolbar: androidx.appcompat.widget.Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.title = Constants.PREVIEW_FRAGMENT
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setDisplayShowHomeEnabled(true)
            }


        }
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_arrow_back_24)

    }


    private fun displayAttachment(list: List<String>) {
        binding.rvAttachment.initRecyclerView(
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false),
            AttachmentAdaptor(
                list.toMutableList(), this,
                isDelete = false
            ),
            true
        )
        displayRv()


    }


    override fun onAttachmentClicked(name: String) {
        val fragment = AttachmentPerviewFragment.newInstance(name)
        fragmentNavigationLisenter?.navigate(fragment, BackStack.ATTACHMENT_PREVIEW)
    }

    override fun onDelete(name: String, position: Int) {

    }

    override fun removeUrl(string: String) {

    }


}