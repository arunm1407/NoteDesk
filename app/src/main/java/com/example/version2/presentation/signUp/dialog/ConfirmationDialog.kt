package com.example.version2.presentation.signUp.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.version2.databinding.AccountCreateDialogBinding
import com.example.version2.presentation.signUp.listener.ConfirmationListener

class ConfirmationDialog : DialogFragment() {

    private lateinit var binding: AccountCreateDialogBinding
    private var dialogLisenter: ConfirmationListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val parent = parentFragment ?: context
        if (parent is ConfirmationListener) {
            dialogLisenter = parent
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountCreateDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventHandler()
    }

    private fun eventHandler() {
        successListener()
    }

    private fun successListener() {
        binding.btnSuccess.setOnClickListener {
            dialogLisenter?.done()
            dismiss()
        }
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
        dialogLisenter = null

    }


}