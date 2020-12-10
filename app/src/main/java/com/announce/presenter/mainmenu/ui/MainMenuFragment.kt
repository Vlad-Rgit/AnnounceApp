package com.announce.presenter.mainmenu.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.announce.R
import com.announce.databinding.FilterDialogLayoutBinding
import com.announce.databinding.MainMenuLayoutBinding
import com.announce.presenter.mainmenu.announce_list.ui.AnnounceListFragment
import com.announce.presenter.mainmenu.viewmodel.MainMenuViewModel
import com.announce.presenter.mainmenu.viewmodel.TimePaymentState
import com.announce.presenter.pay.ui.PayActivity
import com.google.android.material.chip.Chip
import java.time.Instant
import java.time.ZoneId

class MainMenuFragment: Fragment() {


    companion object {
        private const val REQUETS_TIME_PAYMENT_CODE = 1
    }

    private lateinit var viewModel: MainMenuViewModel
    private lateinit var binding: MainMenuLayoutBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(
                requireActivity().application)
        )
            .get(MainMenuViewModel::class.java)

        viewModel.loadMessages()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MainMenuLayoutBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        selectBtnLive()
        observerViewModel()
    }

    private fun observerViewModel() {
        viewModel.paymentState.observe(viewLifecycleOwner) {
            renderPaymentState(it)
        }
    }

    private fun renderPaymentState(state: TimePaymentState) {

        val seconds = state.remainingSeconds % 60
        val minutes = (state.remainingSeconds / 60) % 60
        val hours = (state.remainingSeconds / 3600)

        val localDateTime = Instant.ofEpochSecond(state.remainingSeconds)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()

        binding.run {

            //tvRemainingTime.text = localDateTime.format(DateTimeFormatter.ISO_TIME)
            tvRemainingTime.text = "$hours:$minutes:$seconds"

            val textIconRes = if(state.hasTime)
                R.drawable.unlock_time
            else
                R.drawable.lock_time

            val textDrawable = ContextCompat.getDrawable(
                    requireContext(),
                    textIconRes
            )

            tvRemainingTime.setCompoundDrawables(
                    textDrawable, // set only left drawable
                    null,
                    null,
                    null
            )
        }

    }


    private fun initButtons() {
        binding.run {
            btnLive.setOnClickListener {
                selectBtnLive()
            }
            btnFilter.setOnClickListener {
                selectBtnFilter()
            }
            btnFilterDialog.setOnClickListener {
                showFilterDialog()
            }
            btnMenu.setOnClickListener {
                showMenu()
            }
        }
    }

    private fun showMenu() {

        val popupMenu = PopupMenu(requireContext(), binding.btnMenu).apply {

            this.inflate(R.menu.main_menu)

            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_pay -> showMenuPay()
                    R.id.menu_announcment -> showMenuAnnouncments()
                }
                true
            }

            show()
        }

    }

    private fun showMenuPay() {
        val intent = Intent(requireContext(), PayActivity::class.java)
        startActivityForResult(intent, REQUETS_TIME_PAYMENT_CODE)
    }

    private fun showMenuAnnouncments() {
        findNavController().navigate(
                R.id.action_mainMenuFragment_to_userAnnouncesFragment
        )
    }

    private fun selectBtnFilter() {
        childFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_host,
                AnnounceListFragment.newInstance(true)
            )
            .commit()

        binding.btnLive.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.colorUnselected)
        )

        binding.btnFilter.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.colorSelected)
        )
    }

    private fun selectBtnLive() {
        childFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_host,
                AnnounceListFragment.newInstance(false)
            )
            .commit()

        binding.btnLive.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.colorSelected)
        )

        binding.btnFilter.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.colorUnselected)
        )
    }

    private fun showFilterDialog() {

        val dialogBinding = FilterDialogLayoutBinding.inflate(
            layoutInflater
        )

        fun addChip(text: String) {
            val chip = layoutInflater.inflate(
                R.layout.filter_dialog_chip,
                dialogBinding.chipGroup,
                false
            ) as Chip

            chip.setOnCloseIconClickListener {
                dialogBinding.chipGroup.removeView(it)
            }
            chip.text = text
            dialogBinding.chipGroup.addView(chip)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .show()

        dialogBinding.run {

            val filterReader = viewModel.filterReader

            chAudio.isChecked = filterReader.isAudioEnabled
            chPhoto.isChecked = filterReader.isPhotoEnabled

            for(keyword in filterReader.keywords) {
                addChip(keyword)
            }

            btnAddKeyWord.setOnClickListener {

                val keyword = edKeyWord.text.toString()

                if(keyword.isNotEmpty()) {
                    addChip(edKeyWord.text.toString())
                    edKeyWord.setText("")
                }
            }

            btnClose.setOnClickListener {

                val keywords = mutableListOf<String>()
                for (i in 0 until chipGroup.childCount) {
                    val chip = chipGroup.getChildAt(i) as Chip
                    keywords.add(chip.text.toString())
                }

                val editor = viewModel.filterEditor
                    .setIsAudioEnabled(chAudio.isChecked)
                    .setIsPhotoEnabled(chPhoto.isChecked)
                        .setKeywords(keywords)

                editor.commit()
                dialog.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            REQUETS_TIME_PAYMENT_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    handelSuccessTimePayment(data
                            .getLongExtra(PayActivity.KEY_ORDERED_TIME_MILLIS, 0L))
                }
            }
        }
    }

    private fun handelSuccessTimePayment(payedMillis: Long) {
        viewModel.addPayedTime(payedMillis)
    }



}