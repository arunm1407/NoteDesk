package com.example.notedesk.presentation.home


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.domain.usecase.FilterList.filterListByChoice
import com.example.notedesk.domain.usecase.SortList.sortList
import com.example.notedesk.domain.util.keys.Constants
import com.example.notedesk.domain.util.keys.Keys
import com.example.notedesk.domain.util.keys.Keys.BACKGROUND_COLOR
import com.example.notedesk.domain.util.keys.Keys.CURRENT_MODE
import com.example.notedesk.domain.util.keys.Keys.CURRENT_SORT_OPTION
import com.example.notedesk.domain.util.keys.Keys.IS_FAVORITE
import com.example.notedesk.domain.util.keys.Keys.IS_GREEN
import com.example.notedesk.domain.util.keys.Keys.IS_RED
import com.example.notedesk.domain.util.keys.Keys.IS_YELLOW
import com.example.notedesk.domain.util.keys.Keys.LIST_VIEW
import com.example.notedesk.domain.util.keys.Keys.SORT_ORDER
import com.example.notedesk.domain.util.keys.Keys.STAGGERED_GRID
import com.example.notedesk.domain.util.sharedPreference.SharedPreference
import com.example.notedesk.presentation.createNote.CreateNotesFragment
import com.example.notedesk.presentation.home.Listener.*
import com.example.notedesk.presentation.home.adptor.NotesAdaptor
import com.example.notedesk.presentation.home.dailog.FilterDailog
import com.example.notedesk.presentation.home.dailog.SortDialogFragment
import com.example.notedesk.presentation.policy.PolicyFragment
import com.example.notedesk.presentation.previewNote.PreviewFragment
import com.example.notedesk.presentation.activity.NotesRVViewHolder
import com.example.notedesk.presentation.home.enums.*
import com.example.notedesk.presentation.search.SearchFragment
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.presentation.util.getSelectedCount
import com.example.notesappfragment.R
import com.example.notesappfragment.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment(), SortLisenter, FilterChoiceLisenter, NotesListener {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NotesAdaptor
    private val viewModel: HomeViewModel by viewModels()
    private var oldMyNotes = arrayListOf<Notes>()
    private var filterChoiceSelected: FilterChoiceSelected? = null
    private lateinit var sortedList: List<Notes>
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    private var filterList = listOf<Notes>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        retrievePreference()
        if (context is FragmentNavigationLisenter) {
            viewModel.fragmentNavigationLisenter = context
        }
        if (context is SettingsLisenter) {
            viewModel.settingsLisenter = context

        }
    }


    private fun retrievePreference() {
        SharedPreference(requireActivity()).apply {
            viewModel.currentMode = getSharedPreferenceInt(CURRENT_MODE)
            val fav = getBooleanSharedPreference(IS_FAVORITE)
            val red = getBooleanSharedPreference(IS_RED)
            val yellow = getBooleanSharedPreference(IS_YELLOW)
            val green = getBooleanSharedPreference(IS_GREEN)
            filterChoiceSelected = FilterChoiceSelected(fav, red, yellow, green)
            viewModel.filterSelectedCount.value = filterChoiceSelected!!.getSelectedCount()
            when (getSharedPreferenceString(CURRENT_SORT_OPTION)) {
                SortValues.ALPHABETICALLY_TITLE.toString() -> {
                    viewModel.currentSortOptions = SortValues.ALPHABETICALLY_TITLE

                }
                SortValues.ALPHABETICALLY_SUBTITLE.toString() -> {
                    viewModel.currentSortOptions = SortValues.ALPHABETICALLY_SUBTITLE

                }
                SortValues.MODIFICATION_DATE.toString() -> {
                    viewModel.currentSortOptions = SortValues.MODIFICATION_DATE

                }
                SortValues.PRIORITY.toString() -> {
                    viewModel.currentSortOptions = SortValues.PRIORITY
                }
                SortValues.CREATION_DATE.toString() -> {
                    viewModel.currentSortOptions = SortValues.CREATION_DATE

                }

            }

            when (getSharedPreferenceString(SORT_ORDER)) {
                SortBy.ASCENDING.toString() -> {
                    viewModel.sortBy = SortBy.ASCENDING
                }
                SortBy.DESCENDING.toString() -> {
                    viewModel.sortBy = SortBy.DESCENDING
                }
            }

        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colorDrawableBackground = ColorDrawable(Color.parseColor(BACKGROUND_COLOR))
        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.avd_delete)!!
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root


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
                    viewModel.settingsLisenter?.settings()
                }
                R.id.menu_privacy -> {
                    navigationOnFragment(PolicyFragment(), BackStack.POLICY)
                }
            }
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }
        animateNavigationDrawer()
    }

    private fun navigateFragment(action: MenuActions) {
        navigationOnFragment(CreateNotesFragment.newInstance(null, action), BackStack.CREATE)
    }

    private fun navigationOnFragment(fragment: Fragment, name: String) {
        viewModel.fragmentNavigationLisenter?.navigate(fragment, name)
    }


    private fun displayNotes(list: List<Notes>) {
        if (viewModel.currentMode == LIST_VIEW) {
            setAdaptor(null, LinearLayoutManager(requireContext()), true, list)
            activity

        } else if (viewModel.currentMode == STAGGERED_GRID) {
            setAdaptor(
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL),
                null,
                false,
                list
            )
        }


    }

    private fun updateRecyclerViewGrid(currentMode: Int) {
        if (currentMode == LIST_VIEW) {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        } else if (currentMode == STAGGERED_GRID) {
            binding.recyclerView.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }


    private fun setAdaptor(
        staggeredGridLayoutManager: StaggeredGridLayoutManager?,
        linearLayoutManager: LinearLayoutManager?,
        isLinear: Boolean,
        list: List<Notes>
    ) {
        if (isLinear) {
            binding.recyclerView.layoutManager = linearLayoutManager
        } else {
            binding.recyclerView.layoutManager = staggeredGridLayoutManager
        }
        adapter =
            NotesAdaptor(
                requireActivity(),
                list, this
            )



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
                    Log.i(
                        "item",
                        "${itemView.left + iconMarginVertical}   ${itemView.top + iconMarginVertical}   ${itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth}    ${itemView.bottom - iconMarginVertical}"
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

                val deletedNotes: Notes = oldMyNotes[viewHolder.adapterPosition]
                val position = viewHolder.adapterPosition
                oldMyNotes.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                Snackbar.make(
                    binding.root, "Note   ${deletedNotes.title} Deleted ", Snackbar.LENGTH_SHORT
                )

                    .apply {
                        anchorView = binding.floatingActionAddNotesBtn
                        setAction("UNDO")
                        {


                            oldMyNotes.add(position, deletedNotes)
                            lifecycleScope.launch(Dispatchers.IO)
                            {
                                viewModel.addNotes(deletedNotes)
                            }

                            adapter.notifyItemInserted(position)
                        }

                    }.show()
                viewModel.deleteNote(deletedNotes.id)

            }

        }
        ).attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.adapter = adapter


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeView()
        updateAppBarText(viewModel.currentSortOptions)
        observeFilterText()
        notesListener()
        actionBarListener()
        navigationLisenter()
        contextualObserver()
        initializeMenu()
        backPressed()
        setActionOnViews()

    }

    private fun initializeView() {
        initializeToolBar()
        initializeDrawerAndAction()
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
        viewModel.filterSelectedCount.observe(viewLifecycleOwner)
        { count ->

            if (count > 0) {
                binding.sort.btnFilterText.text = resources.getString(R.string.selectedCount, count)
                binding.sort.btnFilterText.visibility = View.VISIBLE


            } else {
                binding.sort.btnFilterText.visibility = View.GONE
            }


        }

    }


    private fun actionBarListener() {

        binding.sort.sort.setOnClickListener()
        {

            displaySortDailog()


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
                        viewModel.currentMode = if (viewModel.currentMode == LIST_VIEW) {
                            menuItem.setIcon(R.drawable.ic_view_list)
                            STAGGERED_GRID

                        } else {
                            menuItem.setIcon(R.drawable.ic_view_grid)
                            LIST_VIEW

                        }
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


    private fun backPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()

                }
            })
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
        viewModel.getNotes().observe(viewLifecycleOwner) {

            if (it.isEmpty()) {
                binding.imageEmpty.visibility = View.VISIBLE
                binding.textEmpty.visibility = View.VISIBLE
                viewModel.listEmpty = true
            } else {
                binding.imageEmpty.visibility = View.GONE
                binding.textEmpty.visibility = View.GONE

            }

            if (it.isNotEmpty()) {
                binding.sort.root.visibility = View.VISIBLE

            } else {
                binding.sort.root.visibility = View.GONE

            }

            filterList = it
            oldMyNotes = it as ArrayList<Notes>

            sortedList = sortList(
                viewModel.currentSortOptions,
                viewModel.sortBy,
                filterListByChoice(filterList, filterChoiceSelected!!)
            )
            if (sortedList.isEmpty() && it.isNotEmpty()) {
                binding.filterEmptyImage.visibility = View.VISIBLE
                binding.filterEmptyText.visibility = View.VISIBLE
            } else {
                binding.filterEmptyImage.visibility = View.GONE
                binding.filterEmptyText.visibility = View.GONE
            }
            displayNotes(sortedList)


        }

    }


    override fun onStop() {
        super.onStop()
        contextualActionModeDisabled()
        savePreference()
    }


    private fun displaySortDailog(): Boolean {
        val dialog = SortDialogFragment.newInstance(viewModel.currentSortOptions, viewModel.sortBy)
        dialog.show(childFragmentManager, "3")
        return true
    }


    private fun displayFilterDailog(): Boolean {
        val dialog = FilterDailog.newInstance(filterChoiceSelected!!)
        dialog.show(childFragmentManager, "3")
        return true
    }


    private fun savePreference() {
        SharedPreference(requireActivity()).apply {

            putIntSharePreferenceInt(CURRENT_MODE, viewModel.currentMode)
            putStringSharedPreference(SORT_ORDER, viewModel.sortBy.toString())
            putStringSharedPreference(CURRENT_SORT_OPTION, viewModel.currentSortOptions.toString())
            putBooleanSharedPreference(IS_FAVORITE, filterChoiceSelected!!.isFavorite)
            putBooleanSharedPreference(IS_RED, filterChoiceSelected!!.isPriority_red)
            putBooleanSharedPreference(IS_GREEN, filterChoiceSelected!!.isPriority_green)
            putBooleanSharedPreference(IS_YELLOW, filterChoiceSelected!!.isPriority_yellow)
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeToolBar() {
        val toolbar: androidx.appcompat.widget.Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.title = Constants.HOME_FRAGMENT_TITLE
        (activity as AppCompatActivity).apply {
            this.setSupportActionBar(toolbar)

            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)

        }
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_arrow_back_24)


    }


    // SORT OPTION SELECTED Interface Implementation


    override fun onSortOptionSelected(sortValues: SortValues, sortBy: SortBy) {
        viewModel.currentSortOptions = sortValues
        updateAppBarText(sortValues)
        viewModel.sortBy = sortBy
        sortedList = sortList(sortValues, viewModel.sortBy, oldMyNotes)
        adapter.setData(sortedList)
    }


    // FILTER OPTION SELECTED Interface Implementation

    override fun onFilterClickDone(choice: FilterChoiceSelected) {
        filterChoiceSelected = choice
        viewModel.filterSelectedCount.value = filterChoiceSelected!!.getSelectedCount()
        oldMyNotes = filterListByChoice(filterList, choice)
        if (filterList.isNotEmpty() && oldMyNotes.isEmpty()) {
            binding.filterEmptyImage.visibility = View.VISIBLE
            binding.filterEmptyText.visibility = View.VISIBLE
        } else {
            binding.filterEmptyImage.visibility = View.GONE
            binding.filterEmptyText.visibility = View.GONE
        }
        adapter.setData(oldMyNotes)
    }


    override fun onFilterClear() {

        filterChoiceSelected = FilterChoiceSelected(
            isFavorite = false,
            isPriority_red = false, isPriority_yellow = false,
            isPriority_green = false
        )
        viewModel.filterSelectedCount.value = filterChoiceSelected!!.getSelectedCount()
        notesListener()
    }


    // NotesListener Interface Implementation


    override fun onClick(holder: NotesRVViewHolder.NotesViewHolder, data: Notes) {
        if (isEnable()) {
            clickItem(holder, data)
        } else {
            onClicked(data)
        }
    }

    override fun onLongClicked(holder: RecyclerView.ViewHolder, data: Notes) {
        setTrueContextualActionMode()
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
                        viewModel.isEnable = true
                        clickItem(holder, data)
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
                                        "No Selection is made",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val builder: AlertDialog.Builder =
                                        AlertDialog.Builder(context)
                                    builder.setTitle("Are you sure?")
                                        .setMessage("Do you want to Delete the Notes ?")
                                        .setPositiveButton(
                                            "Yes"

                                        ) { _, _ ->

                                            viewModel.selectList.forEach { note ->

                                                deleteNote(note.id)
                                                deleteFile(note.id)
                                            }

                                            mode!!.finish()

                                        }
                                        .setNegativeButton("No")
                                        { _, _ ->
                                            mode!!.finish()
                                        }
                                    val alert: AlertDialog = builder.create()
                                    alert.show()
                                }


                            }
                            R.id.menu_md_selectAll -> {
                                if (viewModel.selectList.size == adapter.notes.size) {
                                    viewModel.isSelectAll = false
                                    viewModel.selectList.clear()
                                    viewModel.text.value = "${viewModel.selectList.size}"


                                } else {
                                    viewModel.isSelectAll = true
                                    viewModel.selectList.clear()
                                    viewModel.selectList.addAll(adapter.notes)
                                    viewModel.text.value = "${viewModel.selectList.size}"

                                }
                                adapter.notifyDataSetChanged()
                            }
                        }
                        return true

                    }


                    override fun onDestroyActionMode(mode: ActionMode?) {
                        holder.itemView.foreground = null
                        contextualActionModeDisabled()

                    }


                }
                requireActivity().startActionMode(callback)

            } else {
                clickItem(holder, data)
                setText()
            }


    }


    override fun isAllSelected(): Boolean {
        return viewModel.isSelectAll
    }


    // Class Function


    private fun onClicked(notes: Notes) {
        navigationOnFragment(PreviewFragment.newInstance(notes), BackStack.PREVIEW)
    }


    private fun deleteNote(id: Int) {
        viewModel.deleteNote(id)
    }

    private fun deleteFile(id: Int) {

        lifecycleScope.launch(Dispatchers.IO)
        {
            withContext(Dispatchers.IO) {
                viewModel.deleteFile(id)
            }
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun contextualActionModeDisabled() {

        viewModel.isEnable = false
        viewModel.isSelectAll = false
        viewModel.selectList.clear()
        adapter.notifyDataSetChanged()
        setContextualFalse()
    }

    private fun isEnable(): Boolean {
        return viewModel.isEnable
    }


    private fun addNoteToSelectedList(notes: Notes) {
        viewModel.selectList.add(notes)
    }

    private fun removeNoteFromSelectedList(notes: Notes) {
        viewModel.selectList.remove(notes)
    }

    private fun setText() {
        viewModel.text.value = "${viewModel.selectList.size}"
    }


    private fun clickItem(holder: NotesRVViewHolder, note: Notes) {
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
        viewModel.contextual.value = false
    }

    private fun setTrueContextualActionMode() {
        viewModel.contextual.value = true
    }


}
