package com.announce.presenter.mainmenu.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.announce.R
import com.announce.announce_list.ui.AnnounceListFragment
import com.google.android.material.tabs.TabLayout
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class MainMenuFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.main_menu_layout,
            container,
            false
        )

        val viewPager = view.findViewById<ViewPager>(
            R.id.view_pager
        )

        viewPager.adapter = ViewPagerAdapter(
            childFragmentManager, requireContext()
        )

        val tabLayout = view.findViewById<TabLayout>(
            R.id.tab_layout
        )

        tabLayout.setupWithViewPager(viewPager)



        return view
    }

    private class PagerIllegalState(position: Int)
        : IllegalStateException("There is no fragment at position: $position")

    private class ViewPagerAdapter(
        fragmentManager: FragmentManager,
        val context: Context
    ) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> AnnounceListFragment()
                1 -> AnnounceListFragment()
                else -> throw PagerIllegalState(position)
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> context.getString(R.string.live)
                1 -> context.getString(R.string.filter)
                else -> throw PagerIllegalState(position)
            }
        }

    }
}