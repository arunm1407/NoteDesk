package com.example.notedesk.presentation.home.dailog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notesappfragment.R
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected
import com.example.notesappfragment.databinding.FilterDailogBinding
import com.example.notedesk.domain.util.keys.IndentKeys
import com.example.notedesk.presentation.home.Listener.FilterChoiceLisenter
import com.example.notedesk.presentation.home.Listener.SortLisenter

class FilterDailog : DialogFragment(),
    View.OnClickListener {

    private lateinit var filterChoiceSelected: FilterChoiceSelected
    private lateinit var binding: FilterDailogBinding
    private var filterLisenter: FilterChoiceLisenter? = null

    companion object {


        fun newInstance(filterChoiceSelected: FilterChoiceSelected) =
            FilterDailog().apply {
                val bundle = Bundle()
                bundle.putParcelable(IndentKeys.FILTER_VALUES, filterChoiceSelected)
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cancel.setOnClickListener(this)
        binding.done.setOnClickListener(this)
        binding.clearData.setOnClickListener(this)
    }


    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        filterChoiceSelected = bundle.getParcelable(IndentKeys.FILTER_VALUES)!!
        Log.i("arun","$filterChoiceSelected")
    }


    private fun retrievedChoiceToView() {

        binding.favorite.isChecked = filterChoiceSelected.isFavorite
        binding.priorityGreen.isChecked = filterChoiceSelected.isPriority_green
        binding.priorityRed.isChecked = filterChoiceSelected.isPriority_red
        binding.priorityYellow.isChecked = filterChoiceSelected.isPriority_yellow


    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancel -> dismiss()
            R.id.done -> {
                filterLisenter?.onClickDone(
                    FilterChoiceSelected(
                        binding.favorite.isChecked,
                        binding.priorityRed.isChecked,
                        binding.priorityYellow.isChecked,
                        binding.priorityGreen.isChecked,
                    )
                )
                dismiss()
            }
            R.id.clear_data -> {
                filterLisenter?.onClear()
                dismiss()
            }
        }
    }
}