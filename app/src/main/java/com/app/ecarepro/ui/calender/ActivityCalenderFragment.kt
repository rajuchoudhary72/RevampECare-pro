package com.app.ecarepro.ui.calender

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.FragmentActivityCalenderBinding
import com.app.ecarepro.model.ActivityMonth



class ActivityCalenderFragment : Fragment() {

    private lateinit var binding: FragmentActivityCalenderBinding

    private var month: ActivityMonth? = null
    private var session: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                month = it.getParcelable(ARG_ITEM_DATA, ActivityMonth::class.java)
            }else{
                @Suppress("DEPRECATION")
                month = it.getParcelable(ARG_ITEM_DATA)
            }
            session = it.getString(ARG_ITEM_SESSION, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentActivityCalenderBinding.inflate(inflater,container,false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSession.text= session

        if (month!=null){

        if (month!!.activity!=null){

            val calenderListAdapter =
                CalenderListAdapter(
                    month!!.activity!!,
                    this@ActivityCalenderFragment)

            binding.recyclerCalender.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = calenderListAdapter
            }
            binding.recyclerCalender.isVisible=true
            binding.tvNoData.isVisible=false


        }else{
            binding.recyclerCalender.isVisible=false
            binding.tvNoData.isVisible=true

        }
        }else{
            binding.recyclerCalender.isVisible=false
            binding.tvNoData.isVisible=true

        }



    }


    companion object {
        private const val ARG_ITEM_DATA = "item_month_data"
        private const val ARG_ITEM_SESSION = "item_session"

        fun newInstance( month: ActivityMonth,  session: String)= ActivityCalenderFragment().apply {
            arguments= Bundle().apply {
                putParcelable(ARG_ITEM_DATA,month)
                putString(ARG_ITEM_SESSION,session)

            }
        }

    }

}