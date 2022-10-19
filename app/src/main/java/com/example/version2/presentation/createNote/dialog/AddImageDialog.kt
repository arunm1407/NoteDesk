package com.example.version2.presentation.createNote.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.version2.databinding.AddImageDialogBinding
import com.example.version2.presentation.createNote.enums.AddImage
import com.example.version2.presentation.createNote.listener.DialogLisenter

class AddImageDialog : DialogFragment() {
    private lateinit var binding: AddImageDialogBinding
    private var dialogLisenter: DialogLisenter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val parent = parentFragment ?: context
        if (parent is DialogLisenter) {
            dialogLisenter = parent
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddImageDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventHandler()
    }

    private fun eventHandler() {
        takePhotoListener()
        chooseImageListener()


    }

    private fun chooseImageListener() {
        binding.layoutTakePhoto.setOnClickListener {
            dialogLisenter?.choice(AddImage.TAKE_PHOTO)
            dismiss()
        }
    }

    private fun takePhotoListener() {
        binding.layoutChooseImage.setOnClickListener {
            dialogLisenter?.choice(AddImage.CHOOSE_PHOTO)
            dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
        dialogLisenter = null

    }


}