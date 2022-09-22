package com.example.notedesk.presentation.createNote.dailog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notesappfragment.databinding.SettingsStorgaeDailogBinding
import com.example.notedesk.presentation.createNote.enums.ExitSettingsAction
import com.example.notedesk.presentation.createNote.listener.ExitDailogLisenter
import com.example.notedesk.presentation.createNote.CreateNotesFragment

class StorageSettings : DialogFragment() {

    private lateinit var binding: SettingsStorgaeDailogBinding
    private var lisenter: ExitDailogLisenter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment ?: context
        if (parent is CreateNotesFragment)
            lisenter = (parentFragment as CreateNotesFragment)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsStorgaeDailogBinding.inflate(layoutInflater, container, false)
        binding.yes.setOnClickListener {

            lisenter?.onClickYes(ExitSettingsAction.STORAGE)
            dismiss()

        }
        binding.no.setOnClickListener {
            dismiss()
        }
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        lisenter = null
    }

}