package com.example.notedesk.presentation.search

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.data.data_source.NotesRvItem
import com.example.notedesk.domain.usecase.FilterList
import com.example.notedesk.domain.usecase.SortList
import com.example.notedesk.domain.util.keys.Keys.RECENT_SEARCH
import com.example.notedesk.domain.util.keys.Keys.SEARCH
import com.example.notedesk.domain.util.keys.Keys.SEARCH_SUGGESTION
import com.example.notedesk.presentation.home.Listener.FilterChoiceLisenter
import com.example.notedesk.presentation.home.Listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.home.Listener.SortLisenter
import com.example.notedesk.presentation.home.dailog.FilterDailog
import com.example.notedesk.presentation.home.dailog.SortDialogFragment
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import com.example.notedesk.presentation.previewNote.PreviewFragment
import com.example.notedesk.presentation.search.adaptor.SearchViewAdaptor
import com.example.notedesk.presentation.search.listner.SuggestionLisenter
import com.example.notedesk.presentation.util.BackStack
import com.example.notedesk.presentation.util.hideKeyboard
import com.example.notedesk.presentation.util.initRecyclerView
import com.example.notesappfragment.R
import com.example.notesappfragment.databinding.FragmentSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment(), SuggestionLisenter, SortLisenter,
    FilterChoiceLisenter {


    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var adaptor: SearchViewAdaptor
    private var filterChoiceSelected: FilterChoiceSelected? = null
    private var filterList = listOf<Notes>()
    private var oldMyNotes = arrayListOf<Notes>()
    private lateinit var sortedList: List<Notes>
    private var searchNotes: List<Notes> = mutableListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentNavigationLisenter) {
            viewModel.fragmentNavigationLisenter = context
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
        searchNotes = viewModel.getSearchNotes()
        filterList = searchNotes
        oldMyNotes = searchNotes as ArrayList<Notes>
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeToolBar()
        actionBarListener()
        setAdaptor()
        initializeMenu()


    }


    private fun initializeFilterChoice() {
        filterChoiceSelected = FilterChoiceSelected(
            isFavorite = false,
            isPriority_red = false,
            isPriority_yellow = false,
            isPriority_green = false
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


                        if (p0 == "") {
                            searchView.hideKeyboard()
                        }
                        if (
                            ::adaptor.isInitialized
                        )
                            notesFiltering(p0)

                        return true
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
        val suggestion = viewModel.getSuggestion()
        if (suggestion.isNotEmpty()) {
            list.add(NotesRvItem.Title(1, SEARCH_SUGGESTION))
            suggestion.forEach {
                list.add(NotesRvItem.Suggestion((it)))
            }


        }
        binding.rvSuggestions.initRecyclerView(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            ), SearchViewAdaptor(list, this@SearchFragment), false
        )


    }


    private val initializeToolBar = {
        val toolbar: Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.title = ""
        (activity as AppCompatActivity).apply {
            this.setSupportActionBar(toolbar)
            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)

        }
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_arrow_back_24)


    }


    private fun notesFiltering(p0: String) {
        binding.searchtext.visibility = View.GONE
        binding.searchImage.visibility = View.GONE
        val newFilteredList = arrayListOf<Notes>()
        val list = mutableListOf<NotesRvItem>()

        if (p0 != "") {
            binding.searchtext.visibility = View.GONE
            binding.ivSearchIllustration.visibility = View.GONE
            binding.tvScreenInfo.visibility = View.GONE
            binding.searchImage.visibility = View.GONE
            for (i in searchNotes) {
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
        } else {
            binding.searchtext.visibility = View.VISIBLE
            binding.searchImage.visibility = View.VISIBLE
            binding.ivSearchIllustration.visibility = View.GONE
            binding.tvScreenInfo.visibility = View.GONE

            lifecycleScope.launch(Dispatchers.IO)
            {
//                delay(5000L)
                val suggestion = viewModel.getSuggestion()
                if (suggestion.isNotEmpty()) {
                    list.add(NotesRvItem.Title(1, SEARCH_SUGGESTION))
                    suggestion.forEach {
                        list.add(NotesRvItem.Suggestion((it)))
                    }


                }
            }


        }



        if (newFilteredList.isEmpty() && p0 != "") {
            binding.tvScreenInfo.visibility = View.VISIBLE
            binding.ivSearchIllustration.visibility = View.VISIBLE
        } else if (newFilteredList.isNotEmpty()) {
            binding.tvScreenInfo.visibility = View.GONE
            binding.ivSearchIllustration.visibility = View.GONE
            list.add(NotesRvItem.Title(2, RECENT_SEARCH))
        }
        list.addAll(newFilteredList)
        adaptor.setData(list)
    }


    private fun navigationOnFragment(fragment: Fragment) {
        viewModel.fragmentNavigationLisenter?.navigate(fragment, BackStack.PREVIEW)
    }


    private fun displaySortDailog(): Boolean {
        viewModel.dialog =
            SortDialogFragment.newInstance(viewModel.currentSortOptions, viewModel.sortBy)
        viewModel.dialog.show(childFragmentManager, "3")
        return true
    }


    private fun displayFilterDailog(): Boolean {
        viewModel.dialog = FilterDailog.newInstance(filterChoiceSelected!!)
        viewModel.dialog.show(childFragmentManager, "3")
        return true
    }


    override fun addSuggestion(name: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.deleteSearchHistory(name)
            viewModel.addSuggestion(name)
        }
    }

    override fun onSuggestionClicked(name: String) {
        searchView.setQuery(name, false)
    }

    override fun deleteSearchHistory(name: String) {
        lifecycleScope.launch()
        {
            viewModel.deleteSearchHistory(name)
            setData()


        }
    }

    override fun onClickedNote(notes: Notes) {
        navigationOnFragment(PreviewFragment.newInstance(notes))
    }

    override fun onFilterClickDone(choice: FilterChoiceSelected) {
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
        searchNotes = FilterList.filterListByChoice(filterList, choice)
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
        viewModel.resetFilterCount()
        filterChoiceSelected = FilterChoiceSelected(
            isFavorite = false,
            isPriority_red = false, isPriority_yellow = false,
            isPriority_green = false
        )
    }

    override fun onSortOptionSelected(sortValues: SortValues, sortBy: SortBy) {

        viewModel.currentSortOptions = sortValues
        updateAppBarText(sortValues)
        updateAppBarText(sortValues)
        viewModel.sortBy = sortBy
        sortedList = SortList.sortList(sortValues, viewModel.sortBy, oldMyNotes)
        searchNotes = sortedList
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
        { selectedFilterCount ->

            if (selectedFilterCount > 0) {
                binding.sort.btnFilterText.text =
                    resources.getString(R.string.selectedCount, selectedFilterCount)
                binding.sort.btnFilterText.visibility = View.VISIBLE
            } else {
                binding.sort.btnFilterText.visibility = View.GONE
            }


        }

    }


    private fun setData() {
        val list = mutableListOf<NotesRvItem>()
        val suggestion = viewModel.getSuggestion()
        if (suggestion.isNotEmpty()) {
            list.add(NotesRvItem.Title(1, SEARCH_SUGGESTION))
            suggestion.forEach {
                list.add(NotesRvItem.Suggestion((it)))
            }


        }
        adaptor.setData(list)


    }

}