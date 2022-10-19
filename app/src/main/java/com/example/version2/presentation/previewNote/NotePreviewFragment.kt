package com.example.version2.presentation.previewNote

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.version2.presentation.util.date.DateUtil
import com.example.version2.presentation.util.keys.Constants
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.R
import com.example.version2.databinding.FragmentNotePreviewBinding
import com.example.version2.domain.model.Attachment
import com.example.version2.domain.model.Note
import com.example.version2.domain.model.Priority
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.attachmentPreview.AttachmentPreviewFragment
import com.example.version2.presentation.attachmentPreview.adaptor.AttachmentAdaptor
import com.example.version2.presentation.attachmentPreview.listener.AttachmentLisenter
import com.example.version2.presentation.createNote.CreateNoteFragment
import com.example.version2.presentation.createNote.adaptor.UrlAdaptor
import com.example.version2.presentation.createNote.listener.UrlListener
import com.example.version2.presentation.homeScreen.enums.MenuActions
import com.example.version2.presentation.homeScreen.listener.FragmentNavigationLisenter
import com.example.version2.presentation.util.*


class NotePreviewFragment : Fragment(), AttachmentLisenter, UrlListener {


    companion object {

        fun newInstance(data: Note) = NotePreviewFragment().withArgs {

            putSerializable(Keys.SAVED_NOTES, data)

        }
    }


    private val viewModel: PreviewViewModel by lazy {
        ViewModelProvider(
            this,
            (requireActivity().applicationContext as NotesApplication).previewNoteFactory
        )[PreviewViewModel::class.java]
    }
    private lateinit var binding: FragmentNotePreviewBinding
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

        binding = FragmentNotePreviewBinding.inflate(layoutInflater, container, false)
        return binding.root
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




    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        viewModel.notes = (bundle.getSerializable(Keys.SAVED_NOTES)!! as Note)
        viewModel.filenames = viewModel.notes.attachments

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
            resources.getString(
                R.string.createdtime,
                DateUtil.getDateAndTime(viewModel.notes.createdTime)
            )
        if (viewModel.notes.modifiedTime.toInt() != viewModel.notes.createdTime.toInt()) {
            binding.updatedTime.text = resources.getString(
                R.string.updatedTime,
                DateUtil.getDateAndTime(viewModel.notes.modifiedTime)
            )
            binding.updatedTime.visibility = View.VISIBLE
        } else {
            binding.updatedTime.visibility = View.GONE
        }
        binding.inputNoteTitle.text = viewModel.notes.title
        binding.inputNoteSubtitle.text = viewModel.notes.subtitle
        binding.inputNote.text = viewModel.notes.noteText
        binding.preview.setBackgroundColor(viewModel.notes.color.color.toColor())
        if (viewModel.notes.weblink.isNotEmpty()) {
            displayUrl(viewModel.notes.weblink)
            binding.rvUrl.visibility = View.VISIBLE
        }
        setFavoriteChoice(viewModel.notes.favorite)
        setPriorityToView(viewModel.notes.priority)




    }

    private fun setPriorityToView(priority: Priority) {

        when (priority) {
            Priority.LOW ->
                binding.apply {
                    resetPriorityChoiceSelected()
                    green.setImageResource(R.drawable.ic_done) }
            Priority.IMPORTANT ->
                binding.apply {
                    resetPriorityChoiceSelected()
                    red.setImageResource(R.drawable.ic_done)
                }


            Priority.MEDIUM -> {
                binding.apply {
                    resetPriorityChoiceSelected()
                    yellow.setImageResource(R.drawable.ic_done)
                }
            }

        }

    }

    private fun setFavoriteChoice(favorite: Boolean) {
        binding.like.isChecked = favorite
    }


    private fun resetPriorityChoiceSelected() {
        binding.apply {
            red.setImageResource(0)
            yellow.setImageResource(0)
            green.setImageResource(0)

        }
    }

    private fun triggerAttachmentList() {
        if (viewModel.filenames.isNotEmpty()) {
            displayAttachment(viewModel.notes.attachments)
        }
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
        val fragment = CreateNoteFragment.newInstance(viewModel.notes, MenuActions.NOACTION)
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
        toolbar.setup(requireActivity(), Constants.PREVIEW_FRAGMENT)

    }


    private fun displayAttachment(list: List<Attachment>) {
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

        fragmentNavigationLisenter?.navigate(
            AttachmentPreviewFragment.newInstance(name),
            BackStack.ATTACHMENT_PREVIEW
        )
    }

    override fun onDelete(name: String, position: Int) {

    }

    override fun removeUrl(string: String) {

    }


}