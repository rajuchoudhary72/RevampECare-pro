package com.app.ecarepro.ui.payslip

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.Year


class CustomDropDownAdapterYear(val context: Context, private var listItemsTxt: List<Year>) : BaseAdapter() {


    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.view_drop_down_menu, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        // setting adapter item height programatically.

        /*val params = view.layoutParams
        params.height = 120
        view.layoutParams = params*/

        vh.label.text = listItemsTxt[position].year.toString()
        return view
    }

    override fun getItem(position: Int): Any? {

        return null

    }




    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return listItemsTxt.size
    }

    private class ItemRowHolder(row: View?) {

        val label: TextView

        init {
            this.label = row?.findViewById(R.id.txtDropDownLabel) as TextView
        }
    }
}