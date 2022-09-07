package com.example.notedesk.presentation.createNote

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.speech.RecognizerIntent
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesappfragment.*
import com.example.notesappfragment.databinding.FragmentCreateNotesBinding
import com.example.notedesk.data.data_source.FileName
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.presentation.attachmentPreview.listener.AttachmentLisenter
import com.example.notedesk.presentation.createNote.adaptor.PriorityAdaptor
import com.example.notedesk.presentation.createNote.adaptor.UrlAdaptor
import com.example.notedesk.presentation.createNote.listener.DialogLisenter
import com.example.notedesk.presentation.createNote.listener.ExitDailogLisenter
import com.example.notedesk.presentation.createNote.listener.UrlListener
import com.example.notedesk.presentation.home.Listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.home.Listener.SettingsLisenter
import com.example.notedesk.domain.util.date.DateUtil.getDateAndTime
import com.example.notedesk.domain.util.keys.Constants
import com.example.notedesk.domain.util.keys.IndentKeys
import com.example.notedesk.domain.util.storage.InternalStoragePhoto
import com.example.notedesk.domain.util.storage.Storage
import com.example.notedesk.presentation.attachmentPreview.AttachmentPerviewFragment
import com.example.notedesk.presentation.attachmentPreview.adaptor.AttachmentAdaptor
import com.example.notedesk.presentation.createNote.dailog.AddImageDailog
import com.example.notedesk.presentation.createNote.dailog.CameraSettingDailog
import com.example.notedesk.presentation.createNote.dailog.StorageSettings
import com.example.notedesk.presentation.createNote.enums.AddImage
import com.example.notedesk.presentation.createNote.enums.ExitSettingsAction
import com.example.notesappfragment.features.presentation.home.HomeFragment
import com.example.notedesk.presentation.home.enums.MenuActions
import com.example.notedesk.presentation.util.BackStack
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.*
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class CreateNotesFragment : Fragment(), DialogLisenter, ExitDailogLisenter,
    UrlListener,
    AttachmentLisenter {


    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 3
        private const val SAVED_NOTES = "notes"
        fun newInstance(data: Notes?, res: MenuActions) =
            CreateNotesFragment().apply {
                val bundle = Bundle()
                bundle.putSerializable(IndentKeys.MENU_ACTION, res)
                bundle.putParcelable(SAVED_NOTES, data)
                arguments = bundle
            }
    }

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var action: MenuActions
    private var notes: Notes? = null
    private lateinit var binding: FragmentCreateNotesBinding
    private lateinit var bottomsheet: BottomSheetDialog
    private lateinit var dialogBottomSheetDialog: BottomSheetDialog
    private var selectedNoteColor = "#F1F1F1"
    private var isFavorite: Boolean = false
    private val viewModel: CreateNoteViewModel by viewModels()
    private var priority: Int = IndentKeys.GREEN
    private lateinit var currentNote: Notes
    private var tempList: MutableList<String> = mutableListOf()
    private lateinit var adaptor: AttachmentAdaptor
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null
    private var settingsLisenter: SettingsLisenter? = null
    private var isEdit: Boolean = false
    private lateinit var originalList: MutableList<String>


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
        binding = FragmentCreateNotesBinding.inflate(layoutInflater, container, false)
        checkForFavorite()
        setupCustomSpinner()
        initializeMenu()

        return binding.root

    }


    private fun showActionMenu() {
        initializeBottomSheet(R.layout.action)
        bottomsheet.findViewById<ConstraintLayout>(R.id.layout_take_photo)?.setOnClickListener()
        {

            if (viewModel.fileName.value!!.size <= 5) {
                takePhoto()
                dismissBottomSheet()
            } else {
                maximumCountExceed()
            }


        }
        bottomsheet.findViewById<ConstraintLayout>(R.id.layout_add_image)?.setOnClickListener()
        {
            if (viewModel.fileName.value!!.size <= 5) {
                chooseImage()
                dismissBottomSheet()
            } else {
                maximumCountExceed()
            }


        }
        bottomsheet.findViewById<ConstraintLayout>(R.id.layout_voice_note)?.setOnClickListener()
        {


            voiceToText()
            dismissBottomSheet()
        }
        bottomsheet.findViewById<ConstraintLayout>(R.id.layout_add_url)?.setOnClickListener()
        {

            if (viewModel.webUrl.value?.size!! <= 5) {
                showDialogOfUrl()
                dismissBottomSheet()
            } else {
                maximumLimitExceed()
            }
            dismissBottomSheet()

        }
        bottomsheet.findViewById<View>(R.id.top_view)?.setOnClickListener()
        {
            dismissBottomSheet()
        }

    }

    private fun maximumCountExceed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Maximum Limit Exceeds... ")
            .setMessage("Sorry, Pls try to remove attachment ,Try again  ")
            .setPositiveButton(
                "Ok", null
            )
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun setupCustomSpinner() {
        val adaptor = PriorityAdaptor(requireActivity(), BackStack.priortyList)
        binding.dropdown.adapter = adaptor
        binding.dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> priority = IndentKeys.GREEN
                    1 -> priority = IndentKeys.YELLOW
                    2 -> priority = IndentKeys.RED

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentNote = viewModel.tempNotes
        binding.create.setOnClickListener()
        {

        }
        initializeToolBar()
        if (isEdit) {
            restoreDataToView()
            otherOptionsListener()
            toolbar.title = "Edit Note"
        }

        initAttachmentTitle()
        if (!isEdit) {
            triggerRecyclerView()
        }

        bottomNavigationListener()
        setDateInBottomNavBar()
        observeUrl()

        when (action) {
            MenuActions.ATTACH -> showDialogFragment(AddImageDailog())
            MenuActions.VOICE -> voiceToText()
            MenuActions.WEBLINK -> showDialogOfUrl()
            else -> {

            }
        }

    }

    private fun otherOptionsListener() {
        binding.otherOptions.setOnClickListener()
        {
            miscellaneous()
        }
    }

    private fun restoreDataToView() {

        binding.otherOptions.visibility = View.VISIBLE
        notes?.apply {
            retrievedPriorityView()
            binding.inputNoteTitle.setText(title)
            binding.inputNoteSubtitle.setText(subtitle)
            binding.inputNote.setText(noteText)
            this@CreateNotesFragment.priority = priority
            selectedNoteColor = color
            isFavorite = favorite
            updateFavorites()

            setSubtitleIndicatorColor()
            viewModel.webUrl.value = notes!!.weblink
        }

    }


    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        action = bundle[IndentKeys.MENU_ACTION] as MenuActions
        notes = bundle.getParcelable(SAVED_NOTES) as Notes?

        if (notes != null) {
            lifecycleScope.launch(Dispatchers.IO)
            {
                originalList = viewModel.getFileName(notes?.id!!).toMutableList()
                withContext(Dispatchers.Main)
                {
                    viewModel.fileName.value = originalList.toMutableList()


                }


            }
        }

        isEdit = notes != null

    }


    private fun showDialogFragment(dialog: DialogFragment) {
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog.activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show(childFragmentManager, "2")


    }


    private fun triggerRecyclerView() {

        var list: MutableList<InternalStoragePhoto>
        hideRv()
        lifecycleScope.launch(Dispatchers.IO) {
            list = getPhotosFromInternalStorage(tempList)
            withContext(Dispatchers.Main) {
                displayAttachment(list)
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun initAttachmentTitle() {


        viewModel.fileName.observe(viewLifecycleOwner)
        {
            tempList = viewModel.fileName.value as MutableList<String>

            if (tempList.isNotEmpty()) {
                binding.tvAttachmentTitle.visibility = View.VISIBLE
                binding.tvAttachmentTitle.text = "Attachment (${tempList.size})"
                triggerRecyclerView()


            } else {
                binding.tvAttachmentTitle.visibility = View.GONE

            }
        }


    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeToolBar() {
        toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.title = Constants.CREATE_NOTE_FRAGMENT_TITLE
        (activity as AppCompatActivity).apply {
            this.setSupportActionBar(toolbar)
            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)

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


    private fun checkForFavorite() {
        binding.favoriteImage.setOnClickListener {
            isFavorite = !isFavorite
            updateFavorites()
        }
    }

    private fun updateFavorites() {
        if (isFavorite) {
            binding.notAFaviorte.visibility = View.GONE
            binding.yesAFaviortie.visibility = View.VISIBLE
        } else {
            binding.notAFaviorte.visibility = View.VISIBLE
            binding.yesAFaviortie.visibility = View.GONE

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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
    }


    private fun createNotes() {

        if (isEdit) {

            notes?.let { notes ->

                binding.apply {
                    notes.title = inputNoteTitle.text.toString()
                    notes.subtitle = inputNoteSubtitle.text.toString()
                    notes.noteText = inputNote.text.toString()
                    notes.priority = priority
                    notes.favorite = isFavorite
                    notes.weblink = viewModel.webUrl.value as ArrayList<String>
                    notes.color = selectedNoteColor
                    notes.modifiedTime = Calendar.getInstance().timeInMillis
                }


                if (notes.title.isNotEmpty() && notes.noteText.isNotEmpty()) {
                    notes.let { viewModel.updateNote(it) }
                    lifecycleScope.launch(Dispatchers.IO)
                    {
                        applyChanges()
                        withContext(Dispatchers.Main) {
                            toastMessage("Note is updated Successfully")
                            fragmentNavigationLisenter?.navigate(HomeFragment(), BackStack.HOME)
                        }

                    }
                } else if (notes.title.isEmpty() && notes.noteText.isNotEmpty()) {
                    toastMessage("Title is Empty")
                } else if (notes.title.isNotEmpty() && notes.noteText.isEmpty()) {
                    toastMessage("Description is Empty")
                } else {
                    toastMessage("both title and description is Empty")
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


                        tempList.forEach {
                            viewModel.addListFileName(FileName(it, noteId))
                        }
                        toastMessage("Note is Saved Successfully")

                        fragmentNavigationLisenter?.navigate(HomeFragment(), BackStack.HOME)

                    } else if (notes.title.isEmpty() && notes.noteText.isNotEmpty()) {
                        toastMessage("Title is Empty")
                    } else if (notes.title.isNotEmpty() && notes.noteText.isEmpty()) {
                        toastMessage("Description is Empty")
                    } else {
                        toastMessage("both title and description is Empty")
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






        currentNote = Notes(
            title,
            subtitle,
            createdDate,
            modifiedTime = createdDate,
            color = selectedNoteColor,
            weblink = viewModel.webUrl.value as ArrayList<String>,
            noteText = noteText
        )
        currentNote.priority = priority
        currentNote.favorite = isFavorite
        currentNote.attachmentCount = tempList.size
        return currentNote

    }


    private fun shareNotes() {

        val content: String = binding.inputNoteTitle.text.toString().trim() + "\n\n" +
                binding.inputNoteSubtitle.text.toString().trim() + "\n\n" +
                binding.inputNote.text.toString().trim()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            binding.inputNoteSubtitle.text.toString().trim()
        )
        shareIntent.putExtra(Intent.EXTRA_TEXT, content)
        startActivity(Intent.createChooser(shareIntent, "Share via"))

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


    private fun setSubtitleIndicatorColor() {
        binding.create.setBackgroundColor(Color.parseColor(selectedNoteColor))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
        bottomsheet.findViewById<ConstraintLayout>(R.id.layout_delete_note)
            ?.setOnClickListener()
            {
                deleteNote()
                dismissBottomSheet()
            }
        bottomsheet.findViewById<ConstraintLayout>(R.id.layout_share_Notes)
            ?.setOnClickListener()
            {
                shareNotes()
                dismissBottomSheet()
            }
        bottomsheet.findViewById<View>(R.id.top_view)?.setOnClickListener()
        {
            dismissBottomSheet()
        }
    }


    private fun removeAllSelectedColors() {
        bottomsheet.findViewById<ImageView>(R.id.check_color1)?.setImageResource(0)
        bottomsheet.findViewById<ImageView>(R.id.check_color2)?.setImageResource(0)
        bottomsheet.findViewById<ImageView>(R.id.check_color3)?.setImageResource(0)
        bottomsheet.findViewById<ImageView>(R.id.check_color4)?.setImageResource(0)
        bottomsheet.findViewById<ImageView>(R.id.check_color5)?.setImageResource(0)
        bottomsheet.findViewById<ImageView>(R.id.check_color6)?.setImageResource(0)
        bottomsheet.findViewById<ImageView>(R.id.check_color7)?.setImageResource(0)
        bottomsheet.findViewById<ImageView>(R.id.check_color8)?.setImageResource(0)
        bottomsheet.findViewById<ImageView>(R.id.check_color9)?.setImageResource(0)
        bottomsheet.findViewById<ImageView>(R.id.check_color10)?.setImageResource(0)
    }

    private fun restoreColorToView(color: String) {

        removeAllSelectedColors()
        when (color) {
            "#F1F1F1" -> bottomsheet.findViewById<ImageView>(R.id.check_color1)
                ?.setImageResource(R.drawable.ic_done)
            "#fcada8" -> bottomsheet.findViewById<ImageView>(R.id.check_color2)
                ?.setImageResource(R.drawable.ic_done)
            "#f19f71" -> bottomsheet.findViewById<ImageView>(R.id.check_color3)
                ?.setImageResource(R.drawable.ic_done)
            "#EAC78C" -> bottomsheet.findViewById<ImageView>(R.id.check_color4)
                ?.setImageResource(R.drawable.ic_done)
            "#b4ddd4" -> bottomsheet.findViewById<ImageView>(R.id.check_color5)
                ?.setImageResource(R.drawable.ic_done)
            "#d3bed7" -> bottomsheet.findViewById<ImageView>(R.id.check_color6)
                ?.setImageResource(R.drawable.ic_done)
            "#ECB680" -> bottomsheet.findViewById<ImageView>(R.id.check_color7)
                ?.setImageResource(R.drawable.ic_done)
            "#81DDD3" -> bottomsheet.findViewById<ImageView>(R.id.check_color8)
                ?.setImageResource(R.drawable.ic_done)
            "#A4ADE4" -> bottomsheet.findViewById<ImageView>(R.id.check_color9)
                ?.setImageResource(R.drawable.ic_done)
            "#F57777" -> bottomsheet.findViewById<ImageView>(R.id.check_color10)
                ?.setImageResource(R.drawable.ic_done)

        }
    }


    private fun pickColor() {
        initializeBottomSheet(R.layout.pick_color)
        if (isEdit) {
            restoreColorToView(notes!!.color)
        }

        val imageColor1 = bottomsheet.findViewById<ImageView>(R.id.check_color1)!!
        val imageColor2 = bottomsheet.findViewById<ImageView>(R.id.check_color2)!!
        val imageColor3 = bottomsheet.findViewById<ImageView>(R.id.check_color3)!!
        val imageColor4 = bottomsheet.findViewById<ImageView>(R.id.check_color4)!!
        val imageColor5 = bottomsheet.findViewById<ImageView>(R.id.check_color5)!!
        val imageColor6 = bottomsheet.findViewById<ImageView>(R.id.check_color6)!!
        val imageColor7 = bottomsheet.findViewById<ImageView>(R.id.check_color7)!!
        val imageColor8 = bottomsheet.findViewById<ImageView>(R.id.check_color8)!!
        val imageColor9 = bottomsheet.findViewById<ImageView>(R.id.check_color9)!!
        val imageColor10 = bottomsheet.findViewById<ImageView>(R.id.check_color10)!!

        imageColor1.setOnClickListener {
            selectedNoteColor = "#F1F1F1"
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
            selectedNoteColor = "#fcada8"
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
            selectedNoteColor = "#f19f71"
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
            selectedNoteColor = "#EAC78C"
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
            selectedNoteColor = "#b4ddd4"
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
            selectedNoteColor = "#d3bed7"
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
            selectedNoteColor = "#ECB680"
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
            selectedNoteColor = "#81DDD3"
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
            selectedNoteColor = "#A4ADE4"
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
            selectedNoteColor = "#F57777"
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
        val url = etURL.text.toString()
        if (isValidURl(url)) {
            dialogBottomSheetDialog.dismiss()
            if (viewModel.webUrl.value!!.size <= 5) {
                viewModel.addUrl(url)
            }


        } else {
            etURL.error = "Pls Enter the Valid URL"
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
        builder.setTitle("Maximum Limit Exceeds... ")
            .setMessage("Sorry, Pls try to replace Existing URL  ")
            .setPositiveButton(
                "Ok", null
            )
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun deleteNote() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Confirm Delete ")
            .setMessage("Are you Sure Want to Delete Note ")
            .setPositiveButton(
                "Yes",
            )

            { _, _ ->
                lifecycleScope.launch(Dispatchers.IO)
                {
                    viewModel.deleteNote(notes!!.id)
                    bottomsheet.dismiss()
                    fragmentNavigationLisenter?.navigate(HomeFragment(), BackStack.HOME)
                }
            }
            .setNegativeButton("No", null)
        val alert: AlertDialog = builder.create()
        alert.show()
    }


    private fun displayUrl(list: MutableList<String>) {
        val recyclerView = binding.rvUrl
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = UrlAdaptor(list, this)
    }


    private fun displayAttachment(list: MutableList<InternalStoragePhoto>) {

        AttachmentAdaptor(
            list, this,
            isDelete = true,
        ).also { adaptor = it }
        val recyclerView = binding.rvAttachment
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.adapter = adaptor

        displayRv()


    }


    private fun showDialogOfUrl() {
        dialogBottomSheetDialog = BottomSheetDialog(requireContext())
        dialogBottomSheetDialog.setContentView(R.layout.add_url)
        dialogBottomSheetDialog.show()
        val eturl = dialogBottomSheetDialog.findViewById<EditText>(R.id.etUrl)!!
        dialogBottomSheetDialog.findViewById<TextView>(R.id.btncancel)?.setOnClickListener()
        {
            dialogBottomSheetDialog.dismiss()
        }
        dialogBottomSheetDialog.findViewById<TextView>(R.id.btnSubmit)?.setOnClickListener()
        {
            validateUrl()

        }
        eturl.addTextChangedListener {
            eturl.setTextColor(Color.BLACK)
        }

    }


    private fun isValidURl(url: String?): Boolean {

        return try {
            URL(url).toURI()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun displayExitDailog() {
        if (binding.inputNoteTitle.text.isEmpty() && binding.inputNoteSubtitle.text.isEmpty() && binding.inputNote.text.isEmpty() && tempList.isEmpty()) {
            Toast.makeText(requireContext(), "Blank Note is Discarded", Toast.LENGTH_SHORT).show()
            fragmentNavigationLisenter?.navigate(HomeFragment(), BackStack.HOME)


        } else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Are you sure?")
                .setMessage("Do you want to Exit without Saving ?")
                .setPositiveButton(
                    "Yes"

                ) { _, _ ->
                    Log.i("arun", "backstack ${parentFragmentManager.backStackEntryCount}")
                    parentFragmentManager.popBackStack()

                }
                .setNegativeButton("No", null)
            val alert: AlertDialog = builder.create()
            alert.show()
        }


    }


    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap =
                    getBitmap(requireContext().contentResolver, uri)
                val filename = UUID.randomUUID().toString()
                val isSavedSuccessfully =
                    Storage.savePhotoToInternalStorage(filename, bitmap, requireActivity())
                if (isSavedSuccessfully) {
                    viewModel.addFileName("$filename.jpg")
                    Toast.makeText(requireContext(), "Photo upload successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Failed to save photo", Toast.LENGTH_SHORT)
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
                Toast.makeText(requireContext(), "Photo saved successfully", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Failed to save photo", Toast.LENGTH_SHORT).show()
            }

        }

//
//    private fun showNotification(str:String) {
//        val notification = NotificationHandler(requireContext())
//        notification.showNotification(str)
//    }
//
//    private fun cancelNotification() {
//        val notification = NotificationHandler(requireContext())
//        notification.cancelNotification()
//    }


    private suspend fun getPhotosFromInternalStorage(filenames: List<String>): MutableList<InternalStoragePhoto> {
        val list = mutableListOf<InternalStoragePhoto>()
        filenames.forEach { name ->
            loadPhotosFromInternalStorage().forEach {
                if (name == it.name)
                    list.add(it)
            }
        }

        return list
    }

    private suspend fun loadPhotosFromInternalStorage(): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = requireActivity().filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }


    override fun onAttachmentClicked(internalStoragePhoto: InternalStoragePhoto) {

        val fragment = AttachmentPerviewFragment.newInstance(internalStoragePhoto)
        fragmentNavigationLisenter?.navigate(fragment, BackStack.ATTACHMENT_PREVIEW)
    }


    override fun onStop() {
        viewModel.tempNotes = saveNotes()
        super.onStop()

    }


    override fun onResume() {
        currentNote = viewModel.tempNotes
        super.onResume()

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
        bottomsheet.dismiss()
    }


    private fun initializeBottomSheet(layout: Int) {
        bottomsheet = BottomSheetDialog(requireContext())
        bottomsheet.setContentView(layout)
        bottomsheet.show()

    }


    private fun retrievedPriorityView() {
        priority = notes!!.priority
        when (priority) {
            IndentKeys.GREEN -> {
                binding.dropdown.setSelection(0)
            }

            IndentKeys.YELLOW -> {
                binding.dropdown.setSelection(1)

            }
            IndentKeys.RED -> {
                binding.dropdown.setSelection(2)
            }


        }
    }


    private fun toastMessage(string: String) {
        Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT)
            .show()
    }


    override fun onDelete(internalStoragePhoto: InternalStoragePhoto, position: Int) {
        viewModel.removeFileName(internalStoragePhoto.name)
        if (!isEdit) {
            Storage.deletePhotoFromInternalStorage(internalStoragePhoto.name, requireContext())
            lifecycleScope.launch(Dispatchers.IO)
            {
                viewModel.deleteFileName(internalStoragePhoto.name)
            }
        }
        displayRv()


    }


    private suspend fun applyChanges() {
        Log.i("arun", "original List $originalList")
        Log.i("arun", "temp List $tempList")
        (tempList - originalList.toSet()).forEach {
            viewModel.addListFileName(FileName(it, noteId = notes!!.id))


        }
        (originalList - tempList.toSet()).forEach {
            Storage.deletePhotoFromInternalStorage(it, requireContext())
            viewModel.deleteFileName(it)


        }

    }

    @SuppressLint("SetTextI18n")
    private fun setDateInBottomNavBar() {
        binding.createdTime.text =
            "Created Time:   ${getDateAndTime(Calendar.getInstance().timeInMillis)}"
        binding.updatedTime.visibility = View.GONE
        if (isEdit) {

            if ((notes?.modifiedTime)?.toInt() == 0) binding.updatedTime.text =
                "Updated Time:   ${getDateAndTime(Calendar.getInstance().timeInMillis)}"
            else binding.updatedTime.text =
                "Updated Time:   ${getDateAndTime(notes!!.modifiedTime)}"
            binding.updatedTime.visibility = View.VISIBLE

            binding.createdTime.text = "Created Time:   ${getDateAndTime(notes!!.createdTime)}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsLisenter = null
        fragmentNavigationLisenter = null
        tempList = mutableListOf()
    }


    override fun onClickYes(action: ExitSettingsAction) {
        when (action) {
            ExitSettingsAction.CAMERA, ExitSettingsAction.STORAGE -> {
                settingsLisenter?.settings()
            }
            ExitSettingsAction.NO_ACTION -> {
            }
        }
    }

    override fun removeUrl(string: String) {
        viewModel.removeUrl(string)
    }


}
