package com.example.notedesk.presentation.createNote

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.speech.RecognizerIntent
import android.util.Patterns
import android.view.*
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notedesk.data.data_source.FileName
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.domain.util.date.DateUtil.getDateAndTime
import com.example.notedesk.domain.util.keys.Constants
import com.example.notedesk.domain.util.keys.Keys
import com.example.notedesk.domain.util.keys.Keys.COLOR_1
import com.example.notedesk.domain.util.keys.Keys.COLOR_10
import com.example.notedesk.domain.util.keys.Keys.COLOR_2
import com.example.notedesk.domain.util.keys.Keys.COLOR_3
import com.example.notedesk.domain.util.keys.Keys.COLOR_4
import com.example.notedesk.domain.util.keys.Keys.COLOR_5
import com.example.notedesk.domain.util.keys.Keys.COLOR_6
import com.example.notedesk.domain.util.keys.Keys.COLOR_7
import com.example.notedesk.domain.util.keys.Keys.COLOR_8
import com.example.notedesk.domain.util.keys.Keys.COLOR_9
import com.example.notedesk.domain.util.keys.Keys.REQUEST_CODE_SPEECH_INPUT
import com.example.notedesk.domain.util.storage.Storage
import com.example.notedesk.presentation.attachmentPreview.AttachmentPerviewFragment
import com.example.notedesk.presentation.attachmentPreview.adaptor.AttachmentAdaptor
import com.example.notedesk.presentation.attachmentPreview.listener.AttachmentLisenter
import com.example.notedesk.presentation.createNote.adaptor.PriorityAdaptor
import com.example.notedesk.presentation.createNote.adaptor.UrlAdaptor
import com.example.notedesk.presentation.createNote.dailog.AddImageDailog
import com.example.notedesk.presentation.createNote.dailog.CameraSettingDailog
import com.example.notedesk.presentation.createNote.dailog.StorageSettings
import com.example.notedesk.presentation.createNote.enums.AddImage
import com.example.notedesk.presentation.createNote.enums.ExitSettingsAction
import com.example.notedesk.presentation.createNote.listener.DialogLisenter
import com.example.notedesk.presentation.createNote.listener.ExitDailogLisenter
import com.example.notedesk.presentation.createNote.listener.UrlListener
import com.example.notedesk.presentation.home.HomeFragment
import com.example.notedesk.presentation.home.Listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.home.Listener.SettingsLisenter
import com.example.notedesk.presentation.home.enums.MenuActions
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.presentation.util.PriorityList
import com.example.notedesk.presentation.util.hideKeyboard
import com.example.notedesk.presentation.util.initRecyclerView
import com.example.notesappfragment.R
import com.example.notesappfragment.databinding.FragmentCreateNotesBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
//import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class CreateNotesFragment : Fragment(), DialogLisenter, ExitDailogLisenter,
    UrlListener,
    AttachmentLisenter {


    companion object {


        fun newInstance(data: Notes?, res: MenuActions) =
            CreateNotesFragment().apply {
                val bundle = Bundle()
                bundle.putSerializable(Keys.MENU_ACTION, res)
                bundle.putParcelable(Keys.SAVED_NOTES, data)
                arguments = bundle
            }
    }


    private lateinit var binding: FragmentCreateNotesBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var dialogBottomSheetDialog: BottomSheetDialog
    private val viewModel: CreateNoteViewModel by viewModels()
    private lateinit var fragmentNavigationLisenter: FragmentNavigationLisenter
    private lateinit var settingsLisenter: SettingsLisenter


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


    private fun getArgumentParcelable() {

        val bundle: Bundle = requireArguments()
        if (viewModel.action == null) {
            viewModel.action = bundle[Keys.MENU_ACTION] as MenuActions
        }

        viewModel.notes = (bundle.getParcelable(Keys.SAVED_NOTES) as Notes?) ?: Notes()
        if (viewModel.notes.title.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO)
            {

                viewModel.originalList =
                    viewModel.getFileName(viewModel.notes.id).toMutableList()
                withContext(Dispatchers.Main)
                {
                    if (viewModel.fileName.value!!.isEmpty()) {
                        viewModel.fileName.value = viewModel.originalList.toMutableList()
                    }


                }

                viewModel.selectedNoteColor = viewModel.notes.color
            }
        }

        viewModel.isEdit = viewModel.notes.title.isNotEmpty()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateNotesBinding.inflate(layoutInflater, container, false)
        setupCustomSpinner()
        initializeMenu()
        return binding.root

    }


    private fun setupCustomSpinner() {
        val adaptor = PriorityAdaptor(requireActivity(), PriorityList.priorityList)
        binding.dropdown.adapter = adaptor
        binding.dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> viewModel.priority = Keys.GREEN
                    1 -> viewModel.priority = Keys.YELLOW
                    2 -> viewModel.priority = Keys.RED

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


    }


    private fun initializeMenu() {

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_create, menu)

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.menu_done -> {

                        createNotes()


                        true
                    }
                    else -> false
                }

            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeToolBar()
        if (viewModel.isEdit) {

            restoreDataToView()
            otherOptionsListener()
//            binding.myToolbar.title = Keys.EDIT_NOTE_TITLE
        }
        if (viewModel.selectedNoteColor != Keys.SELECTED_NOTED_COLOR) {
            setSubtitleIndicatorColor()
        }
        initAttachmentTitle()
        if (!viewModel.isEdit) {
            triggerRecyclerView()
        }

        bottomNavigationListener()
        setDateInBottomNavBar()
        observeUrl()
        backPressed()

        when (viewModel.action) {
            MenuActions.ATTACH -> showDialogFragment(AddImageDailog())
            MenuActions.VOICE -> voiceToText()
            MenuActions.WEBLINK -> showDialogOfUrl()
            else -> {

            }
        }

    }


    private fun initializeToolBar() {
        val toolbar: androidx.appcompat.widget.Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.title = Constants.CREATE_NOTE_FRAGMENT_TITLE
        (activity as AppCompatActivity).apply {
            this.setSupportActionBar(toolbar)
            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_arrow_back_24)

    }


    private fun otherOptionsListener() {
        binding.otherOptions.setOnClickListener()
        {
            miscellaneous()
        }
    }

    private fun restoreDataToView() {

        binding.otherOptions.visibility = View.VISIBLE

        viewModel.notes.apply {
            retrievedPriorityView()
            binding.inputNoteTitle.setText(title)
            binding.inputNoteSubtitle.setText(subtitle)
            binding.inputNote.setText(noteText)
            viewModel.priority = priority
            viewModel.selectedNoteColor = color
            binding.like.isChecked = favorite
            viewModel.webUrl.value = viewModel.notes.weblink
        }

    }


    private fun setSubtitleIndicatorColor() {
        binding.create.setBackgroundColor(Color.parseColor(viewModel.selectedNoteColor))
    }


    private fun showActionMenu() {
        initializeBottomSheet(R.layout.action)
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_take_photo)
            ?.setOnClickListener()
            {

                if (viewModel.fileName.value!!.size <= 5) {
                    takePhoto()
                    dismissBottomSheet()
                } else {
                    maximumCountExceed()
                    dismissBottomSheet()
                }


            }
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_add_image)
            ?.setOnClickListener()
            {
                if (viewModel.fileName.value!!.size <= 5) {
                    chooseImage()
                    dismissBottomSheet()
                } else {
                    maximumCountExceed()
                    dismissBottomSheet()
                }


            }
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_voice_note)
            ?.setOnClickListener()
            {


                voiceToText()
                dismissBottomSheet()
            }
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_add_url)?.setOnClickListener()
        {

            if (viewModel.webUrl.value?.size!! <= 5) {
                showDialogOfUrl()
                dismissBottomSheet()
            } else {
                maximumLimitExceed()
            }
            dismissBottomSheet()

        }
        bottomSheetDialog.findViewById<View>(R.id.top_view)?.setOnClickListener()
        {
            dismissBottomSheet()
        }

    }

    private fun maximumCountExceed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Maximum Limit Exceeds... ")
            .setMessage("Sorry, Pls try to remove attachment & Try again  ")
            .setPositiveButton(
                "Ok", null
            )
        val alert: AlertDialog = builder.create()
        alert.show()
        alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
        alert.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))


    }


    private fun backPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.hideKeyboard()
                    displayExitDialog()

                }
            })
    }


    private fun showDialogFragment(dialog: DialogFragment) {
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog.activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show(childFragmentManager, "2")


    }


    @SuppressLint("SetTextI18n")
    private fun initAttachmentTitle() {


        viewModel.fileName.observe(viewLifecycleOwner)
        {
            viewModel.tempList = viewModel.fileName.value as MutableList<String>

            if (viewModel.tempList.isNotEmpty()) {
                binding.tvAttachmentTitle.visibility = View.VISIBLE
                binding.tvAttachmentTitle.text = "Attachment (${viewModel.tempList.size})"
                triggerRecyclerView()


            } else {
                binding.tvAttachmentTitle.visibility = View.GONE

            }
        }


    }


    private fun triggerRecyclerView() {
        hideRv()
        lifecycleScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                displayAttachment(viewModel.tempList)
            }
        }

    }


    private fun checkAndRequestCameraPermissions(): Boolean {

        val galleryPermission: Int =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        return if (galleryPermission == PackageManager.PERMISSION_GRANTED) {
            true
        } else {

            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                20
            )
            false
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 20) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) takePhoto.launch()
            else showDialogFragment(CameraSettingDailog())
        } else if (requestCode == 30) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) selectImageFromGallery()
            else showDialogFragment(StorageSettings())
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
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                30
            )
            false
        }


    }


    private fun voiceToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_to_text))
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
    }


    private fun createNotes() {

        if (viewModel.isEdit) {

            viewModel.notes.let { notes ->

                binding.apply {
                    notes.title = inputNoteTitle.text.toString()
                    notes.subtitle = inputNoteSubtitle.text.toString()
                    notes.noteText = inputNote.text.toString()
                    notes.priority = viewModel.priority
                    notes.favorite = binding.like.isChecked
                    notes.weblink = viewModel.webUrl.value as ArrayList<String>
                    notes.color = viewModel.selectedNoteColor
                    notes.modifiedTime = Calendar.getInstance().timeInMillis
                    notes.attachmentCount = viewModel.tempList.size
                }


                if (notes.title.isNotEmpty() && notes.noteText.isNotEmpty()) {
                    notes.let { viewModel.updateNote(it) }
                    lifecycleScope.launch(Dispatchers.IO)
                    {
                        applyChanges()
                        withContext(Dispatchers.Main) {
                            toastMessage(getString(R.string.saved_succes))
                            view?.hideKeyboard()
                            fragmentNavigationLisenter.navigate(
                                HomeFragment(),
                                BackStack.HOME
                            )
                        }

                    }
                } else if (notes.title.isEmpty() && notes.noteText.isNotEmpty()) {
                    toastMessage(getString(R.string.title_empty))
//                    showAlertColoured()

                } else if (notes.title.isNotEmpty() && notes.noteText.isEmpty()) {
                    toastMessage(getString(R.string.des_empty))
                } else {
                    toastMessage(getString(R.string.both_title_subtitle_empty))
                }


            }


        } else {
            var noteId: Int
            lifecycleScope.launch(Dispatchers.IO)
            {

                withContext(Dispatchers.Main)
                {

                    val notes = saveNotes()
                    if (notes.title.isNotEmpty() && notes.noteText.isNotEmpty()) {
                        withContext(Dispatchers.IO)
                        {
                            noteId = viewModel.addNotes(notes)
                        }
                        viewModel.tempList.forEach {
                            viewModel.addListFileName(FileName(it, noteId))
                        }
                        toastMessage(getString(R.string.note_saved))
                        view?.hideKeyboard()
                        fragmentNavigationLisenter.navigate(
                            HomeFragment(),
                            BackStack.HOME
                        )

                    } else if (notes.title.isEmpty() && notes.noteText.isNotEmpty()) {
                        toastMessage(getString(R.string.title_empty))
                        withContext(Dispatchers.Main)
                        {
//                            showAlertColoured()
                        }

                    } else if (notes.title.isNotEmpty() && notes.noteText.isEmpty()) {
                        toastMessage(getString(R.string.des_empty))
                        withContext(Dispatchers.Main)
                        {
//                            showAlertColoured()
                        }

                    } else {
                        toastMessage(getString(R.string.both_title_subtitle_empty))
                        withContext(Dispatchers.Main)
                        {
//                            showAlertColoured()
                        }

                    }


                }


            }

        }


    }


    private fun saveNotes(): Notes {
        val title = binding.inputNoteTitle.text.toString()
        val subtitle = binding.inputNoteSubtitle.text.toString()
        val noteText = binding.inputNote.text.toString()
        val createdDate = Calendar.getInstance().timeInMillis

        viewModel.notes = Notes(
            title,
            subtitle,
            createdDate,
            modifiedTime = createdDate,
            color = viewModel.selectedNoteColor,
            weblink = viewModel.webUrl.value as ArrayList<String>,
            noteText = noteText
        )
        viewModel.notes.priority = viewModel.priority
        viewModel.notes.favorite = binding.like.isChecked
        viewModel.notes.attachmentCount = viewModel.tempList.size
        return viewModel.notes

    }


    private fun shareNotes() {

        val content: String = binding.inputNoteTitle.text.toString().trim() + "\n\n" +
                binding.inputNoteSubtitle.text.toString().trim() + "\n\n" +
                binding.inputNote.text.toString().trim()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = Keys.SHARE_TYPE
        shareIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            binding.inputNoteSubtitle.text.toString().trim()
        )
        shareIntent.putExtra(Intent.EXTRA_TEXT, content)
        startActivity(Intent.createChooser(shareIntent, Keys.SHARE_TYPE))

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            3 -> {
                if (resultCode == RESULT_OK) {
                    val result: ArrayList<String> = data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )!!
                    binding.inputNote.setText(
                        Objects.requireNonNull(result)[0]
                    )
                    binding.inputNote.setSelection(binding.inputNote.length())
                    binding.inputNote.requestFocus()
                }

            }
        }
    }


    private fun miscellaneous() {
        initializeBottomSheet(R.layout.layout_miscellaneous)
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_delete_note)
            ?.setOnClickListener()
            {
                deleteNote()
                dismissBottomSheet()
            }
        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.layout_share_Notes)
            ?.setOnClickListener()
            {
                shareNotes()
                dismissBottomSheet()
            }
        bottomSheetDialog.findViewById<View>(R.id.top_view)?.setOnClickListener()
        {
            dismissBottomSheet()
        }
    }


    private fun removeAllSelectedColors() {
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

    private fun restoreColorToView(color: String) {
        removeAllSelectedColors()
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


    private fun pickColor() {
        initializeBottomSheet(R.layout.pick_color)
        restoreColorToView(viewModel.selectedNoteColor)
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
            viewModel.selectedNoteColor = COLOR_1
            imageColor1.setImageResource(R.drawable.ic_done)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            imageColor7.setImageResource(0)
            imageColor8.setImageResource(0)
            imageColor9.setImageResource(0)
            imageColor10.setImageResource(0)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
        }

        imageColor2.setOnClickListener {
            viewModel.selectedNoteColor = COLOR_2
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(R.drawable.ic_done)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            imageColor7.setImageResource(0)
            imageColor8.setImageResource(0)
            imageColor9.setImageResource(0)
            imageColor10.setImageResource(0)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
        }
        imageColor3.setOnClickListener {
            viewModel.selectedNoteColor = COLOR_3
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(R.drawable.ic_done)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            imageColor7.setImageResource(0)
            imageColor8.setImageResource(0)
            imageColor9.setImageResource(0)
            imageColor10.setImageResource(0)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
        }
        imageColor4.setOnClickListener {
            viewModel.selectedNoteColor = COLOR_4
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(R.drawable.ic_done)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            imageColor7.setImageResource(0)
            imageColor8.setImageResource(0)
            imageColor9.setImageResource(0)
            imageColor10.setImageResource(0)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
        }
        imageColor5.setOnClickListener {
            viewModel.selectedNoteColor = COLOR_5
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(R.drawable.ic_done)
            imageColor6.setImageResource(0)
            imageColor7.setImageResource(0)
            imageColor8.setImageResource(0)
            imageColor9.setImageResource(0)
            imageColor10.setImageResource(0)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
        }
        imageColor6.setOnClickListener {
            viewModel.selectedNoteColor = COLOR_6
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(R.drawable.ic_done)
            imageColor7.setImageResource(0)
            imageColor8.setImageResource(0)
            imageColor9.setImageResource(0)
            imageColor10.setImageResource(0)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
        }
        imageColor7.setOnClickListener {
            viewModel.selectedNoteColor = COLOR_7
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            imageColor7.setImageResource(R.drawable.ic_done)
            imageColor8.setImageResource(0)
            imageColor9.setImageResource(0)
            imageColor10.setImageResource(0)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
        }
        imageColor8.setOnClickListener {
            viewModel.selectedNoteColor = COLOR_8
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            imageColor7.setImageResource(0)
            imageColor8.setImageResource(R.drawable.ic_done)
            imageColor9.setImageResource(0)
            imageColor10.setImageResource(0)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
        }
        imageColor9.setOnClickListener {
            viewModel.selectedNoteColor = COLOR_9
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            imageColor7.setImageResource(0)
            imageColor8.setImageResource(0)
            imageColor9.setImageResource(R.drawable.ic_done)
            imageColor10.setImageResource(0)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
        }
        imageColor10.setOnClickListener {
            viewModel.selectedNoteColor = COLOR_10
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            imageColor6.setImageResource(0)
            imageColor7.setImageResource(0)
            imageColor8.setImageResource(0)
            imageColor9.setImageResource(0)
            imageColor10.setImageResource(R.drawable.ic_done)
            setSubtitleIndicatorColor()
            applyColorForToolBar()
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


    private fun applyColorForToolBar() {

    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun validateUrl() {
        val etURL = dialogBottomSheetDialog.findViewById<EditText>(R.id.etUrl)!!
        val url = etURL.text.toString().trim()
        if (isValidURL(url)) {
            dialogBottomSheetDialog.dismiss()
            if (viewModel.webUrl.value!!.size <= 5) {

                viewModel.addUrl(url)


            }


        } else {
            etURL.error = getString(R.string.invalid_url)
            etURL.setTextColor(Color.RED)
        }


    }


    private fun observeUrl() {
        viewModel.webUrl.observe(viewLifecycleOwner)
        {
            if (viewModel.webUrl.value!!.isNotEmpty() && viewModel.webUrl.value!!.size <= 6) {
                binding.rvUrl.visibility = View.VISIBLE
                displayUrl(viewModel.webUrl.value!!)
            } else if (viewModel.webUrl.value!!.size > 5) {
                maximumLimitExceed()
            }
        }
    }


    private fun maximumLimitExceed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.url_dailog_title))
            .setMessage(getString(R.string.url_daiolog_message))
            .setPositiveButton(
                getString(R.string.url_dailog_ok), null
            )
        val alert: AlertDialog = builder.create()
        alert.show()
        alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
        alert.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
    }

    private fun deleteNote() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.delete_dailog_title))
            .setMessage(getString(R.string.delete_dailog_message))
            .setPositiveButton(
                getString(R.string.delte_dailog_positive),
            )

            { _, _ ->
                lifecycleScope.launch(Dispatchers.IO)
                {
                    viewModel.deleteNote(viewModel.notes.id)
                    bottomSheetDialog.dismiss()
                    view?.hideKeyboard()
                    fragmentNavigationLisenter.navigate(HomeFragment(), BackStack.HOME)
                }
            }
            .setNegativeButton(getString(R.string.delte_dailog_pnegative), null)
        val alert: AlertDialog = builder.create()
        alert.show()
    }


    private fun displayUrl(list: MutableList<String>) {
        binding.rvUrl.initRecyclerView(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            ), UrlAdaptor(list, this, true), true
        )

    }


    private fun displayAttachment(list: MutableList<String>) {
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


    private fun showDialogOfUrl() {
         dialogBottomSheetDialog = BottomSheetDialog(requireContext())
        dialogBottomSheetDialog.setContentView(R.layout.add_url)
        dialogBottomSheetDialog.window?.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)
        dialogBottomSheetDialog.show()
        val etUrl = dialogBottomSheetDialog.findViewById<EditText>(R.id.etUrl)!!
        dialogBottomSheetDialog.findViewById<TextView>(R.id.btncancel)?.setOnClickListener()
        {
            view?.hideKeyboard()
            dialogBottomSheetDialog.dismiss()

        }
        dialogBottomSheetDialog.findViewById<TextView>(R.id.btnSubmit)?.setOnClickListener()
        {

            validateUrl()
        }
        etUrl.addTextChangedListener {
            etUrl.setTextColor(Color.BLACK)
        }

    }


    private fun isValidURL(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()

    }

    fun displayExitDialog() {
        if (binding.inputNoteTitle.text.isEmpty() && binding.inputNoteSubtitle.text.isEmpty() && binding.inputNote.text.isEmpty() && viewModel.tempList.isEmpty()) {
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
            val alert: AlertDialog = builder.create()
            alert.show()
            alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            alert.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
        }


    }


    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = getBitmap(requireContext().contentResolver, uri)
                val filename = UUID.randomUUID().toString()
                val isSavedSuccessfully =
                    Storage.savePhotoToInternalStorage(filename, bitmap, requireActivity())
                if (isSavedSuccessfully) {
                    viewModel.addFileName("$filename.jpg")
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

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            val filename = UUID.randomUUID().toString()
            val isSavedSuccessfully =
                it?.let { it1 ->
                    Storage.savePhotoToInternalStorage(filename, it1, requireActivity())
                }

            if (isSavedSuccessfully == true) {
                viewModel.addFileName("$filename.jpg")
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


    override fun onAttachmentClicked(name: String) {

        val fragment = AttachmentPerviewFragment.newInstance(name)
        view?.hideKeyboard()
        fragmentNavigationLisenter.navigate(fragment, BackStack.ATTACHMENT_PREVIEW)
    }


    override fun onStop() {
        viewModel.notes.color = viewModel.selectedNoteColor
        viewModel.action = MenuActions.NOACTION
        super.onStop()

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


    private fun dismissBottomSheet() {
        bottomSheetDialog.dismiss()
    }


    private fun initializeBottomSheet(layout: Int) {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(layout)
        bottomSheetDialog.show()

    }


    private fun retrievedPriorityView() {
        viewModel.priority = viewModel.notes.priority
        when (viewModel.notes.priority) {
            Keys.GREEN -> {
                binding.dropdown.setSelection(0)
            }

            Keys.YELLOW -> {
                binding.dropdown.setSelection(1)

            }
            Keys.RED -> {
                binding.dropdown.setSelection(2)
            }


        }
    }


    private fun toastMessage(string: String) {
        Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT)
            .show()
    }


    override fun onDelete(name: String, position: Int) {
        viewModel.removeFileName(name)
        if (!viewModel.isEdit) {
            Storage.deletePhotoFromInternalStorage(name, requireContext())
            lifecycleScope.launch(Dispatchers.IO)
            {
                viewModel.deleteFileName(name)
            }
        }
        displayRv()


    }


    private suspend fun applyChanges() {
        (viewModel.tempList - viewModel.originalList.toSet()).forEach {
            viewModel.addListFileName(FileName(it, noteId = viewModel.notes.id))


        }
        (viewModel.originalList - viewModel.tempList.toSet()).forEach {
            Storage.deletePhotoFromInternalStorage(it, requireContext())
            viewModel.deleteFileName(it)


        }

    }


    private fun setDateInBottomNavBar() {
        binding.createdTime.text = resources.getString(
            R.string.createdtime,
            getDateAndTime(Calendar.getInstance().timeInMillis)
        )
        binding.updatedTime.visibility = View.GONE
        if (viewModel.isEdit) {

            if ((viewModel.notes.modifiedTime).toInt() == 0) binding.updatedTime.text =
                resources.getString(
                    R.string.updatedTime,
                    getDateAndTime(Calendar.getInstance().timeInMillis)
                )
            else binding.updatedTime.text =
                resources.getString(
                    R.string.updatedTime,
                    getDateAndTime(viewModel.notes.modifiedTime)
                )
            binding.updatedTime.visibility = View.VISIBLE

            binding.createdTime.text = resources.getString(
                R.string.createdtime,
                getDateAndTime(viewModel.notes.createdTime)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.tempList = mutableListOf()

    }


    override fun onClickYes(action: ExitSettingsAction) {
        when (action) {
            ExitSettingsAction.CAMERA, ExitSettingsAction.STORAGE -> {
                view?.hideKeyboard()
                settingsLisenter.settings()
            }
            ExitSettingsAction.NO_ACTION -> {
            }
        }
    }

    override fun removeUrl(string: String) {
        viewModel.removeUrl(string)
    }


//    private fun showActionMenulertColoured() {
//        Alerter.create(requireActivity())
//            .setTitle("Alert Title")
//            .setText("Alert text...")
//            .show()
//    }
}
