package com.announce.presenter.user_announces.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.announce.databinding.UserAnnouncesLayoutBinding
import com.announce.presenter.user_announces.adapters.AnnounceAdapter
import com.announce.presenter.user_announces.viewmodel.UserAnnounceViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class UserAnnouncesFragment: Fragment() {

    private lateinit var binding: UserAnnouncesLayoutBinding
    private lateinit var viewModel: UserAnnounceViewModel
    private lateinit var announceAdapter: AnnounceAdapter


    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)
                .get(UserAnnounceViewModel::class.java)

        viewModel.startCollectAnnounces()

        announceAdapter = AnnounceAdapter()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        binding = UserAnnouncesLayoutBinding.inflate(
                inflater,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeViewModel()
    }

    private fun initRecyclerView() {
        binding.rvAnnounces.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = announceAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.announces.observe(viewLifecycleOwner) {
            announceAdapter.setItems(it)
        }
    }
}