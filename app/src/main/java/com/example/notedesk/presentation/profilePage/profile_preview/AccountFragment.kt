package com.example.notedesk.presentation.profilePage.profile_preview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notedesk.databinding.FragmentAccountBinding
import com.example.notedesk.presentation.profilePage.ProfileAdaptor
import com.example.notedesk.presentation.profilePage.ProfileDetails
import com.example.notedesk.presentation.util.initRecyclerView
import com.example.notedesk.presentation.util.withArgs


class AccountFragment : Fragment() {


    companion object {

        private const val PROFILE_LIST = "profileList"
        fun newInstance(
            list: ArrayList<ProfileDetails>
        ): AccountFragment =
            AccountFragment().withArgs {
                putParcelableArrayList(PROFILE_LIST, list)
            }


    }


    private lateinit var binding: FragmentAccountBinding
    private var list = mutableListOf<ProfileDetails>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializationData()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgumentParcelable()
    }

    private fun getArgumentParcelable() {
        val bundle: Bundle = requireArguments()
        list=bundle.getParcelableArrayList(PROFILE_LIST)!!
    }

    private fun initializationData() {

        binding.recyclerView.initRecyclerView(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            ), ProfileAdaptor(list)
        )

    }


}