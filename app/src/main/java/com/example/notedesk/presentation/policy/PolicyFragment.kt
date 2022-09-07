package com.example.notedesk.presentation.policy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.notesappfragment.R
import com.example.notedesk.domain.util.keys.Constants

import com.example.notesappfragment.databinding.FragmentPolicyBinding


class PolicyFragment : Fragment() {

private lateinit var binding: FragmentPolicyBinding
private lateinit var toolbar: androidx.appcompat.widget.Toolbar

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
        toolbar = requireView().findViewById(R.id.my_toolbar)
        toolbar.title = Constants.POLICY_FRAGMENT
        (activity as AppCompatActivity).apply {
            this.setSupportActionBar(toolbar)
            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)

    }

}