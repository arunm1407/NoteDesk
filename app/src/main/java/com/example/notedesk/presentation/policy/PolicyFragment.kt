package com.example.notedesk.presentation.policy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.notedesk.R
import com.example.notedesk.util.keys.Constants
import com.example.notedesk.databinding.FragmentPolicyBinding
import com.example.notedesk.presentation.util.setup


class PolicyFragment : Fragment() {

private lateinit var binding: FragmentPolicyBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{


        binding = FragmentPolicyBinding.inflate(layoutInflater, container, false)
        return binding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeToolBar()

    }





    private fun initializeToolBar() {
        val toolbar: Toolbar = requireView().findViewById(R.id.my_toolbar)
      toolbar.setup(requireActivity(),Constants.POLICY_FRAGMENT)


    }

}