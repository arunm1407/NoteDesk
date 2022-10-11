package com.example.notedesk.presentation.signup.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.notedesk.databinding.SuccessBinding
import com.example.notedesk.presentation.signup.listener.SuccessListener


class SuccessDailog : DialogFragment() {

    private lateinit var binding: SuccessBinding
    private var dialogLisenter: SuccessListener?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val parent = parentFragment ?: context
        if (parent is SuccessListener) {
            dialogLisenter = parent
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SuccessBinding.inflate(layoutInflater, container, false)
        binding.btnSuccess.setOnClickListener {
            dialogLisenter?.done()
            dismiss()
        }
        return binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()

        dialog?.window?.addFlags(
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        )

    }

        override fun onDetach() {
        super.onDetach()
        dialogLisenter=null

    }



}


