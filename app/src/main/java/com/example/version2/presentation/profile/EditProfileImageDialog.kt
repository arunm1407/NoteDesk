package com.example.version2.presentation.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.version2.databinding.EditProfileDialogBinding
import com.example.version2.presentation.createNote.enums.AddImage
import com.example.version2.presentation.createNote.listener.DialogLisenter

class EditProfileImageDialog : DialogFragment() {

    private lateinit var binding: EditProfileDialogBinding
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
        binding = EditProfileDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventHandler()
    }

    private fun eventHandler() {
        takePhotoListener()
        chooseImageListener()
        deleteImageListener()

    }

    private fun deleteImageListener() {
        binding.layoutTakePhoto.setOnClickListener {
            dialogLisenter?.choice(AddImage.TAKE_PHOTO)
            dismiss()
        }
    }

    private fun chooseImageListener() {
        binding.layoutChooseImage.setOnClickListener {
            dialogLisenter?.choice(AddImage.CHOOSE_PHOTO)
            dismiss()
        }
    }

    private fun takePhotoListener() {
        binding.layoutDeleteImage.setOnClickListener {
            dialogLisenter?.choice(AddImage.DELETE_IMAGE)
            dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
        dialogLisenter=null
    }
}