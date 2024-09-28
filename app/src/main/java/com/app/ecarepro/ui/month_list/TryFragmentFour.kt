package com.app.ecarepro.ui.month_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
@AndroidEntryPoint
class TryFragmentFour  //
    : Fragment() {

    private var calender2: RecyclerView? = null
    private var adapter2: TryRVCellAdapter? = null

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
            Log.e("Anil", "" + TryAttendanceTest2.session_month_list!![3])
            Log.e("Anil", "" + TryAttendanceTest2.session_year_list!![3])
            adapter2 = TryRVCellAdapter(
                activity,
                TryAttendanceTest2.session_month_list!![3],
                TryAttendanceTest2.session_year_list!![3]
            )
            calender2!!.adapter = adapter2
            adapter2!!.notifyDataSetChanged()
        } catch (e: Exception) {
            e.stackTrace
            Toast.makeText(context, TryAttendanceTest2.server_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setId(view: View) {
        calender2 = view.findViewById(R.id.calendar2)
    }


}


