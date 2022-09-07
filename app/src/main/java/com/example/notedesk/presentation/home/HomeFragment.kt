package com.example.notesappfragment.features.presentation.home


import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
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
import com.example.notedesk.domain.util.keys.IndentKeys
import com.example.notedesk.domain.util.keys.IndentKeys.CURRENT_MODE
import com.example.notedesk.domain.util.keys.IndentKeys.CURRENT_SORT_OPTION
import com.example.notedesk.domain.util.keys.IndentKeys.LIST_VIEW
import com.example.notedesk.domain.util.keys.IndentKeys.SORT_ORDER
import com.example.notedesk.domain.util.keys.IndentKeys.STAGGERED_GRID
import com.example.notedesk.domain.util.sharedPreference.SharedPreference
import com.example.notedesk.presentation.createNote.CreateNotesFragment
import com.example.notedesk.presentation.home.HomeViewModel
import com.example.notedesk.presentation.home.Listener.FilterChoiceLisenter
import com.example.notedesk.presentation.home.Listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.home.Listener.SettingsLisenter
import com.example.notedesk.presentation.home.Listener.SortLisenter
import com.example.notedesk.presentation.home.adptor.Notesadaptor
import com.example.notedesk.presentation.home.dailog.FilterDailog
import com.example.notedesk.presentation.home.dailog.SortDialogFragment
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected
import com.example.notedesk.presentation.home.enums.MenuActions
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import com.example.notedesk.presentation.policy.PolicyFragment
import com.example.notedesk.presentation.previewNote.PreviewFragment
import com.example.notedesk.presentation.util.BackStack
import com.example.notesappfragment.R
import com.example.notesappfragment.databinding.FragmentHomeBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), SortLisenter, FilterChoiceLisenter {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: Notesadaptor
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var dialog: DialogFragment
    private val viewModel: HomeViewModel by viewModels()
    private var currentMode: Int = LIST_VIEW
    private var currentSortOptions: SortValues = SortValues.ALPHABETICALLY_TITLE
    private var sortBy: SortBy = SortBy.DESENDING
    private var oldMyNotes = arrayListOf<Notes>()
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null
    private var settingsLisenter: SettingsLisenter? = null
    private var filterChoiceSelected: FilterChoiceSelected? = null
    private lateinit var sortedList:List<Notes>
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    private var filterList= listOf<Notes>()

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
        return binding.root


    }

    private fun retrievePreference() {

        currentMode = SharedPreference(requireActivity()).getSharedPreferenceInt(CURRENT_MODE)
        when (SharedPreference(requireActivity()).getSharedPreferenceString(CURRENT_SORT_OPTION)) {
            SortValues.ALPHABETICALLY_TITLE.toString() -> {
                currentSortOptions = SortValues.ALPHABETICALLY_TITLE

            }
            SortValues.ALPHABETICALLY_SUBTITLE.toString() -> {
                currentSortOptions = SortValues.ALPHABETICALLY_SUBTITLE

            }
            SortValues.MODIFICATION_DATE.toString() -> {
                currentSortOptions = SortValues.MODIFICATION_DATE

            }
            SortValues.PRIORITY.toString() -> {
                currentSortOptions = SortValues.PRIORITY
            }
            SortValues.CREATION_DATE.toString() -> {
                currentSortOptions = SortValues.CREATION_DATE

            }

        }
        Log.i(
            "arun",
            "${SharedPreference(requireActivity()).getSharedPreferenceString(SORT_ORDER)}"
        )
        when (SharedPreference(requireActivity()).getSharedPreferenceString(SORT_ORDER)) {
            SortBy.ASCENDING.toString() -> {
                sortBy = SortBy.ASCENDING
            }
            SortBy.DESENDING.toString() -> {
                sortBy = SortBy.DESENDING
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

            if (it.isNotEmpty()) {

                binding.sort.root.visibility=View.VISIBLE
                binding.imageEmpty.visibility = View.GONE
                binding.textEmpty.visibility = View.GONE
            } else {
                binding.sort.root.visibility=View.GONE
                binding.imageEmpty.visibility = View.VISIBLE
                binding.textEmpty.visibility = View.VISIBLE
            }







            filterList = it
            oldMyNotes = it as ArrayList<Notes>
            sortedList = sortList(currentSortOptions, sortBy, oldMyNotes)
            displayNotes(sortedList)
        }

    }

    private fun initializeView() {
        initializeToolBar()
        bottomAppBar = binding.mainBottomAppBar
        initializeDrawerAndAction()
    }

    private fun navigationLisenter() {
        binding.floatingActionAddNotesBtn.setOnClickListener {
            navigateFragment(MenuActions.NOACTION)
        }
    }


    private fun animateNavigationDrawer() {
        binding.mainDrawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val diffScaledOffset: Float =
                    slideOffset * (1 - IndentKeys.END_SCALE)
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
        toggle = ActionBarDrawerToggle(requireActivity(), binding.mainDrawerLayout, toolbar, 0, 0)
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
        fragmentNavigationLisenter?.navigate(fragment, name)
    }


    private fun setActionOnViews() {
        bottomAppBar.setOnMenuItemClickListener()
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


    private fun notesFiltering(p0: String) {
        val newFilteredList = arrayListOf<Notes>()

        for (i in oldMyNotes) {
            if (i.title.contains(p0, true) || i.subtitle.contains(p0, true) || i.noteText.contains(
                    p0,
                    true
                )
            ) {
                newFilteredList.add(i)

            }
            if (newFilteredList.isEmpty()) {
                binding.searchEmptyImage.visibility = View.VISIBLE
                binding.searchEmptyText.visibility = View.VISIBLE
                binding.textEmpty.visibility = View.GONE
                binding.imageEmpty.visibility = View.GONE
            } else {
                binding.searchEmptyImage.visibility = View.GONE
                binding.searchEmptyText.visibility = View.GONE
                binding.textEmpty.visibility = View.GONE
                binding.imageEmpty.visibility = View.GONE
            }
            adapter.filtering(newFilteredList)
        }

    }

    private fun displayNotes(list: List<Notes>) {


        if (currentMode == LIST_VIEW) {
            setAdaptor(null, LinearLayoutManager(requireContext()), true, list)
            activity

        } else if (currentMode == STAGGERED_GRID) {
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
            Notesadaptor(
                requireActivity(),
                list,
                notesSelectedLisenter,
                requireActivity(), viewModel, viewLifecycleOwner
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
                    val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                    if (dX > 0) {
                        colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                        deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                            itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                    } else {
                        colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                        deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                            itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                        deleteIcon.level = 0
                    }

                    colorDrawableBackground.draw(c)

                    c.save()

                    if (dX > 0)
                        c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    else
                        c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                    deleteIcon.draw(c)

                    c.restore()



                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }



                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val deletedNotes: Notes =
                        oldMyNotes[viewHolder.adapterPosition]
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.avd_delete)!!
        filterChoiceSelected = FilterChoiceSelected(
            isFavorite = false,
            isPriority_red = false,
            isPriority_yellow = false,

            isPriority_green = false
        )
    }



    private fun initalizeMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.search, menu)
                val item = menu.findItem(R.id.menu_Search)
                val searchView = item.actionView as SearchView
                searchView.findViewById<LinearLayout>(com.google.android.material.R.id.search_bar)
                searchView.layoutTransition = LayoutTransition()
                searchView.queryHint = "Search"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {


                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        notesFiltering(p0!!)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_List -> {
                        currentMode = if (currentMode == LIST_VIEW) {
                            menuItem.setIcon(R.drawable.ic_view_list)
                            STAGGERED_GRID

                        } else {
                            menuItem.setIcon(R.drawable.ic_view_grid)
                            LIST_VIEW

                        }
                        updateRecyclerViewGrid(currentMode)

                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun displaySortDailog(): Boolean {

        dialog = SortDialogFragment.newInstance(currentSortOptions, sortBy)
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog.show(childFragmentManager, "3")
        return true
    }


    private fun displayFilterDailog(): Boolean {
        Log.i("arun", "inside the display data $filterChoiceSelected")
        dialog = FilterDailog.newInstance(filterChoiceSelected!!)
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog.show(childFragmentManager, "3")
        return true
    }

    private fun actionBarListener() {

        binding.sort.sort.setOnClickListener()
        {
            if (2 > viewModel.getNotes().value!!.size) {
                Toast.makeText(
                    requireContext(),
                    "Notes is Not sufficient to Sort",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                displaySortDailog()
            }

        }
        binding.sort.filter.setOnClickListener()
        {
            if (2 > viewModel.getNotes().value!!.size) {

                Toast.makeText(
                    requireContext(),
                    "Notes is Not sufficient to Filter",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                displayFilterDailog()
            }
        }
    }


    private fun savePreference() {
        SharedPreference(requireActivity()).apply {
            putIntSharePreferenceInt(CURRENT_MODE, currentMode)
            putStringSharedPreference(SORT_ORDER, sortBy.toString())
            putStringSharedPreference(CURRENT_SORT_OPTION, currentSortOptions.toString())

        }
    }


    override fun onStop() {
        savePreference()
        super.onStop()
    }


    private val notesSelectedLisenter: (Notes) -> Unit = {
        parentFragmentManager.beginTransaction().apply {
            replace(
                R.id.fragmentContainerView,
                PreviewFragment.newInstance(it)
            ).addToBackStack(null)
                .commit()
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private val initializeToolBar = {
        toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.title = Constants.HOME_FRAGMENT_TITLE
        (activity as AppCompatActivity).apply {
            this.setSupportActionBar(toolbar)

            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)

        }
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeView()
        updateAppBarText(currentSortOptions)
        notesListener()
        actionBarListener()
        navigationLisenter()
        contextualObserver()
        initalizeMenu()
        setActionOnViews()
        onOptionSelected(currentSortOptions, sortBy)


    }


    override fun onOptionSelected(sortValues: SortValues, sortby: SortBy) {
        currentSortOptions = sortValues
        updateAppBarText(sortValues)

        sortBy = sortby
        val arrayList: ArrayList<Notes> = oldMyNotes
        sortedList = sortList(sortValues, sortBy, arrayList)
        displayNotes(sortedList)
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentNavigationLisenter = null
    }

    override fun onClickDone(choice: FilterChoiceSelected) {
        filterChoiceSelected = choice
        viewModel.resetFilterCount()
        if (choice.isFavorite) {
            viewModel.addFilterCount()
        }
        if (choice.isPriority_red) {
            viewModel.addFilterCount()
        }
        if (choice.isPriority_yellow) {
            viewModel.addFilterCount()
        }
        if (choice.isPriority_green) {
            viewModel.addFilterCount()
        }
        observeFilterText()
        if (viewModel.filterSelectedCount.value!! > 0) {
            oldMyNotes = filterListByChoice(filterList, choice)
            if (oldMyNotes.isEmpty()) {
                binding.imageEmpty.visibility = View.VISIBLE
                binding.textEmpty.visibility = View.VISIBLE
                displayNotes(oldMyNotes)
            } else {
                binding.imageEmpty.visibility = View.GONE
                binding.textEmpty.visibility = View.GONE
                displayNotes(oldMyNotes)
            }

        }


    }


    private fun observeFilterText() {
        viewModel.filterSelectedCount.observe(viewLifecycleOwner)
        {

            if (it > 0) {
                binding.sort.btnFilterText.text = "($it)"
                binding.sort.btnFilterText.visibility = View.VISIBLE
                Log.i("arun", "old list ${oldMyNotes.size}")


            } else {
                binding.sort.btnFilterText.visibility = View.GONE
            }


        }

    }


    private fun updateAppBarText(sortValues: SortValues) {
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

    override fun onClear() {
        notesListener()
        viewModel.resetFilterCount()
        filterChoiceSelected = FilterChoiceSelected(
            isFavorite = false,
            isPriority_red = false, isPriority_yellow = false,
            isPriority_green = false
        )
    }


}
