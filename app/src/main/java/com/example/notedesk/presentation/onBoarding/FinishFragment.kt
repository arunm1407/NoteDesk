package com.example.notedesk.presentation.onBoarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notedesk.presentation.login.activity.LoginActivity
import com.example.notedesk.databinding.FragmentFinishBinding


class FinishFragment : Fragment() {

    private lateinit var binding: FragmentFinishBinding
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
            startActivity(
                Intent(requireContext(), LoginActivity::class.java).apply {
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }


            )
            requireActivity().finish()

        }
    }

}