package com.app.ecarepro.ui

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.TryActivityTestScrollBinding
import com.app.ecarepro.ui.month_list.FragmentAPI
import com.app.ecarepro.ui.staffAttendence.AttendanceViewModel
import com.app.ecarepro.utils.calenderInstance
import com.app.ecarepro.utils.dateToMonth
import com.app.ecarepro.utils.date_converterDay
import com.app.ecarepro.utils.getDayNumberSuffix
import com.google.firebase.installations.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TryAttendanceTest2 : AppCompatActivity() {
    private lateinit var binding: TryActivityTestScrollBinding
    private val context: Context = this
    private var month_name: String? = null
    private var isFromStaff = false
    private var monthId = 0
    private var yearId = 0
    private var userId: String? = null

    /*from student profile */
    private var studentID: String = ""
    private var toStartDate: String = ""
    private var toEndDate: String = ""

    /*end var from student profile */
    private var adapter: TryViewPagerAdapter? = null
    private var progressdialog: ProgressDialog? = null
    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private fun validateCircleUi(lateEnabled: Boolean) {
        val circleCount = if (lateEnabled) 6 else 4
        val screenWidth = (ScreenUtils.getScreenWidth(context))
        val circleW_H = screenWidth / circleCount
        val textSize = if (lateEnabled) 16 else 20

        val circleP = binding.incRowAttendanceCircle.perPresent.layoutParams
        circleP.height = circleW_H
        circleP.width = circleW_H

        binding.incRowAttendanceCircle.perPresent.layoutParams = circleP
        binding.incRowAttendanceCircle.perAbsent.layoutParams = circleP
        binding.incRowAttendanceCircle.perLeave.layoutParams = circleP
        binding.incRowAttendanceCircle.tvLateCircle.layoutParams = circleP

        binding.incRowAttendanceCircle.perPresent.textSize = textSize.toFloat()
        binding.incRowAttendanceCircle.perAbsent.textSize = textSize.toFloat()
        binding.incRowAttendanceCircle.perLeave.textSize = textSize.toFloat()
        binding.incRowAttendanceCircle.tvLateCircle.textSize = textSize.toFloat()

        binding.incRowAttendanceCircle.text4.textSize = textSize.toFloat()
        binding.incRowAttendanceCircle.text5.textSize = textSize.toFloat()
        binding.incRowAttendanceCircle.tvLeave.textSize = textSize.toFloat()
        binding.incRowAttendanceCircle.tvLabelLt.textSize = textSize.toFloat()
    }

    private fun progressDialog() {
        progressdialog = ProgressDialog(this)
        progressdialog!!.setMessage("Loading")
        progressdialog!!.setCancelable(false)
    }

    private fun uiSetup() {
        binding.fullscreen2.schoolday.text = working_days
        binding.fullscreen2.presentDay.text = present_days
        binding.fullscreen2.absentDay.text = absent_days
        binding.fullscreen2.leaveDay.text = leave_days
        binding.totalSchoolDay.text = schooldays
        binding.totalPresentDay.text = total_present
        binding.totalAbsentDay.text = total_absent
        binding.totalLeaveDay.text = total_leave

        val total_days = schooldays.toInt()
        val persent = total_present.toInt()
        val absent = total_absent.toInt()
        val leave = total_leave.toInt()
        val late = total_late.toInt()
        val absent_ = (absent * 100).toDouble()
        val leave_ = (leave * 100).toDouble()
        val present_ = (persent * 100).toDouble()
        val late_ = (late * 100).toDouble()
        //Math.round((absent_ / total_days)* 100.0) / 100.0 ;
        try {
            binding.incRowAttendanceCircle.perAbsent.text =
                (Math.round((absent_ / total_days) * 100.0) / 100.0).toString() + "%"
            binding.incRowAttendanceCircle.perLeave.text =
                (Math.round((leave_ / total_days) * 100.0) / 100.0).toString() + "%"
            binding.incRowAttendanceCircle.perPresent.text =
                (Math.round((present_ / total_days) * 100.0) / 100.0).toString() + "%"
            binding.incRowAttendanceCircle.tvLateCircle.text =
                (Math.round((late_ / total_days) * 100.0) / 100.0).toString() + "%"
        } catch (e: Exception) {
        }

        val month_current = CalenderInstance.currentMonth()
        //int month_current = TryAttendanceTest2.session_month_list.get(0);
        val date_current = CalenderInstance.currentDateDD()

        if (month_current == month_) {
            when (today_status) {
                1 -> {
                    binding.relPresent.visibility = View.VISIBLE
                    binding.relPresent2.visibility = View.GONE
                }

                2 -> {
                    binding.relPresent.visibility = View.GONE
                    binding.relPresent2.visibility = View.VISIBLE
                }

                3 -> {
                    binding.textPresent1.text = "On Leave Today"
                    binding.relPresent.setBackgroundColor(resources.getColor(R.color.att_leave_color))
                    binding.textPresent.text = "L"
                    binding.textPresent.setTextColor(resources.getColor(R.color.att_leave_color))
                    binding.relPresent.visibility = View.VISIBLE
                    binding.relPresent2.visibility = View.GONE
                }

                else -> {
                    binding.relPresent.visibility = View.GONE
                    binding.relPresent2.visibility = View.GONE
                }
            }
        } else {
            binding.relPresent.visibility = View.GONE
            binding.relPresent2.visibility = View.GONE
        }
        val suffix = getDayNumberSuffix(date_current)
        val s = "$date_current<sup>$suffix</sup> "
        when (month_current) {
            1 -> month_name = "January"
            2 -> month_name = "February"
            3 -> month_name = "March"
            4 -> month_name = "April"
            5 -> month_name = "May"
            6 -> month_name = "June"
            7 -> month_name = "July"
            8 -> month_name = "August"
            9 -> month_name = "September"
            10 -> month_name = "October"
            11 -> month_name = "November"
            12 -> month_name = "December"
        }
        if (session != null) {
            binding.toolbarSerch.year.text = session;
            binding.toolbarSerch.year.visibility = VISIBLE;
        }
        binding.textAbsent2.text =
            Html.fromHtml(s).toString() + month_name + ", " + year_
        binding.textPresent2.text =
            Html.fromHtml(s).toString() + month_name + ", " + year_

        validateCircleUi(isLateEnabled)
        validateLateUI(isLateEnabled)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.try_activity_test_scroll)
        addMonthAndYear()
        isFromStaff = intent.getBooleanExtra("isFromStaff", false)
        calenderInstance()
        val bundle = intent.extras
        bundle?.let {
            studentID = it.getString("studentID", "0")
            toStartDate = it.getString("formDate", "")
            toEndDate = it.getString("tillDate", "")
            if (toStartDate.isNotEmpty() && toEndDate.isNotEmpty()) {
                CalenderInstance.currentMonth = dateToMonth(toStartDate).toInt()-1
            }
        }


        val id = intent.getIntExtra("id", 0)
        yearId = intent.getIntExtra("Year", 0)
        monthId = intent.getIntExtra("MonthId", 0)


        //setTypeface();
        userId = id.toString() + ""
        if (!isFromStaff) {
            /*String userDetails = PreferenceUtils.getString(ACTIVE_USER_DETAILS, context);
            BoardLogo loginResult = new Gson().fromJson(userDetails, BoardLogo.class);*/
            binding.toolbarSerch.year.text = "Session";
            binding.toolbarSerch.year.visibility = View.VISIBLE

        }

        progressDialog()
        init()
        initialProcess()
        var month: Int
        var year: Int
        if (!isFromStaff) {
            clickListener()


            /*userId = USER_ID_APP_USER;

            try {
                for (int i = 0; i < TryAttendanceTest2.session_month_list.size(); i++) {
                    if (CalenderInstance.currentMonth() + 1 == TryAttendanceTest2.session_month_list.get(i)) {
                        PreferenceUtils.setInt("default", i, context);

                    }
                }
                monthId = TryAttendanceTest2.session_month_list.get(CalenderInstance.currentMonth() + 1);
                yearId = TryAttendanceTest2.session_year_list.get(CalenderInstance.currenYear() + 1);
            } catch (Exception ex) {
                ex.getStackTrace();
                Toast.makeText(context, TryAttendanceTest2.server_error, Toast.LENGTH_SHORT).show();
            }*/
        } else {
        }

        binding.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                callApi(position)

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        observer()
        val ccMonthIndex =
            session_month_list.indexOfFirst { it == (CalenderInstance.currentMonth() + 1) }
        binding.viewpager.setCurrentItem(ccMonthIndex)
        callApi(ccMonthIndex)
    }

    private fun clickListener() {
        binding.toolbarSerch.year.setOnClickListener({

            /*Intent intent = new Intent(TryAttendanceTest2.this, ActivityMonthsReport.class);
                    //  Intent intent = new Intent(TryAttendanceTest2.this, WholeMonthReport.class);
                    startActivity(intent);*/


        });
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Generic.getHandler().hasMessages(0)) {
            Generic.getHandler().removeMessages(0)
        }
        finish()
    }

    private fun initialProcess() {
        //common for all
        try {
            setupViewPager(binding.viewpager)
            binding.tabs.setupWithViewPager(binding.viewpager)
            //name_user.setText(NAME_DATABASE);
            binding.tabs.setBackgroundColor(resources.getColor(R.color.brand_color))
            // rel.setBackgroundColor(getResources().getColor(R.color.app_color));
        } catch (e: Exception) {
        }

        //common for end
    }


    private fun setupViewPager(viewPager: ViewPager) {
        adapter = TryViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
    }


    private fun init() {
        binding.toolbarSerch.mainToolbarTitle2.setText(R.string.attendance)
        binding.fromTillTodayText.text = String.format(getString(R.string.from_march), "01 April")
    }


    private fun validateLateUI(isLateEnable: Boolean) {
        binding.fullscreen2.llLate.visibility = if (isLateEnable) View.VISIBLE else View.GONE
        binding.incRowAttendanceCircle.llLateCircle.visibility =
            if (isLateEnable) View.VISIBLE else View.GONE
        binding.rlLateCount.visibility =
            if (isLateEnable) View.VISIBLE else View.GONE
        if (isLateEnable) bindLateValue()
    }

    private fun bindLateValue() {
        binding.fullscreen2.tvLateCount.text = late_days
        binding.tvLateMonthCount.text = total_late
    }

    private fun callApi(position: Int = 0) {
        FragmentAPI.report_arraylist.clear()
        val month = session_month_list[position];
        val year = session_year_list[position];
        val date = getMinMaxDate(year, month)
        Log.e("rajuNewPage", "${date.first} ${date.second}")
        attendanceViewModel.getAttendance(
            from = date.first,
            till = date.second,
            yrID = yearId.toString(),
            studentID = studentID
        )
    }

    private fun observer() {
        lifecycleScope.launch {
            attendanceViewModel.attendanceStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        progressdialog?.show()
                    }

                    is NetworkResult.Error -> {
                        progressdialog?.dismiss()
                    }

                    is NetworkResult.Success -> {


                        if (it.data != null) {
                            if (it.data.errorCode == 0) {
                                it.data.let { data ->
                                    if (data.isLateEnable == true) {
                                        isLateEnabled = true
                                        late_days = "${data.lateDays}"
                                        total_late =
                                            "${data.totalLates}"
                                    }
                                    schooldays = "${data.schoolDays}"

                                    absent_days = "${data.absentDays}"

                                    leave_days = "${data.leaveDays}"

                                    present_days = "${data.presentDays}"
                                    total_absent = "${data.totalAbsent}"
                                    total_leave = "${data.totalLeave}"
                                    total_present = "${data.totalPresent}"
                                    working_days = "${data.workingDays}"
                                    session = data.academicYears[0].session
                                }
                                FragmentAPI.report_arraylist.clear()
                                it.data.attendance?.let { it1 ->

                                    for (i in it1.indices) {
                                        val report_attendance = RPT()
                                        report_attendance.attDate = it1[i].attDate
                                        report_attendance.status = it1[i].status
                                        report_attendance.late = it1[i].isLate
                                        report_attendance.duration = 1
                                        FragmentAPI.report_arraylist.add(report_attendance)
                                        if (Generic.checkCurrentDate(report_attendance.attDate)) today_status =
                                            report_attendance.status
                                    }

                                }
                                if (session_month_list.size > 0) {

                                }
                                else Toast.makeText(
                                    context,
                                    server_error,
                                    Toast.LENGTH_SHORT
                                ).show()
                                uiSetup()
                                adapter!!.notifyDataSetChanged()
                            }

                        }
                        progressdialog?.dismiss()
                    }


                    else -> {
                        progressdialog?.dismiss()
                    }
                }
            }
        }
    }

    private fun addMonthAndYear() {
        session_month_string_list.clear()
        session_year_list.clear()
        session_month_list.clear()
        var start_month: Int = 4
        var start_year: Int = 2024

        for (month in 0..11) {
            if (start_month > 11) {
                if (month > 0) {
                    start_month = start_month - 11
                    session_month_list.add(start_month)
                    start_year = start_year + 1
                    session_year_list.add(start_year)
                    switch_case_month(start_month)
                } else {
                    session_month_list.add(start_month)
                    session_year_list.add(start_year)
                    switch_case_month(start_month)
                }
            } else {
                if (month > 0) {
                    start_month = start_month + 1
                    session_month_list.add(start_month)
                    session_year_list.add(start_year)
                    switch_case_month(start_month)
                } else {
                    session_month_list.add(start_month)
                    session_year_list.add(start_year)
                    switch_case_month(start_month)
                }
            }
        }
    }

    private fun switch_case_month(start_month: Int) {
        when (start_month) {
            1 -> session_month_string_list.add(
                resources.getString(R.string.jan)
            )

            2 -> session_month_string_list.add(
                resources.getString(
                    R.string.feb
                )
            )

            3 -> session_month_string_list.add(
                resources.getString(
                    R.string.mar
                )
            )

            4 -> session_month_string_list.add(
                resources.getString(
                    R.string.apr
                )
            )

            5 -> session_month_string_list.add(
                resources.getString(
                    R.string.may
                )
            )

            6 -> session_month_string_list.add(
                resources.getString(
                    R.string.jun
                )
            )

            7 -> session_month_string_list.add(
                resources.getString(
                    R.string.jul
                )
            )

            8 -> session_month_string_list.add(
                resources.getString(
                    R.string.aug
                )
            )

            9 -> session_month_string_list.add(
                resources.getString(
                    R.string.sep
                )
            )

            10 -> session_month_string_list.add(
                resources.getString(
                    R.string.oct
                )
            )

            11 -> session_month_string_list.add(
                resources.getString(
                    R.string.nov
                )
            )

            12 -> session_month_string_list.add(
                resources.getString(
                    R.string.dec
                )
            )
        }
    }

    fun getMinMaxDate(year: Int, month: Int): Pair<String, String> {
        val yearMonth = YearMonth.of(year, month)

        val firstDay = yearMonth.atDay(1) // First day of the month
        val lastDay = yearMonth.atEndOfMonth() // Last day of the month

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return Pair(firstDay.format(formatter), lastDay.format(formatter))
    }

    companion object {
        @JvmField
        var server_error: String = "Oops ! something went wrong. Please try again."

        @JvmField
        var session_month_string_list: ArrayList<String> = ArrayList()

        @JvmField
        var session_year_list: ArrayList<Int> = ArrayList()

        @JvmField
        var session_month_list: ArrayList<Int> = ArrayList()
        var schooldays: String = "0"
        var absent_days: String = "0"
        var leave_days: String = "0"
        var late_days: String = "0"
        var present_days: String = "0"
        var total_absent: String = "0"
        var total_leave: String = "0"
        var total_present: String = "0"
        var total_late: String = "0"
        var working_days: String = "0"
        var session: String? = "0"
        var month: Int = 0
        var month_: Int = 0
        var year_: Int = 0
        var today_status: Int = 0
        var isLateEnabled: Boolean = false

    }


}
