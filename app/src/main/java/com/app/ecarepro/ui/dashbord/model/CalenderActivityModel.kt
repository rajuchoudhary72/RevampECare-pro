package com.app.ecarepro.ui.dashbord.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.calenderEventView
import com.app.ecarepro.data.network.model.Activity
import com.app.ecarepro.databinding.CalendarHeaderBinding
import com.app.ecarepro.databinding.ItemCalendarDayBinding
import com.app.ecarepro.databinding.ItemCalenderActivityBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalenderActivityModel(val activities: List<Activity>) :
    ViewBindingKotlinModel<ItemCalenderActivityBinding>(R.layout.item_calender_activity) {

    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")


    override fun ItemCalenderActivityBinding.bind() {
        eventsView.addItemDecoration(DividerItemDecoration(eventsView.context, RecyclerView.VERTICAL))
        calendarView.monthScrollListener = {
            calenderTitle.text = if (it.yearMonth.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }
            // Select the first day of the visible month.
            selectDate(it.yearMonth.atDay(1))
        }

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(50)
        val endMonth = currentMonth.plusMonths(50)
        configureBinders(daysOfWeek)
        calendarView.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        btnNextMonth.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        btnPreviewMonth.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
    }


    private fun ItemCalenderActivityBinding.selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { calendarView.notifyDateChanged(it) }
            calendarView.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun ItemCalenderActivityBinding.updateAdapterForDate(date: LocalDate) {
        val events = getEvents(date)
        buildEventModel(events)
        selectedDateText.text = selectionFormatter.format(date)
        selectedDateText.isVisible = events.isEmpty().not()
    }

    private fun getEvents(date: LocalDate): List<Activity> {
        val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH)
        return activities.filter { activity ->
            try {
                activity.fromDate?.let {
                    val activityDate = LocalDate.parse(it, formatter)
                    activityDate == date
                } ?: false
            }catch (e:Exception){
                e.printStackTrace()
                false
            }
        }
    }
    private fun ItemCalenderActivityBinding.buildEventModel(events: List<Activity>) {
        eventsView.isVisible = events.isNotEmpty()
        eventsView.withModels {
            events.forEach {
                calenderEventView {
                    id(it.title)
                    title(it.title)
                }
            }
        }
    }

    private fun ItemCalenderActivityBinding.configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = ItemCalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.binding.exThreeDayText
                val dotView = container.binding.exThreeDotView

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.makeVisible()
                    when (data.date) {
                        today -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.calender_today_bg)
                            dotView.makeInVisible()
                        }

                        selectedDate -> {
                            textView.setTextColorRes(R.color.blue_headline)
                            textView.setBackgroundResource(R.drawable.example_3_selected_bg)
                            dotView.makeInVisible()
                        }

                        else -> {
                            textView.setTextColorRes(R.color.black)
                            textView.background = null
                            val events = getEvents(data.date)
                            dotView.isVisible = events.isNotEmpty()
                            dotView.isClickable = events.isNotEmpty()
                            val isWorkingDay = events.all { it.isWorking == true }
                            dotView.setBackgroundResource(if(isWorkingDay) R.drawable.calender_today_bg else R.drawable.calender_today_bg_red)
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }
        calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = true
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].name.first().toString()
                                tv.setTextColorRes(R.color.black)
                            }
                    }
                }
            }
    }

}


fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}


fun dpToPx(dp: Int, context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        context.resources.displayMetrics,
    ).toInt()

internal val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

internal val Context.inputMethodManager
    get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int): Drawable =
    requireNotNull(ContextCompat.getDrawable(this, drawable))

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))