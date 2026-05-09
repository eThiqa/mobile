package com.almizan.mobile.front.soumission

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SoumissionPagerAdapter(
    activity: AppCompatActivity,
    private val marcheId: String
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> EnveloppeAdminFragment.newInstance(marcheId)
        1 -> EnveloppeTechniqueFragment.newInstance(marcheId)
        2 -> EnveloppeFinanciereFragment.newInstance(marcheId)
        else -> EnveloppeAdminFragment.newInstance(marcheId)
    }
}