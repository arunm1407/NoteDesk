package com.example.notedesk.presentation.home.dailog


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notedesk.databinding.SortDailogBinding
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.presentation.home.listener.SortLisenter
import com.example.notedesk.presentation.util.withArgs


class SortDialogFragment : DialogFragment() {


    companion object {


        fun newInstance(sort: SortValues, sortBy: SortBy) =
            SortDialogFragment().withArgs {

                putSerializable(Keys.SORT_VALUES, sort)
                putSerializable(Keys.SORT_BY, sortBy)


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
        selectedSortChoice = bundle[Keys.SORT_VALUES] as SortValues
        sortBy = bundle[Keys.SORT_BY] as SortBy

    }


    override fun onStart() {
        super.onStart()
        dialog?.let {

            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)

        }
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

        binding.cancel.setOnClickListener()
        {
            dismiss()
        }
        binding.done.setOnClickListener()
        {
            getSelectedChoice()
            sortBy()
            sortLisenter?.onSortOptionSelected(selectedSortChoice, sortBy)
            dismiss()
        }

    }


    private fun sortBy() {
        sortBy = when (binding.toggleButton.checkedButtonId) {
            binding.button1.id -> SortBy.DESCENDING
            else -> SortBy.ASCENDING
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
                binding.priority.isChecked = true
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
}