package com.example.version2.presentation.profile.preview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.version2.databinding.FragmentAccountBinding
import com.example.version2.presentation.profile.ProfileAdaptor
import com.example.version2.presentation.profile.ProfileDetails
import com.example.version2.presentation.util.initRecyclerView
import com.example.version2.presentation.util.withArgs

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


    private fun initializationData() {
        val bundle: Bundle = requireArguments()
        binding.recyclerView.initRecyclerView(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            ), ProfileAdaptor(bundle.getParcelableArrayList(PROFILE_LIST)!!)
        )

    }


}