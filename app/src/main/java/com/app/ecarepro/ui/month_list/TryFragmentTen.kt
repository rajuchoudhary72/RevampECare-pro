package com.app.ecarepro.ui.month_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.ui.TryAttendanceTest2
import com.app.ecarepro.ui.TryRVCellAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Chandan on 17/11/2017.
 */

class TryFragmentTen  //
    : Fragment() {

    private var calender2: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.try_fragment_abcd, container, false)

        setId(view)


        setAdapter()
        return view
    }

    private fun setAdapter() {
        try {
            val numberOfColumns = 7
            calender2!!.layoutManager = GridLayoutManager(context, numberOfColumns)
            val adapter2 = TryRVCellAdapter(
                activity,
                TryAttendanceTest2.session_month_list!![9],
                TryAttendanceTest2.session_year_list!![9]
            )
            adapter2.notifyDataSetChanged()
            calender2!!.adapter = adapter2
        } catch (ex: Exception) {
            ex.stackTrace
        }
    }

    private fun setId(view: View) {
        calender2 = view.findViewById(R.id.calendar2)
    }


}


