package com.app.ecarepro.ui.timetableviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.R
import com.app.ecarepro.model.Classe


class ClassTimeTableFragment(classes: List<Classe>) : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         return inflater.inflate(R.layout.fragment_class_time_table, container, false)
    }


}