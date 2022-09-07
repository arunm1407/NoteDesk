package com.example.notedesk.presentation.home.dailog


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notesappfragment.R
import com.example.notesappfragment.databinding.SortDailogBinding
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import com.example.notedesk.domain.util.keys.IndentKeys
import com.example.notedesk.presentation.home.Listener.SortLisenter


class SortDialogFragment : DialogFragment(),
    View.OnClickListener {


    companion object {


        fun newInstance(sort: SortValues, sortBy: SortBy) =
            SortDialogFragment().apply {
                val bundle = Bundle()
                bundle.putSerializable(IndentKeys.SORT_VALUES, sort)
                bundle.putSerializable(IndentKeys.SORT_BY, sortBy)
                arguments = bundle

            }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment ?: context
        if (parent is SortLisenter)
            sortLisenter = parentFragment as SortLisenter
    }

    private var sortLisenter: SortLisenter? = null
    private lateinit var binding: SortDailogBinding
    private lateinit var selectedSortChoice: SortValues
    private lateinit var sortBy: SortBy


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgumentParcelable()
    }


    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        selectedSortChoice = bundle[IndentKeys.SORT_VALUES] as SortValues
        sortBy = bundle[IndentKeys.SORT_BY] as SortBy
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SortDailogBinding.inflate(inflater, container, false)
        retrievedChoiceToView()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sortBy()
        binding.cancel.setOnClickListener(this)
        binding.done.setOnClickListener(this)

    }


    private fun sortBy() {
        sortBy = when (binding.toggleButton.checkedButtonId) {
            binding.button1.id -> SortBy.ASCENDING
            else -> SortBy.DESENDING
        }
    }


    private fun getSelectedChoice() {

        selectedSortChoice = when (binding.radioGroup.checkedRadioButtonId) {
            binding.alphabeticalTitle.id -> {
                SortValues.ALPHABETICALLY_TITLE
            }
            binding.alphabeticalSubtitle.id -> {
                SortValues.ALPHABETICALLY_SUBTITLE
            }
            binding.creationDate.id -> {
                SortValues.CREATION_DATE
            }
            binding.modificationDate.id -> {
                SortValues.MODIFICATION_DATE
            }
            else -> {
                SortValues.PRIORITY
            }


        }
    }


    private fun retrievedChoiceToView() {
        when (selectedSortChoice) {
            SortValues.MODIFICATION_DATE -> {
                binding.modificationDate.isChecked = true
            }
            SortValues.ALPHABETICALLY_TITLE -> {
                binding.alphabeticalTitle.isChecked = true
            }
            SortValues.ALPHABETICALLY_SUBTITLE -> {
                binding.alphabeticalSubtitle.isChecked = true
            }
            SortValues.CREATION_DATE -> {
                binding.creationDate.isChecked = true
            }
            SortValues.PRIORITY -> {
                binding.priorty.isChecked = true
            }
        }
        when (sortBy) {
            SortBy.ASCENDING -> {
                binding.button2.isChecked = true
            }
            else -> {
                binding.button1.isChecked = true
            }
        }

    }


    override fun onDetach() {
        super.onDetach()
        sortLisenter = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancel -> dismiss()
            R.id.done -> {
                getSelectedChoice()
                sortLisenter?.onOptionSelected(selectedSortChoice, sortBy)
                dismiss()
            }
        }

    }
}