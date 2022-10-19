package com.example.version2.presentation.homeScreen


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.version2.presentation.util.keys.Constants
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.util.sharedPreference.SharedPreference
import com.example.version2.presentation.util.storage.InternalStoragePhoto
import com.example.version2.presentation.util.storage.Storage
import com.example.version2.R
import com.example.version2.databinding.FragmentHomeBinding
import com.example.version2.domain.model.Attachment
import com.example.version2.domain.model.Note
import com.example.version2.presentation.common.NotesApplication
import com.example.version2.presentation.common.NoteScreen
import com.example.version2.presentation.common.viewHolder.NotesRVViewHolder
import com.example.version2.presentation.attachmentPreview.AttachmentPreviewFragment
import com.example.version2.presentation.createNote.CreateNoteFragment
import com.example.version2.presentation.homeScreen.adaptor.NotesAdaptor
import com.example.version2.presentation.homeScreen.dialog.FilterDialog
import com.example.version2.presentation.homeScreen.dialog.SortDialog
import com.example.version2.presentation.homeScreen.enums.FilterChoiceSelected
import com.example.version2.presentation.homeScreen.enums.MenuActions
import com.example.version2.presentation.homeScreen.enums.SortBy
import com.example.version2.presentation.homeScreen.enums.SortValues
import com.example.version2.presentation.homeScreen.listener.*
import com.example.version2.presentation.login.activity.LoginActivity
import com.example.version2.presentation.model.NotesRvItem
import com.example.version2.presentation.model.mapper.UiNotesMapper
import com.example.version2.presentation.policy.PolicyFragment
import com.example.version2.presentation.previewNote.NotePreviewFragment
import com.example.version2.presentation.profile.ProfileFragment
import com.example.version2.presentation.search.SearchFragment
import com.example.version2.presentation.util.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment(), SortLisenter, FilterChoiceLisenter, NotesListener {


    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(
            this,
            (requireActivity().applicationContext as NotesApplication).homeFactory
        )[HomeViewModel::class.java]
    }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NotesAdaptor
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null
    private var settingsLisenter: SettingsLisenter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        retrievePreference()
        if (context is FragmentNavigationLisenter) {
            fragmentNavigationLisenter = context
        }
        if (context is SettingsLisenter) {
            settingsLisenter = context

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        fetchData()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeView()
        viewListener()
        eventHandler()


    }


    override fun onStop() {
        super.onStop()
        contextualActionModeDisabled()
        savePreference()

    }


    private fun fetchData() {
        viewModel.setUserId((requireActivity() as NoteScreen).getUserID())
    }


    private fun eventHandler() {
        displayNotes()
        backPressedListener()
        setActionOnViews()
        if (viewModel.isContextualActive)
            contextualActionMode()
    }

    private fun viewListener() {
        notesListener()
        updateAppBarText(viewModel.currentSortOptions)
        observeFilterText()
        actionBarListener()
        navigationLisenter()
        contextualObserver()
    }


    private fun animateNavigationDrawer() {
        binding.mainDrawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val diffScaledOffset: Float =
                    slideOffset * (1 - Keys.END_SCALE)
                val offsetScale = 1 - diffScaledOffset
                val contentView = binding.contentView
                contentView.scaleX = offsetScale
                contentView.scaleY = offsetScale
                val xOffset = drawerView.width * slideOffset
                val xOffsetDiff: Float = contentView.width * diffScaledOffset / 2
                val xTranslation = xOffsetDiff - xOffset
                contentView.translationX = xTranslation
            }
        })
    }


    private fun initializeDrawerAndAction() {
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.mainDrawerLayout,
            requireView().findViewById(R.id.my_toolbar),
            0,
            0
        )
        binding.mainDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.mainNavigationMenu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_add_voice -> {
                    navigateFragment(MenuActions.VOICE)
                    binding.mainDrawerLayout.closeDrawer(GravityCompat.START)

                }
                R.id.menu_add_image -> {
                    navigateFragment(MenuActions.ATTACH)
                    binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.menu_url -> {
                    navigateFragment(MenuActions.WEBLINK)
                    binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.menu_add_note -> {
                    navigateFragment(MenuActions.NOACTION)
                    binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.menu_app_info -> {
                    settingsLisenter?.settings()

                }
                R.id.menu_privacy -> {
                    navigationOnFragment(PolicyFragment(), BackStack.POLICY)

                }
                R.id.menu_sign_out -> {
                    signOut()
                }
                R.id.menu_profile -> {
                    navigateToProfile()
                }
            }
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }
        animateNavigationDrawer()
        var image: String?
        var name: String?
        var bitmap: InternalStoragePhoto?
        lifecycleScope.launch(Dispatchers.IO)
        {
            withContext(Dispatchers.IO) {
                image = viewModel.getProfileImage(viewModel.userId.toLong())
                name = viewModel.getFullName(viewModel.userId.toLong())
                bitmap = image?.let {
                    Storage.getPhotosFromInternalStorage(
                        requireActivity(),
                        it
                    )

                }


            }

            withContext(Dispatchers.Main)
            {
                binding.mainNavigationMenu.getHeaderView(0)
                    .findViewById<ImageView>(R.id.ivProfilePicture).setOnClickListener {

                        if (bitmap != null) {
                            fragmentNavigationLisenter?.navigate(
                                AttachmentPreviewFragment.newInstance(bitmap!!.name),
                                BackStack.ATTACHMENT_PREVIEW
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.pls_Set_profile_image),
                                Toast.LENGTH_SHORT
                            )
                                .show()

                        }


                    }

                name?.let {
                    binding.mainNavigationMenu.getHeaderView(0)
                        .findViewById<TextView>(R.id.appName).text = it

                }

                if (bitmap == null) {
                    binding.mainNavigationMenu.getHeaderView(0)
                        .findViewById<ImageView>(R.id.ivProfilePicture)
                        .setImageResource(
                            R.drawable.ic_profile_picture
                        )


                } else {
                    binding.mainNavigationMenu.getHeaderView(0)
                        .findViewById<ImageView>(R.id.ivProfilePicture)
                        .setImageBitmap(
                            bitmap?.bmp
                        )
                }


            }
        }


    }

    private fun navigateToProfile() {

        lifecycleScope.launch()
        {
            withContext(Dispatchers.IO)
            {
                fragmentNavigationLisenter?.navigate(
                    ProfileFragment.newInstance(viewModel.userId),
                    BackStack.PROFILE
                )
            }
        }

    }

    private fun signOut() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.are_your_sure))
            .setIcon(R.drawable.ic_login)
            .setMessage(getString(R.string.sign_out))
            .setPositiveButton(
                getString(R.string.yes)

            ) { _, _ ->
                SharedPreference(requireActivity()).putIntSharePreferenceInt(Keys.USER_ID, 0)
                fragmentNavigationLisenter?.navigateActivity(LoginActivity()::class.java)
            }
            .setNegativeButton(getString(R.string.no), null)
            .setCancelable(false)
        builder.create().apply {
            show()
            getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
        }


    }

    private fun navigateFragment(action: MenuActions) {
        navigationOnFragment(CreateNoteFragment.newInstance(null, action), BackStack.CREATE)
    }

    private fun navigationOnFragment(fragment: Fragment, name: String) {
        Log.i("arun", "$fragmentNavigationLisenter")
        fragmentNavigationLisenter?.navigate(fragment, name)
    }


    private fun displayNotes() {
        if (viewModel.currentMode == Keys.LIST_VIEW) {
            setAdaptor(null, LinearLayoutManager(requireContext()), true, viewModel.displayList)
            activity

        } else if (viewModel.currentMode == Keys.STAGGERED_GRID) {
            setAdaptor(
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL),
                null,
                false,
                viewModel.displayList
            )
        }


    }

    private fun updateRecyclerViewGrid(currentMode: Int) {
        if (currentMode == Keys.LIST_VIEW) {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        } else if (currentMode == Keys.STAGGERED_GRID) {
            binding.recyclerView.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }


    private fun setAdaptor(
        staggeredGridLayoutManager: StaggeredGridLayoutManager?,
        linearLayoutManager: LinearLayoutManager?,
        isLinear: Boolean,
        list: List<Note>
    ) {
        if (isLinear) {
            binding.recyclerView.layoutManager = linearLayoutManager
        } else {
            binding.recyclerView.layoutManager = staggeredGridLayoutManager
        }
        adapter =
            NotesAdaptor(
                requireActivity(),
                list.toPresenterList(), this
            )

        val colorDrawableBackground = ColorDrawable(Keys.BACKGROUND_COLOR.toColor())
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.avd_delete)!!

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {


                val itemView = viewHolder.itemView
                val iconMarginVertical =
                    (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {

                    colorDrawableBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMarginVertical
                    )
                } else {
                    colorDrawableBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                    )
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)
                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                deleteIcon.draw(c)
                c.restore()



                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )


            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val deletedNotes: NotesRvItem.UINotes =
                    adapter.getNotesAtPosition(viewHolder.adapterPosition)
                val position = viewHolder.adapterPosition
                adapter.removeNoteAtPosition(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                Snackbar.make(
                    binding.root,
                    "Note   ${deletedNotes.note.title} Deleted ",
                    Snackbar.LENGTH_SHORT
                )

                    .apply {
                        anchorView = binding.floatingActionAddNotesBtn
                        setAction(getString(R.string.undo))
                        {


                            adapter.addNotes(position, deletedNotes)
                            lifecycleScope.launch(Dispatchers.IO)
                            {
                                viewModel.addNotes(
                                    deletedNotes.note,
                                    (requireActivity() as NoteScreen).getUserID()
                                )
                            }

                            adapter.notifyItemInserted(position)
                        }

                    }.show()
                viewModel.deleteNote(
                    deletedNotes.note.id, viewModel.userId
                )

            }

        }
        ).attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.adapter = adapter


    }


    private fun initializeView() {
        initializeToolBar()
        initializeDrawerAndAction()
        initializeMenu()
    }


    private fun updateAppBarText(
        sortValues: SortValues
    ) {


        when (sortValues) {
            SortValues.ALPHABETICALLY_TITLE -> {

                binding.sort.btnSortText.text = getString(R.string.sort_title)
            }
            SortValues.ALPHABETICALLY_SUBTITLE -> {

                binding.sort.btnSortText.text = getString(R.string.sort_subtitle)
            }
            SortValues.MODIFICATION_DATE -> {

                binding.sort.btnSortText.text = getString(R.string.sort_modified)
            }
            SortValues.CREATION_DATE -> {
                binding.sort.btnSortText.text = getString(R.string.sort_creation)
            }
            SortValues.PRIORITY -> {
                binding.sort.btnSortText.text = getString(R.string.sort_priority)
            }
        }


    }


    private fun observeFilterText() {
        viewModel.filterChoiceSelected.observe(viewLifecycleOwner)
        { count ->

            if (count.getSelectedCount() > 0) {
                binding.sort.btnFilterText.text =
                    resources.getString(R.string.selectedCount, count.getSelectedCount())
                binding.sort.btnFilterText.visibility = View.VISIBLE


            } else {
                binding.sort.btnFilterText.visibility = View.GONE
            }


        }

    }


    private fun actionBarListener() {

        binding.sort.sort.setOnClickListener()
        {
            if (viewModel.oldMyNotes.size > 1) {
                displaySortDailog()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.insufficent),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        binding.sort.filter.setOnClickListener()
        {

            displayFilterDailog()


        }
    }

    private fun navigationLisenter() {
        binding.floatingActionAddNotesBtn.setOnClickListener {
            navigateFragment(MenuActions.NOACTION)
        }
    }

    private fun initializeMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.home, menu)
                val list = menu.findItem(R.id.menu_List)
                when (viewModel.currentMode) {
                    1 -> {
                        list.setIcon(R.drawable.ic_view_list)
                    }
                    0 -> {
                        list.setIcon(R.drawable.ic_view_grid)
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_List -> {
                        viewModel.setCurrentMode(
                            if (viewModel.currentMode == Keys.LIST_VIEW) {
                                menuItem.setIcon(R.drawable.ic_view_list)
                                Keys.STAGGERED_GRID

                            } else {
                                menuItem.setIcon(R.drawable.ic_view_grid)
                                Keys.LIST_VIEW

                            }
                        )
                        updateRecyclerViewGrid(viewModel.currentMode)

                        true
                    }
                    R.id.menu_Search -> {
                        navigationOnFragment(SearchFragment(), BackStack.SEARCH)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }


    private fun setActionOnViews() {
        binding.mainBottomAppBar.setOnMenuItemClickListener()
        {
            when (it.itemId) {
                R.id.menu_image -> {
                    navigateFragment(MenuActions.ATTACH)
                    true
                }
                R.id.menu_voice -> {
                    navigateFragment(MenuActions.VOICE)
                    true
                }
                R.id.menu_web_link -> {
                    navigateFragment(MenuActions.WEBLINK)
                    true
                }
                else -> false
            }
        }
    }


    private fun contextualObserver() {
        viewModel.contextual.observe(viewLifecycleOwner)
        { flag ->
            if (flag) {

                binding.mainBottomAppBar.visibility = View.GONE
                binding.sort.root.visibility = View.GONE
                binding.floatingActionAddNotesBtn.visibility = View.GONE
            } else {

                binding.mainBottomAppBar.visibility = View.VISIBLE
                binding.sort.root.visibility = View.VISIBLE
                binding.floatingActionAddNotesBtn.visibility = View.VISIBLE
            }

        }
    }


    private fun notesListener() {
        lifecycleScope.launch()
        {


            viewModel.getNotes(viewModel.userId)
                .observe(viewLifecycleOwner) {


                    if (it.isEmpty()) {
                        binding.imageEmpty.visibility = View.VISIBLE
                        binding.textEmpty.visibility = View.VISIBLE
                    } else {
                        binding.imageEmpty.visibility = View.GONE
                        binding.textEmpty.visibility = View.GONE

                    }

                    if (it.isNotEmpty()) {
                        binding.sort.root.visibility = View.VISIBLE

                    } else {
                        binding.sort.root.visibility = View.GONE

                    }


                    viewModel.oldMyNotes = it
                    viewModel.filterList = it
                    viewModel.displayList = viewModel.sortChoiceByList(
                        viewModel.filterListByChoice(
                            viewModel.filterList,
                            viewModel.filterChoiceSelected.value!!
                        ), viewModel.sortBy, viewModel.currentSortOptions


                    )
                    if (viewModel.displayList.isEmpty() && it.isNotEmpty()) {
                        binding.filterEmptyImage.visibility = View.VISIBLE
                        binding.filterEmptyText.visibility = View.VISIBLE
                    } else {
                        binding.filterEmptyImage.visibility = View.GONE
                        binding.filterEmptyText.visibility = View.GONE
                    }
                    adapter.setData(viewModel.displayList.toPresenterList())


                }
        }


    }


    private fun displaySortDailog(): Boolean {
        val dialog = SortDialog.newInstance(viewModel.currentSortOptions, viewModel.sortBy)
        dialog.show(childFragmentManager, getString(R.string.SORT))
        return true
    }


    private fun displayFilterDailog(): Boolean {
        val dialog = FilterDialog.newInstance(viewModel.filterChoiceSelected.value!!)
        dialog.show(childFragmentManager, getString(R.string.FILTER))
        return true
    }


    private fun savePreference() {
        SharedPreference(requireActivity()).apply {
            putIntSharePreferenceInt(Keys.CURRENT_MODE, viewModel.currentMode)
            putStringSharedPreference(Keys.SORT_ORDER, viewModel.sortBy.toString())
            putStringSharedPreference(
                Keys.CURRENT_SORT_OPTION,
                viewModel.currentSortOptions.toString()
            )
            putBooleanSharedPreference(
                Keys.IS_FAVORITE,
                viewModel.filterChoiceSelected.value!!.isFavorite
            )
            putBooleanSharedPreference(
                Keys.IS_RED,
                viewModel.filterChoiceSelected.value!!.isPriority_red
            )
            putBooleanSharedPreference(
                Keys.IS_GREEN,
                viewModel.filterChoiceSelected.value!!.isPriority_green
            )
            putBooleanSharedPreference(
                Keys.IS_YELLOW,
                viewModel.filterChoiceSelected.value!!.isPriority_yellow
            )
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeToolBar() {
        val toolbar: androidx.appcompat.widget.Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.setup(requireActivity(), Constants.HOME_FRAGMENT_TITLE)


    }


    override fun onSortOptionSelected(sortValues: SortValues, sortBy: SortBy) {

        viewModel.setCurrentSortOptions(sortValues)
        updateAppBarText(sortValues)
        viewModel.setSortBy(sortBy)
        viewModel.filterList = viewModel.filterListByChoice(
            viewModel.oldMyNotes,
            viewModel.filterChoiceSelected.value!!
        )
        viewModel.displayList =
            viewModel.sortChoiceByList(viewModel.filterList, viewModel.sortBy, sortValues)
        adapter.setData(viewModel.displayList.toPresenterList())
        binding.recyclerView.smoothScrollToPosition(0)
    }


    // FILTER OPTION SELECTED Interface Implementation

    override fun onFilterClickDone(choice: FilterChoiceSelected) {
        viewModel.setFilterChoiceSelected(choice)
        viewModel.filterList = viewModel.filterListByChoice(
            viewModel.oldMyNotes,
            viewModel.filterChoiceSelected.value!!
        )
        if (viewModel.oldMyNotes.isNotEmpty() && viewModel.filterList.isEmpty()) {
            binding.filterEmptyImage.visibility = View.VISIBLE
            binding.filterEmptyText.visibility = View.VISIBLE
        } else {
            binding.filterEmptyImage.visibility = View.GONE
            binding.filterEmptyText.visibility = View.GONE
        }

        adapter.setData(viewModel.filterList.toPresenterList())
    }


    override fun onFilterClear() {
        viewModel.setFilterChoiceSelected(
            FilterChoiceSelected(
                isFavorite = false,
                isPriority_red = false, isPriority_yellow = false,
                isPriority_green = false
            )
        )
        notesListener()
    }


    // NotesListener Interface Implementation


    override fun onClick(pos: Int) {
        val holder: RecyclerView.ViewHolder =
            binding.recyclerView.findViewHolderForAdapterPosition(pos)!!
        if (isEnable()) {
            clickItem(holder, UiNotesMapper.viewToDomain(adapter.getNotesAtPosition(pos)))
        } else {
            onClicked(UiNotesMapper.viewToDomain(adapter.getNotesAtPosition(pos)))
        }
    }


    private fun contextualActionMode() {
        setTrueContextualActionMode()

        val callback = object : ActionMode.Callback {

            override fun onCreateActionMode(
                mode: ActionMode?,
                menu: Menu?
            ): Boolean {
                val menuInflater: MenuInflater = mode!!.menuInflater
                menuInflater.inflate(R.menu.muliptle_delete, menu)
                return true
            }

            override fun onPrepareActionMode(
                mode: ActionMode?,
                menu: Menu?
            ): Boolean {
                viewModel.setIsEnable(true)

                viewModel.text.observe(viewLifecycleOwner) {
                    mode?.title = "$it Selected"
                }

                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onActionItemClicked(
                mode: ActionMode?,
                item: MenuItem?
            ): Boolean {
                when (item!!.itemId) {
                    R.id.menu_md_delete -> {

                        if (viewModel.selectList.isEmpty()) {
                            Toast.makeText(
                                context,
                                getString(R.string.no_selection_made),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val builder: AlertDialog.Builder =
                                AlertDialog.Builder(context)
                            builder.setTitle(getString(R.string.areyoursure))
                                .setMessage(getString(R.string.deletenote))
                                .setPositiveButton(
                                    getString(R.string.yes)

                                ) { _, _ ->

                                    viewModel.selectList.forEach { note ->
                                        deleteNote(note.id)
                                        deleteFile(
                                            note.attachments
                                        )
                                    }

                                    mode!!.finish()

                                }
                                .setNegativeButton(getString(R.string.no))
                                { _, _ ->
                                    mode!!.finish()
                                }
                            builder.create().apply {
                                show()
                                getButton(DialogInterface.BUTTON_NEGATIVE)
                                    .setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.color_primary
                                        )
                                    )
                                getButton(DialogInterface.BUTTON_POSITIVE)
                                    .setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.color_primary
                                        )
                                    )
                            }

                        }


                    }
                    R.id.menu_md_selectAll -> {
                        if (viewModel.selectList.size == adapter.getNoteListSize()) {
                            adapter.setIsAllSelected(false)
                            viewModel.clearSelectedList()
                            viewModel.setText(viewModel.selectList.size.toString())


                        } else {
                            adapter.setIsAllSelected(true)
                            viewModel.clearSelectedList()
                            viewModel.addListToSelectedList(adapter.note.toDomainList())
                            viewModel.setText(viewModel.selectList.size.toString())


                        }
                        adapter.notifyDataSetChanged()

                    }
                }
                return true

            }


            override fun onDestroyActionMode(mode: ActionMode?) {
                contextualActionModeDisabled()
                viewModel.clearSelectedList()
                adapter.setIsAllSelected(false)
                viewModel.setContextualActive(false)
            }


        }
        requireActivity().startActionMode(callback)
    }

    private fun deleteFile(attachments: ArrayList<Attachment>) {

        attachments.map {
            it.name
        }.forEach {
            Storage.deletePhotoFromInternalStorage(it, requireContext())
        }

    }

    override fun onLongClicked(pos: Int) {
        viewModel.setContextualActive(true)
        setTrueContextualActionMode()
        val holder: RecyclerView.ViewHolder =
            binding.recyclerView.findViewHolderForAdapterPosition(pos)!!
        if (holder is NotesRVViewHolder
        )
            if (!viewModel.isEnable) {
                val callback = object : ActionMode.Callback {

                    override fun onCreateActionMode(
                        mode: ActionMode?,
                        menu: Menu?
                    ): Boolean {
                        val menuInflater: MenuInflater = mode!!.menuInflater
                        menuInflater.inflate(R.menu.muliptle_delete, menu)
                        return true
                    }

                    override fun onPrepareActionMode(
                        mode: ActionMode?,
                        menu: Menu?
                    ): Boolean {
                        viewModel.setIsEnable(true)
                        clickItem(
                            holder,
                            UiNotesMapper.viewToDomain(adapter.getNotesAtPosition(holder.adapterPosition))
                        )
                        viewModel.text.observeForever {
                            mode?.title = "$it Selected"
                        }

                        return true
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onActionItemClicked(
                        mode: ActionMode?,
                        item: MenuItem?
                    ): Boolean {
                        when (item!!.itemId) {
                            R.id.menu_md_delete -> {

                                if (viewModel.selectList.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        getString(R.string.no_selection_made),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val builder: AlertDialog.Builder =
                                        AlertDialog.Builder(context)
                                    builder.setTitle(getString(R.string.are_your_sure))
                                        .setMessage(getString(R.string.deletenote))
                                        .setPositiveButton(
                                            getString(R.string.yes)

                                        ) { _, _ ->

                                            viewModel.selectList.forEach { note ->

                                                deleteNote(note.id)
                                                deleteFile(
                                                    note.attachments
                                                )
                                            }

                                            mode!!.finish()

                                        }
                                        .setNegativeButton(getString(R.string.no))
                                        { _, _ ->
                                            mode!!.finish()
                                        }
                                    builder.create().apply {
                                        show()
                                        getButton(DialogInterface.BUTTON_NEGATIVE)
                                            .setTextColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.color_primary
                                                )
                                            )
                                        getButton(DialogInterface.BUTTON_POSITIVE)
                                            .setTextColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.color_primary
                                                )
                                            )
                                    }

                                }


                            }
                            R.id.menu_md_selectAll -> {
                                if (viewModel.selectList.size == adapter.getNoteListSize()) {
                                    adapter.setIsAllSelected(false)
                                    viewModel.clearSelectedList()
                                    viewModel.setText(viewModel.selectList.size.toString())


                                } else {
                                    adapter.setIsAllSelected(true)
                                    viewModel.clearSelectedList()
                                    viewModel.addListToSelectedList(adapter.note.toDomainList())
                                    viewModel.setText(viewModel.selectList.size.toString())

                                }
                                adapter.notifyDataSetChanged()
                            }
                        }
                        return true

                    }


                    override fun onDestroyActionMode(mode: ActionMode?) {
                        holder.itemView.foreground = null
                        contextualActionModeDisabled()
                        viewModel.clearSelectedList()
                        adapter.setIsAllSelected(false)
                        viewModel.setContextualActive(false)

                    }


                }
                requireActivity().startActionMode(callback)

            } else {
                clickItem(
                    holder,
                    UiNotesMapper.viewToDomain(adapter.getNotesAtPosition(holder.adapterPosition))
                )
                setText()
            }


    }

    override fun getSelectedNote(): List<Note> {
        return viewModel.selectList
    }


    // Class Function


    private fun onClicked(notes: Note) {
        navigationOnFragment(NotePreviewFragment.newInstance(notes), BackStack.PREVIEW)
    }


    private fun deleteNote(id: Int) {
        viewModel.deleteNote(id, (requireActivity() as NoteScreen).getUserID())

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun contextualActionModeDisabled() {

        viewModel.setIsEnable(false)
        adapter.notifyDataSetChanged()
        setContextualFalse()

    }

    private fun isEnable(): Boolean {
        return viewModel.isEnable
    }


    private fun addNoteToSelectedList(notes: Note) {
        viewModel.addSelectList(notes)
    }

    private fun removeNoteFromSelectedList(notes: Note) {
        viewModel.removeFromSelectedList(notes)
    }

    private fun setText() {
        viewModel.setText(viewModel.selectList.size.toString())
    }


    private fun clickItem(holder: RecyclerView.ViewHolder, note: Note) {
        if (holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility == View.GONE) {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.VISIBLE
            holder.itemView.foreground = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.foreground_selected_note
            )
            addNoteToSelectedList(note)


        } else {
            holder.itemView.findViewById<ImageView>(R.id.checkbox).visibility = View.GONE
            holder.itemView.foreground = null
            removeNoteFromSelectedList(note)

        }
        setText()
    }


    private fun setContextualFalse() {
        viewModel.setContextual(false)
    }

    private fun setTrueContextualActionMode() {
        viewModel.setContextual(true)
    }


    private fun retrievePreference() {
        SharedPreference(requireActivity()).apply {
            viewModel.setCurrentMode(getSharedPreferenceInt(Keys.CURRENT_MODE))
            val fav = getBooleanSharedPreference(Keys.IS_FAVORITE)
            val red = getBooleanSharedPreference(Keys.IS_RED)
            val yellow = getBooleanSharedPreference(Keys.IS_YELLOW)
            val green = getBooleanSharedPreference(Keys.IS_GREEN)
            viewModel.setFilterChoiceSelected(FilterChoiceSelected(fav, red, yellow, green))
            when (getSharedPreferenceString(Keys.CURRENT_SORT_OPTION)) {
                SortValues.ALPHABETICALLY_TITLE.toString() -> {
                    viewModel.setCurrentSortOptions(SortValues.ALPHABETICALLY_TITLE)

                }
                SortValues.ALPHABETICALLY_SUBTITLE.toString() -> {
                    viewModel.setCurrentSortOptions(SortValues.ALPHABETICALLY_SUBTITLE)

                }
                SortValues.MODIFICATION_DATE.toString() -> {

                    viewModel.setCurrentSortOptions(SortValues.CREATION_DATE)
                }
                SortValues.PRIORITY.toString() -> {
                    viewModel.setCurrentSortOptions(SortValues.PRIORITY)
                }
                SortValues.CREATION_DATE.toString() -> {
                    viewModel.setCurrentSortOptions(SortValues.CREATION_DATE)

                }

            }

            when (getSharedPreferenceString(Keys.SORT_ORDER)) {
                SortBy.ASCENDING.toString() -> {
                    viewModel.setSortBy(SortBy.ASCENDING)
                }
                SortBy.DESCENDING.toString() -> {
                    viewModel.setSortBy(SortBy.DESCENDING)
                }
            }

        }


    }

    private fun List<Note>.toPresenterList(): List<NotesRvItem.UINotes> {

        val list = mutableListOf<NotesRvItem.UINotes>()
        forEach {

            list.add(UiNotesMapper.mapToView(it))

        }
        return list.toMutableList()

    }

    private fun List<NotesRvItem.UINotes>.toDomainList(): List<Note> {

        val list = mutableListOf<Note>()

        forEach {

            list.add(it.note)


        }
        return list.toMutableList()


    }

    override fun onDetach() {
        super.onDetach()
        fragmentNavigationLisenter = null
        settingsLisenter = null
    }

    private fun backPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()

                }
            })
    }
}


