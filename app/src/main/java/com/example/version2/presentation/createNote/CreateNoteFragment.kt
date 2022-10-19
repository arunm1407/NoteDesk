package com.example.version2.presentation.createNote

import com.example.version2.presentation.homeScreen.HomeFragment
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.version2.presentation.util.date.DateUtil
import com.example.version2.R
import com.example.version2.databinding.FragmentCreateNoteBinding
import com.example.version2.domain.model.Attachment
import com.example.version2.domain.model.Color.Companion.COLOR_1
import com.example.version2.domain.model.Color.Companion.COLOR_10
import com.example.version2.domain.model.Color.Companion.COLOR_2
import com.example.version2.domain.model.Color.Companion.COLOR_3
import com.example.version2.domain.model.Color.Companion.COLOR_4
import com.example.version2.domain.model.Color.Companion.COLOR_5
import com.example.version2.domain.model.Color.Companion.COLOR_6
import com.example.version2.domain.model.Color.Companion.COLOR_7
import com.example.version2.domain.model.Color.Companion.COLOR_8
import com.example.version2.domain.model.Color.Companion.COLOR_9
import com.example.version2.domain.model.Color.Companion.getValues
import com.example.version2.domain.model.Note
import com.example.version2.domain.model.Priority
import com.example.version2.presentation.attachmentPreview.AttachmentPreviewFragment
import com.example.version2.presentation.attachmentPreview.adaptor.AttachmentAdaptor
import com.example.version2.presentation.attachmentPreview.listener.AttachmentLisenter
import com.example.version2.presentation.common.NoteScreen
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.createNote.adaptor.PriorityAdaptor
import com.example.version2.presentation.createNote.adaptor.UrlAdaptor
import com.example.version2.presentation.createNote.dialog.AddImageDialog
import com.example.version2.presentation.createNote.dialog.CameraSettingsDialog
import com.example.version2.presentation.createNote.dialog.StorageSettings
import com.example.version2.presentation.createNote.enums.AddImage
import com.example.version2.presentation.createNote.enums.ExitSettingsAction
import com.example.version2.presentation.createNote.listener.DialogLisenter
import com.example.version2.presentation.createNote.listener.ExitDailogLisenter
import com.example.version2.presentation.createNote.listener.UrlListener
import com.example.version2.presentation.homeScreen.enums.MenuActions
import com.example.version2.presentation.homeScreen.listener.FragmentNavigationLisenter
import com.example.version2.presentation.homeScreen.listener.SettingsLisenter
import com.example.version2.presentation.util.*
import com.example.version2.presentation.util.keys.Constants
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.storage.Storage
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class CreateNoteFragment : Fragment(), DialogLisenter, ExitDailogLisenter,
    UrlListener,
    AttachmentLisenter {


    companion object {
        fun newInstance(data: Note?, res: MenuActions) =
            CreateNoteFragment().withArgs {
                putSerializable(Keys.MENU_ACTION, res)
                putSerializable(Keys.SAVED_NOTES, data)

            }
    }


    private lateinit var binding: FragmentCreateNoteBinding
    private val viewModel: CreateNoteViewModel by lazy {
        ViewModelProvider(
            this,
            (requireActivity().applicationContext as NotesApplication).createNoteFactory
        )[CreateNoteViewModel::class.java]
    }
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null
    private var settingsLisenter: SettingsLisenter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentNavigationLisenter) {
            fragmentNavigationLisenter = context
        }
        if (context is SettingsLisenter) {
            settingsLisenter = context
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

        binding = FragmentCreateNoteBinding.inflate(layoutInflater, container, false)
        fetchData()
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialization()
        eventHandler()
    }


    override fun onStop() {
        viewModel.setMenuAction(MenuActions.NOACTION)
        super.onStop()

    }


    override fun onDestroy() {
        super.onDestroy()
        fragmentNavigationLisenter = null
        settingsLisenter = null


    }


    private fun getArgumentParcelable() {

        val bundle: Bundle = requireArguments()
        if (viewModel.action == null) {
            viewModel.setMenuAction(bundle[Keys.MENU_ACTION] as MenuActions)
        }
        viewModel.notes = (bundle.getSerializable(Keys.SAVED_NOTES) as Note?) ?: Note(
            "", "", 0, 0, "",
            com.example.version2.domain.model.Color.Color1
        )


        if (validateIsEdit(viewModel.notes)) {
            lifecycleScope.launch()
            {
                withContext(Dispatchers.IO)
                {

                    if (viewModel.fileName.value!!.isEmpty()) {

                        withContext(Dispatchers.Main)
                        {
                            viewModel.updateFileNameList(viewModel.notes.attachments)
                        }


                    }


                }

                viewModel.setSelectedNoteColor(viewModel.notes.color.color)
            }
        }

        viewModel.setIsEdit(validateIsEdit(viewModel.notes))

    }


    private fun setupCustomSpinner() {
        val priorityList= mutableListOf(
            com.example.version2.presentation.model.Priority(Keys.LOW, R.drawable.priority_green),
            com.example.version2.presentation.model.Priority(
                Keys.MEDIUM,
                R.drawable.priority_yellow
            ),
            com.example.version2.presentation.model.Priority(Keys.HIGH, R.drawable.priority_red)
        )

        val adaptor = PriorityAdaptor(requireActivity(), priorityList)
        binding.dropdown.adapter = adaptor
        binding.dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {


                when (position) {
                    0 -> viewModel.setPriority(Priority.LOW)
                    1 -> viewModel.setPriority(Priority.MEDIUM)
                    2 -> viewModel.setPriority(Priority.IMPORTANT)

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


    }


    private fun fetchData() {
        viewModel.setUserId((requireActivity() as NoteScreen).getUserID())
    }

    private fun initializeToolBar() {
        val toolbar: androidx.appcompat.widget.Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.setup(requireActivity(), Constants.CREATE_NOTE_FRAGMENT_TITLE)


    }

    private fun restoreDataToView() {

        binding.otherOptions.visibility = View.VISIBLE
        viewModel.notes.apply {
            retrievedPriorityView()
            binding.inputNoteTitle.setText(title)
            binding.inputNoteSubtitle.setText(subtitle)
            binding.inputNote.setText(noteText)
            viewModel.setPriority(priority)
            viewModel.setSelectedNoteColor(color.color)
            binding.like.isChecked = favorite
            viewModel.updateUrlList(viewModel.notes.weblink)

        }

    }


    private fun retrievedPriorityView() {
        viewModel.setPriority(viewModel.notes.priority)
        when (viewModel.notes.priority) {
            Priority.LOW -> {
                binding.dropdown.setSelection(0)
            }

            Priority.MEDIUM -> {
                binding.dropdown.setSelection(1)

            }
            Priority.IMPORTANT -> {
                binding.dropdown.setSelection(2)
            }


        }
    }


    private fun eventHandler() {
        if (viewModel.isEdit) {
            restoreDataToView()
            otherOptionsListener()
            binding.myToolbar.updateTitle(Keys.EDIT_NOTE_TITLE)
        }
        if (viewModel.selectedNoteColor != Keys.SELECTED_NOTED_COLOR) {
            setBackGroundColor()
        }

        if (!viewModel.isEdit) {
            triggerRecyclerView()
        }

        when (viewModel.action) {
            MenuActions.ATTACH -> showDialogFragment(AddImageDialog())
            MenuActions.VOICE -> voiceToText()
            MenuActions.WEBLINK -> showDialogOfUrl()
            else -> {

            }
        }
    }



    private fun setBackGroundColor() {
        binding.create.setBackgroundColor(viewModel.selectedNoteColor.toColor())
    }


    private fun showDialogOfUrl() {
        val dialogBottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.add_url)
            window?.setSoftInputMode(Keys.SOFT_INPUT_ADJUST_RESIZE)
            show()
            behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        dialogBottomSheetDialog.findViewById<TextView>(R.id.btnCancel)?.setOnClickListener()
        {
            view?.hideKeyboard()
            dialogBottomSheetDialog.dismiss()
        }
        dialogBottomSheetDialog.findViewById<TextView>(R.id.btnSubmit)?.setOnClickListener()
        {
            validateUrl(dialogBottomSheetDialog)
        }
    }


    private fun validateUrl(dialogBottomSheetDialog: BottomSheetDialog) {
        val etURL = dialogBottomSheetDialog.findViewById<EditText>(R.id.etUrl)!!
        val url = etURL.getString().trim()
        if (isValidURL(url)) {
            dialogBottomSheetDialog.dismiss()
            if (viewModel.webUrl.value!!.size <= 5) {
                viewModel.addUrl(url)
            }


        } else {
            etURL.error = getString(R.string.invalid_url)
        }


    }

    private fun isValidURL(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()

    }

    private val voiceToText =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {

                val res: ArrayList<String> = result.data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )!!
                binding.inputNote.setText(
                    Objects.requireNonNull(res)[0]
                )
                binding.inputNote.setSelection(binding.inputNote.length())
                binding.inputNote.requestFocus()

            }

        }


    private fun voiceToText() {

        voiceToText.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_to_text))
        })

    }


    private fun triggerRecyclerView() {
        hideRv()
        displayAttachment(viewModel.fileName.value!!.toMutableList())
    }

    private fun displayAttachment(list: List<Attachment>) {
        binding.rvAttachment.initRecyclerView(
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false),
            AttachmentAdaptor(
                list, this,
                isDelete = true,
            ),
            false
        )
        displayRv()


    }

    private fun displayRv() {
        binding.rvAttachmentProgress.visibility = View.GONE
        binding.rvAttachment.visibility = View.VISIBLE
        binding.bottomNavBar.visibility = View.VISIBLE
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


    private fun hideRv() {
        binding.rvAttachmentProgress.visibility = View.VISIBLE
        binding.rvAttachment.visibility = View.GONE
        binding.bottomNavBar.visibility = View.GONE
        val parent = binding.layout1
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)
        constraintSet.connect(
            R.id.updated_time,
            ConstraintSet.TOP,
            R.id.rv_attachment_progress,
            ConstraintSet.BOTTOM
        )
        constraintSet.applyTo(parent)


    }


    private fun showDialogFragment(dialog: DialogFragment) {
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog.activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show(childFragmentManager, "2")

    }


    private fun initialization() {
        initializeToolBar()
        setupCustomSpinner()
        initializeMenu()
        bottomNavigationListener()
        setDateInBottomNavBar()
        observeUrl()
        initAttachmentTitle()
        backPressedListener()
    }


    private fun otherOptionsListener() {
        binding.otherOptions.setOnClickListener()
        {
            miscellaneous()
        }
    }


    private fun initAttachmentTitle() {

        viewModel.fileName.observe(viewLifecycleOwner)
        {

            if (viewModel.fileName.value!!.isNotEmpty()) {
                binding.tvAttachmentTitle.visibility = View.VISIBLE
                binding.tvAttachmentTitle.text =
                    resources.getString(R.string.attachmentCount, viewModel.fileName.value!!.size)
                triggerRecyclerView()


            } else {
                binding.tvAttachmentTitle.visibility = View.GONE

            }
        }


    }


    private fun miscellaneous() {
        val bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.miscellaneous)
            show()
            behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_delete_note)
            ?.setOnClickListener()
            {
                deleteNote()
                bottomSheetDialog.dismiss()
            }
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_share_Notes)
            ?.setOnClickListener()
            {

                shareNotes()
                bottomSheetDialog.dismiss()
            }
        bottomSheetDialog.findViewById<View>(R.id.top_view)?.setOnClickListener()
        {
            bottomSheetDialog.dismiss()
        }
    }


    private fun shareNotes() {

        val content: String =
            binding.inputNoteTitle.getString() + "\n\n" + binding.inputNoteSubtitle.getString() + "\n\n" + binding.inputNote.getString()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = Keys.SHARE_TYPE
            putExtra(
                Intent.EXTRA_SUBJECT,
                binding.inputNoteSubtitle.getString()
            )
            putExtra(Intent.EXTRA_TEXT, content)
        }

        startActivity(Intent.createChooser(shareIntent, Keys.SHARE_TYPE))

    }


    private fun deleteNote() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.delete_dailog_title))
            .setMessage(getString(R.string.delete_dailog_message))
            .setPositiveButton(
                getString(R.string.delte_dailog_positive),
            )

            { _, _ ->
                lifecycleScope.launch()
                {
                    withContext(Dispatchers.IO)
                    {
                        viewModel.notes.attachments.map {
                            it.name
                        }.forEach {
                            Storage.deletePhotoFromInternalStorage(it, requireContext())
                        }

                        viewModel.deleteNote(
                            viewModel.notes.id,
                            viewModel.userId
                        )
                    }

                    view?.hideKeyboard()
                    fragmentNavigationLisenter?.navigate(HomeFragment(), BackStack.HOME)
                }
            }
            .setNegativeButton(getString(R.string.delte_dailog_pnegative), null)
        builder.create().apply {
            show()
            getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
        }

    }


    private fun displayUrl(list: List<String>) {
        binding.rvUrl.initRecyclerView(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            ), UrlAdaptor(list, this, true)
        )

    }


    private fun observeUrl() {
        viewModel.webUrl.observe(viewLifecycleOwner)
        {

            if (viewModel.webUrl.value!!.isNotEmpty() && viewModel.webUrl.value!!.size <= 6) {
                binding.rvUrl.visibility = View.VISIBLE
                displayUrl(viewModel.webUrl.value!!)
            } else if (viewModel.webUrl.value!!.size > 5) {
                maximumLimitExceed()
            } else {
                binding.rvUrl.visibility = View.GONE
            }
        }
    }


    private fun maximumLimitExceed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle(getString(R.string.url_dailog_title))
                .setMessage(getString(R.string.url_daiolog_message))
                .setPositiveButton(
                    getString(R.string.url_dailog_ok), null
                )
        }

        builder.create().apply {
            show()
            getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))

        }


    }


    private fun setDateInBottomNavBar() {
        binding.createdTime.text = resources.getString(
            R.string.createdtime,
            DateUtil.getDateAndTime(Calendar.getInstance().timeInMillis)
        )
        binding.updatedTime.visibility = View.GONE
        if (viewModel.isEdit) {

            if ((viewModel.notes.modifiedTime).toInt() == 0) binding.updatedTime.text =
                resources.getString(
                    R.string.updatedTime,
                    DateUtil.getDateAndTime(Calendar.getInstance().timeInMillis)
                )
            else binding.updatedTime.text =
                resources.getString(
                    R.string.updatedTime,
                    DateUtil.getDateAndTime(viewModel.notes.modifiedTime)
                )
            binding.updatedTime.visibility = View.VISIBLE

            binding.createdTime.text = resources.getString(
                R.string.createdtime,
                DateUtil.getDateAndTime(viewModel.notes.createdTime)
            )
        }
    }

    private fun bottomNavigationListener() {
        binding.createNoteAddActions.setOnClickListener()
        {
            showActionMenu()
        }
        binding.pickAColor.setOnClickListener()
        {
            pickColor()
        }
    }


    private fun restoreColorToView(color: String, bottomSheetDialog: BottomSheetDialog) {
        removeAllSelectedColors(bottomSheetDialog)
        when (color) {
            COLOR_1 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color1)
                ?.setImageResource(R.drawable.ic_done)
            COLOR_2 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color2)
                ?.setImageResource(R.drawable.ic_done)
            COLOR_3 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color3)
                ?.setImageResource(R.drawable.ic_done)
            COLOR_4 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color4)
                ?.setImageResource(R.drawable.ic_done)
            COLOR_5 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color5)
                ?.setImageResource(R.drawable.ic_done)
            COLOR_6 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color6)
                ?.setImageResource(R.drawable.ic_done)
            COLOR_7 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color7)
                ?.setImageResource(R.drawable.ic_done)
            COLOR_8 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color8)
                ?.setImageResource(R.drawable.ic_done)
            COLOR_9 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color9)
                ?.setImageResource(R.drawable.ic_done)
            COLOR_10 -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color10)
                ?.setImageResource(R.drawable.ic_done)
            else -> bottomSheetDialog.findViewById<ImageView>(R.id.check_color1)
                ?.setImageResource(R.drawable.ic_done)

        }
    }


    private fun removeAllSelectedColors(bottomSheetDialog: BottomSheetDialog) {
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color1)?.setImageResource(0)
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color2)?.setImageResource(0)
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color3)?.setImageResource(0)
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color4)?.setImageResource(0)
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color5)?.setImageResource(0)
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color6)?.setImageResource(0)
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color7)?.setImageResource(0)
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color8)?.setImageResource(0)
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color9)?.setImageResource(0)
        bottomSheetDialog.findViewById<ImageView>(R.id.check_color10)?.setImageResource(0)
    }


    private fun pickColor() {
        val bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.pick_color)
            show()
            behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

        }

        restoreColorToView(viewModel.selectedNoteColor, bottomSheetDialog)
        val imageColor1 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color1)!!
        val imageColor2 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color2)!!
        val imageColor3 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color3)!!
        val imageColor4 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color4)!!
        val imageColor5 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color5)!!
        val imageColor6 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color6)!!
        val imageColor7 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color7)!!
        val imageColor8 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color8)!!
        val imageColor9 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color9)!!
        val imageColor10 = bottomSheetDialog.findViewById<ImageView>(R.id.check_color10)!!

        imageColor1.setOnClickListener {

            applyChangesForSelectedColor(imageColor1, COLOR_1, bottomSheetDialog)

        }

        imageColor2.setOnClickListener {

            applyChangesForSelectedColor(imageColor2, COLOR_2, bottomSheetDialog)

        }
        imageColor3.setOnClickListener {

            applyChangesForSelectedColor(imageColor3, COLOR_3, bottomSheetDialog)

        }
        imageColor4.setOnClickListener {

            applyChangesForSelectedColor(imageColor4, COLOR_4, bottomSheetDialog)

        }
        imageColor5.setOnClickListener {

            applyChangesForSelectedColor(imageColor5, COLOR_5, bottomSheetDialog)

        }
        imageColor6.setOnClickListener {

            applyChangesForSelectedColor(imageColor6, COLOR_6, bottomSheetDialog)

        }
        imageColor7.setOnClickListener {

            applyChangesForSelectedColor(imageColor7, COLOR_7, bottomSheetDialog)

        }
        imageColor8.setOnClickListener {

            applyChangesForSelectedColor(imageColor8, COLOR_8, bottomSheetDialog)

        }
        imageColor9.setOnClickListener {
            applyChangesForSelectedColor(imageColor9, COLOR_9, bottomSheetDialog)
        }
        imageColor10.setOnClickListener {
            applyChangesForSelectedColor(imageColor10, COLOR_10, bottomSheetDialog)
        }


    }

    private fun applyChangesForSelectedColor(
        imageView: ImageView,
        color: String,
        bottomSheetDialog: BottomSheetDialog
    ) {


        viewModel.setSelectedNoteColor(color)
        removeAllSelectedColors(bottomSheetDialog)
        imageView.setImageResource(R.drawable.ic_done)
        setBackGroundColor()
    }


    private fun showActionMenu() {
        val bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.action)
            show()
            behavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_take_photo)
            ?.setOnClickListener()
            {

                if (viewModel.fileName.value!!.size <= 5) {
                    takePhoto()
                    bottomSheetDialog.dismiss()
                } else {
                    maximumCountExceed()
                    bottomSheetDialog.dismiss()
                }


            }
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_add_image)
            ?.setOnClickListener()
            {
                if (viewModel.fileName.value!!.size <= 5) {
                    chooseImage()
                    bottomSheetDialog.dismiss()
                } else {
                    maximumCountExceed()
                    bottomSheetDialog.dismiss()
                }


            }
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_voice_note)
            ?.setOnClickListener()
            {


                voiceToText()
                bottomSheetDialog.dismiss()
            }
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_add_url)?.setOnClickListener()
        {

            if (viewModel.webUrl.value?.size!! <= 5) {
                showDialogOfUrl()
                bottomSheetDialog.dismiss()
            } else {
                maximumLimitExceed()
            }
            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.findViewById<View>(R.id.top_view)?.setOnClickListener()
        {
            bottomSheetDialog.dismiss()
        }

    }

    private fun maximumCountExceed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.maximum_limit))
            .setMessage(getString(R.string.count_message))
            .setPositiveButton(
                getString(R.string.url_dailog_ok), null
            )
        builder.create().apply {
            show()
            getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
        }
    }


    private fun initializeMenu() {

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                if (viewModel.isEdit) menuInflater.inflate(R.menu.edit, menu)
                else menuInflater.inflate(R.menu.menu_create, menu)


            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.menu_done -> {
                        createNotes()
                        true
                    }


                    R.id.menu_delete -> {
                        deleteNote()
                        true
                    }
                    else -> false


                }

            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }


    private fun validateIsEdit(notes: Note): Boolean {
        return notes.title.trim().isNotEmpty()
    }


    fun displayExitDialog() {
        if (binding.inputNoteTitle.checkEmpty() && binding.inputNoteSubtitle.checkEmpty() && binding.inputNote.checkEmpty() && viewModel.fileName.value!!.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.blank_note_discarded),
                Toast.LENGTH_SHORT
            ).show()
            parentFragmentManager.popBackStack()


        } else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(getString(R.string.are_you_sure_Title))
                .setMessage(getString(R.string.without_saving_subtitle))
                .setPositiveButton(
                    getString(R.string.positive)

                ) { _, _ ->

                    parentFragmentManager.popBackStack()

                }
                .setNegativeButton(getString(R.string.negative), null)
            builder.create().apply {
                show()
                getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
                getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))

            }
        }


    }


    private fun backPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.hideKeyboard()
                    displayExitDialog()

                }
            })
    }

    override fun onAttachmentClicked(name: String) {
        view?.hideKeyboard()
        fragmentNavigationLisenter!!.navigate(
            AttachmentPreviewFragment.newInstance(name),
            BackStack.ATTACHMENT_PREVIEW
        )
    }

    override fun onDelete(name: String, position: Int) {
        viewModel.removeFileName(Attachment(name, 0))

    }


    override fun choice(choice: AddImage) {
        when (choice) {
            AddImage.TAKE_PHOTO -> {
                takePhoto()
            }
            AddImage.CHOOSE_PHOTO -> {
                chooseImage()
            }
            else -> {}
        }
    }

    override fun onClickYes(action: ExitSettingsAction) {
        when (action) {
            ExitSettingsAction.CAMERA, ExitSettingsAction.STORAGE -> {
                view?.hideKeyboard()
                settingsLisenter?.settings()
            }
            ExitSettingsAction.NO_ACTION -> {
            }
        }
    }

    override fun removeUrl(string: String) {
        viewModel.removeUrl(string)

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
            else showDialogFragment(CameraSettingsDialog())
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
                    viewModel.addFileName(Attachment("$filename.jpg", 0))
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


    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            val filename = UUID.randomUUID().toString()

            val isSavedSuccessfully =
                it?.let { it1 ->
                    Storage.savePhotoToInternalStorage(filename, it1, requireActivity())
                }

            if (isSavedSuccessfully == true) {
                viewModel.addFileName(Attachment("$filename.jpg", 0))
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


    private fun createNotes() {
        view?.hideKeyboard()
        if (viewModel.isEdit) {
            val note: Note



            binding.apply {
                note = Note(
                    title = inputNoteTitle.getString(),
                    subtitle = inputNoteSubtitle.getString(),
                    noteText = inputNote.getString(),
                    priority = viewModel.priority,
                    favorite = binding.like.isChecked,
                    weblink = viewModel.webUrl.value as ArrayList<String>,
                    color = viewModel.selectedNoteColor.getValues(),
                    modifiedTime = Calendar.getInstance().timeInMillis,
                    createdTime = viewModel.notes.createdTime,
                    attachments = viewModel.fileName.value!!.toMutableList() as ArrayList<Attachment> /* = java.util.ArrayList<com.example.version2.domain.model.Attachment> */,
                    id = viewModel.notes.id
                )


            }

            if (validateNotes(note)) {
                val dialog = setProgressDialog()
                viewModel.updateNote(note, viewModel.userId)
                lifecycleScope.launch(Dispatchers.IO)
                {

                    withContext(Dispatchers.Main) {
                        delay(500)
                        dialog.dismiss()
                        toastMessage(getString(R.string.saved_succes))
                        navigateHome()
                    }

                }
            }


        } else {

            lifecycleScope.launch(Dispatchers.IO)
            {

                withContext(Dispatchers.Main)
                {

                    val notes = saveNotes()
                    if (validateNotes(notes)) {
                        val dialog = setProgressDialog()
                        withContext(Dispatchers.IO)
                        {
                            viewModel.addNotes(notes, viewModel.userId)
                        }
                        toastMessage(getString(R.string.note_saved))
                        dialog.dismiss()
                        navigateHome()
                    }


                }


            }


        }


    }


    private fun toastMessage(string: String) {
        Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT)
            .show()
    }

    private fun navigateHome() {

        fragmentNavigationLisenter?.navigate(
            HomeFragment(),
            BackStack.HOME
        )
    }

    private fun saveNotes(): Note {
        viewModel.notes = Note(
            binding.inputNoteTitle.getString(),
            binding.inputNoteSubtitle.getString(),
            Calendar.getInstance().timeInMillis,
            Calendar.getInstance().timeInMillis,
            binding.inputNote.getString(),
            color = viewModel.selectedNoteColor.getValues(),
            weblink = viewModel.webUrl.value!!.toMutableList() as ArrayList<String>,
            priority = viewModel.priority,
            favorite = binding.like.isChecked
        )
        viewModel.fileName.value?.forEach {
            viewModel.notes.attachments.add(it)
        }

        return viewModel.notes

    }


    private fun validateNotes(notes: Note): Boolean {

        return if (notes.title.trim().isNotEmpty() && notes.noteText.trim().isNotEmpty()) {
            true
        } else if (notes.title.trim().isEmpty() && notes.noteText.trim().isNotEmpty()) {
            binding.inputNoteTitle.error = getString(R.string.title_compulsory)
            binding.inputNoteTitle.error = getString(R.string.title_compulsory)
            false

        } else if (notes.title.trim().isNotEmpty() && notes.noteText.trim().isEmpty()) {
            binding.inputNote.error = getString(R.string.description_compuslory)
            false
        } else {
            binding.inputNoteTitle.error = getString(R.string.title_compulsory)
            binding.inputNote.error = getString(R.string.description_compuslory)
            false
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
            text = context.getString(R.string.loading1)
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



}



