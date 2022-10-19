package com.example.version2.presentation.createNote.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.version2.databinding.SettingCameraDialogBinding
import com.example.version2.presentation.createNote.enums.ExitSettingsAction
import com.example.version2.presentation.createNote.listener.ExitDailogLisenter

class CameraSettingsDialog : DialogFragment() {


    private lateinit var binding: SettingCameraDialogBinding
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
        binding = SettingCameraDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventHandler()
    }

    private fun eventHandler() {

        positiveActionListener()
        negativeActionListener()


    }

    private fun negativeActionListener() {
        binding.no.setOnClickListener {
            dismiss()
        }
    }

    private fun positiveActionListener() {
        binding.yes.setOnClickListener {
            lisenter?.onClickYes(ExitSettingsAction.CAMERA)
            dismiss()
        }
    }


    override fun onDetach() {
        super.onDetach()
        lisenter = null
    }
}