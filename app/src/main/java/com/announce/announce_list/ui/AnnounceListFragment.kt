package com.announce.announce_list.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.announce.R
import com.announce.announce_list.viewmodel.AnnounceListViewModel

class AnnounceListFragment: Fragment() {


    private lateinit var viewModel: AnnounceListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)
            .get(AnnounceListViewModel::class.java)
        viewModel.loadMessages()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.announce_list_fragment,
            container,
            false
        )
    }


}