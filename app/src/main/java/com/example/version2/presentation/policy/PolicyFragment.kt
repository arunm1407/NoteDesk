package com.example.version2.presentation.policy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.version2.presentation.util.keys.Constants
import com.example.version2.R
import com.example.version2.databinding.FragmentPolicyBinding
import com.example.version2.presentation.util.setup


class PolicyFragment : Fragment() {
    private lateinit var binding: FragmentPolicyBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPolicyBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()

    }

    private fun setupToolBar() {
        val toolbar: Toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.setup(requireActivity(), Constants.POLICY_FRAGMENT)


    }

}