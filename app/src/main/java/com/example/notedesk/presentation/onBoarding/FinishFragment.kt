package com.example.notedesk.presentation.onBoarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notedesk.databinding.FragmentFinishBinding
import com.example.notedesk.presentation.activity.MainActivity
import com.example.notedesk.presentation.onBoarding.listener.Navigation


class FinishFragment : Fragment() {

    private lateinit var binding: FragmentFinishBinding
    private var navigationLisenter: Navigation? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Navigation)
            navigationLisenter = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinishBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutStart.setOnClickListener {
            navigationLisenter?.navigateScreen(Intent(requireContext(), MainActivity::class.java))
        }
    }


    override fun onDetach() {
        super.onDetach()
        navigationLisenter = null
    }

}

