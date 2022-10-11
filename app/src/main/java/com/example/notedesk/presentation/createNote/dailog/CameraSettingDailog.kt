package com.example.notedesk.presentation.createNote.dailog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notedesk.databinding.SettingsCameraDailogBinding
import com.example.notedesk.presentation.createNote.enums.ExitSettingsAction
import com.example.notedesk.presentation.createNote.listener.ExitDailogLisenter

class CameraSettingDailog : DialogFragment() {


    private lateinit var binding: SettingsCameraDailogBinding
    private var lisenter: ExitDailogLisenter? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment ?: context
        if (parent is ExitDailogLisenter)
            lisenter = (parentFragment as ExitDailogLisenter)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsCameraDailogBinding.inflate(layoutInflater, container, false)
        binding.yes.setOnClickListener {
            lisenter?.onClickYes(ExitSettingsAction.CAMERA)
            dismiss()
        }
        binding.no.setOnClickListener {
            dismiss()
        }
        return binding.root
    }


    override fun onDetach() {
        super.onDetach()
        lisenter=null
    }

}


