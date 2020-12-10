package com.announce.presenter.mainmenu.announce_list.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.announce.common.domain.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.announce.databinding.AnnounceListFragmentBinding
import com.announce.presenter.mainmenu.announce_list.adapters.AnnounceAdapter
import com.announce.presenter.mainmenu.viewmodel.*

class AnnounceListFragment: Fragment() {

    companion object {

        private const val REQUEST_WRITE_PERMISSION = 1
        const val IS_FILTER_KEY = "isFilterKey"

        fun newInstance(isFilter: Boolean): AnnounceListFragment {
            return AnnounceListFragment().apply {
                arguments = bundleOf(
                    IS_FILTER_KEY to isFilter
                )
            }
        }
    }


    private var messageForImageDownload: Message? = null

    private var isFilter: Boolean = false
    private lateinit var binding: AnnounceListFragmentBinding
    private lateinit var viewModel: MainMenuViewModel
    private lateinit var announceAdapter: AnnounceAdapter
    private var currentState: State? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        isFilter = requireArguments().getBoolean(IS_FILTER_KEY)

        viewModel = ViewModelProvider(
            requireParentFragment(),
            ViewModelProvider.AndroidViewModelFactory(
                requireActivity().application)
        ).get(MainMenuViewModel::class.java)

        announceAdapter = initAnnounceAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =  AnnounceListFragmentBinding.inflate(
            inflater,
            container,
            false
        )


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwipeLayout()
        observeViewModel()
        initRecyclerView()
        initButtons()
    }

    private fun initAnnounceAdapter(): AnnounceAdapter {
        return AnnounceAdapter().apply {
            setOnMenuCallClicked {
                callClicked(it)
            }
            setOnMenuDownloadImageClicked {
                downloadImageClicked(it)
            }
            setOnMenuCopyTextClicked {
                copyTextClicked(it)
            }
        }
    }

    private fun callClicked(message: Message) {
        val phone = message.phoneNumber
        val intent = Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:$phone")
        )
        startActivity(intent)
    }

    private fun downloadImageClicked(message: Message) {
        if(checkWritePermission()) {
            viewModel.downloadMessageImage(message)
        }
        else {
            messageForImageDownload = message
        }
    }

    private fun checkWritePermission(): Boolean {

        if(Build.VERSION.SDK_INT < 23)
            return true

        val permissionName = Manifest.permission.WRITE_EXTERNAL_STORAGE

        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionName
        ) == PackageManager.PERMISSION_GRANTED

        if(!hasPermission) {
            requestPermissions(
                arrayOf(permissionName),
                REQUEST_WRITE_PERMISSION
            )
        }

        return hasPermission
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_WRITE_PERMISSION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.downloadMessageImage(messageForImageDownload!!)
            messageForImageDownload = null
        }
    }

    private fun copyTextClicked(message: Message) {

    }

    private fun initButtons() {
        binding.run {
            btnScrollDown.setOnClickListener {
                rvMessages.scrollToPosition(
                    announceAdapter.itemCount - 1)
            }
        }
    }

    private fun initSwipeLayout() {
        binding.swipeLayout.setOnRefreshListener {
            viewModel.loadMessages()
        }
    }

    private fun observeViewModel() {
        if(isFilter) {
            viewModel.filterState.observe(viewLifecycleOwner) {
                render(it)
            }
        }
        else {
            viewModel.state.observe(viewLifecycleOwner) {
                render(it)
            }
        }

        viewModel.paymentState.observe(viewLifecycleOwner) {
            renderTimePaymentState(it)
        }
    }

    private fun renderTimePaymentState(state: TimePaymentState) {
        announceAdapter.mode = if(state.hasTime)
            AnnounceAdapter.Mode.Unblocked
        else
            AnnounceAdapter.Mode.Blocked
    }

    private fun initRecyclerView() {
        binding.rvMessages.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = announceAdapter
        }
    }

    private fun render(state: State) {

        currentState = state

        when(state) {
            is LoadingState -> renderLoadingState()
            is ListState -> renderListState(state)
            is FilteredListState -> renderFilteredListState(state)
        }
    }

    private fun renderFilteredListState(state: FilteredListState) {

        binding.run {
            swipeLayout.isRefreshing = false
            progressCircular.visibility = View.GONE
            rvMessages.visibility = View.VISIBLE
        }

        announceAdapter.setItems(state.filteredList)
    }

    private fun renderLoadingState() {
        binding.run {
            progressCircular.visibility = View.VISIBLE
            rvMessages.visibility = View.GONE
        }
    }

    private fun renderListState(state: ListState) {

        binding.run {
            swipeLayout.isRefreshing = false
            progressCircular.visibility = View.GONE
            rvMessages.visibility = View.VISIBLE
        }

        announceAdapter.setItems(state.list)
    }

}