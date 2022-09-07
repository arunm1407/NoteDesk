package com.example.notedesk.presentation.createNote.dailog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notesappfragment.databinding.AddImageBinding
import com.example.notedesk.presentation.createNote.enums.AddImage
import com.example.notedesk.presentation.createNote.listener.DialogLisenter

class AddImageDailog : DialogFragment() {



    private lateinit var binding: AddImageBinding
    private var dialogLisenter: DialogLisenter?=null

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
        binding = AddImageBinding.inflate(layoutInflater, container, false)
        binding.layoutTakePhoto.setOnClickListener {
            dialogLisenter?.choice(AddImage.TAKE_PHOTO)
            dismiss()
        }
        binding.layoutChooseImage.setOnClickListener {
            dialogLisenter?.choice(AddImage.CHOOSE_PHOTO)
            dismiss()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogLisenter=null
    }


}