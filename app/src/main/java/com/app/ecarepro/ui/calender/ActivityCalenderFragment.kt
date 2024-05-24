package com.app.ecarepro.ui.calender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentActivityCalenderBinding
import com.app.ecarepro.model.ActivityMonth


class ActivityCalenderFragment(private val month: ActivityMonth, private val session: String) :
    Fragment() {

    private lateinit var binding: FragmentActivityCalenderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentActivityCalenderBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSession.text = session

        if (month.activity != null) {


            val calenderListAdapter =
                CalenderListAdapter(
                    month.activity,
                    this@ActivityCalenderFragment
                )

            binding.recyclerCalender.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = calenderListAdapter
            }
            binding.recyclerCalender.isVisible = true
            binding.tvNoData.isVisible = false


        } else {
            binding.recyclerCalender.isVisible = false
            binding.tvNoData.isVisible = true

        }


    }
}