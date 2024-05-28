package com.app.ecarepro.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.widget.DatePicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ECareDataPicker : OnDateSetListener {
    /**
     * 7 * 24 * 60 * 60 * 1000
     * for 7 days
     */
    private var dpd: DatePickerDialog
    private var pickerCallback: PickerCallback

    /**
     * @param mActivity       Current Activity
     * @param preventPastDate is boolean value that set to prevent past date selection
     * @param pickerCallback  is callback return user selected date in String format
     */
    constructor(mActivity: Activity, preventPastDate: Boolean, pickerCallback: PickerCallback) {
        val now = Calendar.getInstance()
        this.pickerCallback = pickerCallback
        dpd = DatePickerDialog(
            mActivity, this,
            now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH]
        )
        if (preventPastDate) {
            dpd.datePicker.minDate = System.currentTimeMillis() - 10000
        }
        dpd.show()
    }



    /**
     * @param mActivity       ECareDataPicker
     * @param preventPastDate is boolean value that set to prevent past date selection
     * @param pickerCallback  is callback return user selected date in String format
     * @param minDate         is set min date to
     * @param maxDate         is set max date
     */
    constructor(
        mActivity: Activity?,
        preventPastDate: Boolean,
        pickerCallback: PickerCallback,
        minDate: Long,
        maxDate: Long
    ) {
        val now = Calendar.getInstance()
        this.pickerCallback = pickerCallback
        dpd = DatePickerDialog(
            mActivity!!, this,
            now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH]
        )
        if (preventPastDate) {
            dpd.datePicker.minDate = System.currentTimeMillis() - 10000
        }
        dpd.datePicker.minDate = minDate
        dpd.datePicker.maxDate = maxDate
        dpd.show()
    }

    fun setDateValidation(minDate: Long, maxDate: Long) {
        if (minDate != 0L) {
            dpd.datePicker.minDate = minDate
        }
        if (maxDate != 0L) {
            dpd.datePicker.maxDate = maxDate
        }
    }

    fun setMinDate(minDate: Long) {
        if (minDate == 0L) return
        dpd.datePicker.minDate = minDate
    }

    fun setMaxDate(maxDate: Long) {
        if (maxDate == 0L) return
        dpd.datePicker.maxDate = maxDate
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        // pickerCallback.onSelect(year + "-" + (month + 1) + "-" + dayOfMonth);
        //  pickerCallback.onSelect(dayOfMonth + "/" + (month + 1) + "/" + year);
        val now = Calendar.getInstance()
        val isCurrentDate =
            now[Calendar.DATE] == dayOfMonth && now[Calendar.MONTH] == month && now[Calendar.YEAR] == year
        pickerCallback.onSelect(
            year.toString() + "-" + (month + 1) + "-" + dayOfMonth,
            isCurrentDate
        )
        /*pickerCallback.onSelect(
            dayOfMonth.toString() + " " + SimpleDateFormat("MMM").format(  Date( year,
                month - 1, dayOfMonth ) ) + ", " + year,
            isCurrentDate
        )*/
        // pickerCallback.onSelect(dayOfMonth + "-" + (month + 1) + "-" + year, isCurrentDate);
    }

    interface PickerCallback {
        fun onSelect(date: String?, isCurrentDate: Boolean)
    }





}
