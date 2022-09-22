package com.example.notedesk.presentation.home.dailog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected
import com.example.notesappfragment.databinding.FilterDailogBinding
import com.example.notedesk.domain.util.keys.Keys
import com.example.notedesk.presentation.home.Listener.FilterChoiceLisenter
import com.example.notedesk.presentation.home.Listener.SortLisenter

class FilterDailog : DialogFragment() {

    private lateinit var filterChoiceSelected: FilterChoiceSelected
    private lateinit var binding: FilterDailogBinding
    private var filterLisenter: FilterChoiceLisenter? = null

    companion object {


        fun newInstance(filterChoiceSelected: FilterChoiceSelected) =
            FilterDailog().apply {
                val bundle = Bundle()
                bundle.putParcelable(Keys.FILTER_VALUES, filterChoiceSelected)
                arguments = bundle

            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment ?: context
        if (parent is SortLisenter)
            filterLisenter = parentFragment as FilterChoiceLisenter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgumentParcelable()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterDailogBinding.inflate(inflater, container, false)
        retrievedChoiceToView()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {

            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cancel.setOnClickListener()
        {
            dismiss()
        }
        binding.done.setOnClickListener()
        {
            filterLisenter?.onFilterClickDone(
                FilterChoiceSelected(
                    binding.favorite.isChecked,
                    binding.priorityRed.isChecked,
                    binding.priorityYellow.isChecked,
                    binding.priorityGreen.isChecked,
                )
            )
            dismiss()

        }
        binding.clearData.setOnClickListener()
        {
            filterLisenter?.onFilterClear()
            dismiss()
        }
    }


    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        filterChoiceSelected = bundle.getParcelable(Keys.FILTER_VALUES)!!
    }


    private fun retrievedChoiceToView() {

        binding.favorite.isChecked = filterChoiceSelected.isFavorite
        binding.priorityGreen.isChecked = filterChoiceSelected.isPriority_green
        binding.priorityRed.isChecked = filterChoiceSelected.isPriority_red
        binding.priorityYellow.isChecked = filterChoiceSelected.isPriority_yellow


    }


    override fun onDestroy() {
        super.onDestroy()
        filterLisenter = null
    }
}