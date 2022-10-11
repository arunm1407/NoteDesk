package com.example.notedesk.presentation.search

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notedesk.domain.model.Note
import com.example.notedesk.presentation.model.NotesRvItem
import com.example.notedesk.domain.usecase.FilterList
import com.example.notedesk.domain.usecase.SortList
import com.example.notedesk.presentation.activity.MainActivity
import com.example.notedesk.util.keys.Keys.RECENT_SEARCH
import com.example.notedesk.util.keys.Keys.SEARCH
import com.example.notedesk.util.keys.Keys.SEARCH_SUGGESTION
import com.example.notedesk.presentation.home.listener.FilterChoiceLisenter
import com.example.notedesk.presentation.home.listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.home.listener.SortLisenter
import com.example.notedesk.presentation.home.dailog.FilterDailog
import com.example.notedesk.presentation.home.dailog.SortDialogFragment
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import com.example.notedesk.presentation.previewNote.PreviewFragment
import com.example.notedesk.presentation.search.adaptor.SearchViewAdaptor
import com.example.notedesk.presentation.search.listner.SuggestionLisenter
import com.example.notedesk.presentation.util.*
import com.example.notedesk.R
import com.example.notedesk.databinding.FragmentSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchFragment : Fragment(), SuggestionLisenter, SortLisenter,
    FilterChoiceLisenter {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchView: SearchView
    private lateinit var adaptor: SearchViewAdaptor
    private lateinit var dialog: DialogFragment
    private var fragmentNavigationLisenter: FragmentNavigationLisenter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentNavigationLisenter) {
            fragmentNavigationLisenter = context
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeFilterChoice()


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        fetchData()
        return binding.root
    }


    private fun fetchData() {

        lifecycleScope.launch()
        {
            viewModel.getNotes((requireActivity() as MainActivity).getUserID())
                .observe(viewLifecycleOwner)
                {
                    viewModel.oldMyNotes = it
                    viewModel.filterList = it
                    viewModel.displayList = it

                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdaptor()
        initializeToolBar()
        observeFilterText()
        actionBarListener()
        initializeMenu()


    }


    private fun initializeFilterChoice() {
        viewModel.setFilterChoiceSelected(
            FilterChoiceSelected(
                isFavorite = false,
                isPriority_red = false,
                isPriority_yellow = false,
                isPriority_green = false
            )
        )
    }

    private fun initializeMenu() {


        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {


            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.search, menu)
                val item = menu.findItem(R.id.search)
                searchView = item.actionView as SearchView
                item.expandActionView()
                searchView.setIconifiedByDefault(true)
                searchView.maxWidth = Integer.MAX_VALUE
                searchView.setQuery(viewModel.searchQuery, false)
                item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                        return true
                    }

                    override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                        searchView.hideKeyboard()
                        requireActivity().supportFragmentManager.popBackStack()
                        return true
                    }

                })
                searchView.queryHint = SEARCH
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {


                    override fun onQueryTextSubmit(query: String): Boolean {


                        return false

                    }

                    override fun onQueryTextChange(p0: String): Boolean {

                        if (p0 != "") {
                            viewModel.setSearchQuery(p0)

                        }
                        notesFiltering(p0)
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }


    private fun setAdaptor() {
        val list = mutableListOf<NotesRvItem>()
        lifecycleScope.launch {
            withContext(Dispatchers.IO)
            {
                val suggestion =
                    viewModel.getSuggestion((requireActivity() as MainActivity).getUserID())
                if (suggestion.isNotEmpty()) {
                    list.add(NotesRvItem.Title(1, SEARCH_SUGGESTION))
                    suggestion.forEach {
                        list.add(NotesRvItem.Suggestion((it)))
                    }
                }
            }
            adaptor = SearchViewAdaptor(list, this@SearchFragment)
            binding.rvSuggestions.initRecyclerView(
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                ), adaptor, false
            )


        }


    }


    private val initializeToolBar = {
        val toolbar: Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.setup(requireActivity(), "")


    }


    private fun notesFiltering(p0: String) {
        binding.searchText.visibility = View.GONE
        binding.searchImage.visibility = View.GONE
        val newFilteredList = mutableListOf<Note>()
        val list = mutableListOf<NotesRvItem>()

        if (p0 != "") {
            binding.searchText.visibility = View.GONE
            binding.ivSearchIllustration.visibility = View.GONE
            binding.tvScreenInfo.visibility = View.GONE
            binding.searchImage.visibility = View.GONE
            for (i in viewModel.displayList) {
                if (i.title.contains(p0, true) || i.subtitle.contains(
                        p0,
                        true
                    ) || i.noteText.contains(
                        p0,
                        true
                    )
                ) {
                    newFilteredList.add(i)

                }


            }
            updateList(list, newFilteredList, p0)
        } else {
            binding.searchText.visibility = View.VISIBLE
            binding.searchImage.visibility = View.VISIBLE
            binding.ivSearchIllustration.visibility = View.GONE
            binding.tvScreenInfo.visibility = View.GONE
            updateList(mutableListOf(), listOf(), p0)

            lifecycleScope.launch()
            {

                withContext(Dispatchers.IO)
                {

                    val suggestion =
                        viewModel.getSuggestion((requireActivity() as MainActivity).getUserID())
                    if (suggestion.isNotEmpty()) {
                        list.add(NotesRvItem.Title(1, SEARCH_SUGGESTION))
                        suggestion.forEach {
                            list.add(NotesRvItem.Suggestion((it)))
                        }

                    }

                    launch(Dispatchers.Main) {
                        updateList(list, newFilteredList, p0)
                    }

                }


            }

        }


    }


    private fun updateList(
        list: MutableList<NotesRvItem>,
        newFilteredList: List<Note>,
        p0: String
    ) {
        if (newFilteredList.isEmpty() && p0 != "") {
            binding.tvScreenInfo.visibility = View.VISIBLE
            binding.ivSearchIllustration.visibility = View.VISIBLE
        } else if (newFilteredList.isNotEmpty()) {
            binding.tvScreenInfo.visibility = View.GONE
            binding.ivSearchIllustration.visibility = View.GONE

        }
        if (newFilteredList.isNotEmpty())
            list.add(NotesRvItem.Title(2, RECENT_SEARCH))

        list.addAll(newFilteredList)
        adaptor.setData(list)
    }

    private fun navigationOnFragment(fragment: Fragment) {
        fragmentNavigationLisenter?.navigate(fragment, BackStack.PREVIEW)
    }


    private fun displaySortDailog(): Boolean {
        dialog =
            SortDialogFragment.newInstance(viewModel.currentSortOptions, viewModel.sortBy)
        dialog.show(childFragmentManager, "3")
        return true
    }


    private fun displayFilterDailog(): Boolean {
        dialog = FilterDailog.newInstance(viewModel.filterChoiceSelected.value!!)
        dialog.show(childFragmentManager, "3")
        return true
    }


    override fun addSuggestion(name: String) {
        viewModel.deleteSearchHistory(name, (requireActivity() as MainActivity).getUserID())
        viewModel.addSuggestion(name, (requireActivity() as MainActivity).getUserID())

    }

    override fun onSuggestionClicked(name: String) {
        searchView.setQuery(name, false)
    }

    override fun deleteSearchHistory(name: String, position: Int) {
        viewModel.deleteSearchHistory(name, (requireActivity() as MainActivity).getUserID())
        adaptor.removeItemFromList(position)


    }

    override fun onClickedNote(notes: NotesRvItem.UNotes) {
        navigationOnFragment(PreviewFragment.newInstance(notes.note))
    }

    override fun onFilterClickDone(choice: FilterChoiceSelected) {
        viewModel.setFilterChoiceSelected(choice)
        viewModel.displayList = FilterList.filterListByChoice(viewModel.filterList, choice)
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

    override fun onFilterClear() {
        viewModel.setFilterChoiceSelected(
            FilterChoiceSelected(
                isFavorite = false,
                isPriority_red = false, isPriority_yellow = false,
                isPriority_green = false
            )
        )
        fetchData()

    }

    override fun onSortOptionSelected(sortValues: SortValues, sortBy: SortBy) {

        viewModel.currentSortOptions = sortValues
        updateAppBarText(sortValues)
        viewModel.sortBy = sortBy
        viewModel.filterList =
            FilterList.filterListByChoice(
                viewModel.oldMyNotes,
                viewModel.filterChoiceSelected.value!!
            )
        viewModel.displayList =
            SortList.sortList(sortValues, viewModel.sortBy, viewModel.filterList)

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
        { selectedFilterCount ->

            if (selectedFilterCount.getSelectedCount() > 0) {
                binding.sort.btnFilterText.text =
                    resources.getString(
                        R.string.selectedCount,
                        selectedFilterCount.getSelectedCount()
                    )
                binding.sort.btnFilterText.visibility = View.VISIBLE
            } else {
                binding.sort.btnFilterText.visibility = View.GONE
            }


        }

    }


    override fun onDetach() {
        super.onDetach()
        fragmentNavigationLisenter = null
    }


    override fun onDestroyView() {
        viewModel.setSearchQuery("")
        super.onDestroyView()
    }


}








