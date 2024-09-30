package com.app.ecarepro.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.app.ecarepro.R;
import com.app.ecarepro.ui.month_list.FragmentAPI;
import com.app.ecarepro.utils.UtilsKt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Chandan on 30/11/2017.
 */

public class TryRVCellAdapter extends RecyclerView.Adapter<TryRVCellAdapter.ViewHolder> {
    private static final int DAY_OFFSET = 1;
    private static int month, year;
    private final List<String> list = new ArrayList<>();
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    // private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private Context context;
    private boolean isTemp=false;
    private String temp;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TryRVCellAdapter() {
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TryRVCellAdapter(Context context, int month, int year) {
        this.context = context;
        TryRVCellAdapter.month = month;
        TryRVCellAdapter.year = year;

        // Print Month
        printMonth(month, year);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test3, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,  int position) {

        // ACCOUNT FOR SPACING

        String[] day_color = list.get(position).split("-");
        String theday = day_color[0];
        String themonth = day_color[2];
        String theyear = day_color[3];

        switch (day_color[1]) {
            case "BLUE":
                holder.gridcell.setTextColor(context.getResources().getColor(R.color.holiday_color));
                break;
            case "present":
                holder.gridcell.setTextColor(context.getResources().getColor(R.color.white));
                holder.gridcell.setBackground(context.getResources().getDrawable(R.drawable.circle_present));

                break;
            case "late":
                holder.gridcell.setTextColor(context.getResources().getColor(R.color.white));
                holder.gridcell.setBackground(context.getResources().getDrawable(R.drawable.circle_late));
                break;
            case "absent":
                holder.gridcell.setTextColor(context.getResources().getColor(R.color.white));
                holder.gridcell.setBackground(context.getResources().getDrawable(R.drawable.circle_absent));
                break;
            case "leave":
                holder.gridcell.setTextColor(context.getResources().getColor(R.color.white));
                holder.gridcell.setBackground(context.getResources().getDrawable(R.drawable.circle_leave));
                holder.gridcell2.setText("L");
                break;
            case "holiday":
                holder.gridcell.setTextColor(context.getResources().getColor(R.color.white));
                holder.gridcell.setBackground(context.getResources().getDrawable(R.drawable.circle_calender_holiday));
                holder.gridcell2.setText("H");
                break;
            case "White":
                holder.gridcell.setTextColor(context.getResources().getColor(R.color.white));
                break;
            default:
                break;
        }
        holder.gridcell.setText(theday);
        FragmentAPI.report_arraylist.clear();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] day_color = list.get(holder.getAdapterPosition()).split("-");
                if(day_color[1].equalsIgnoreCase("present"))
                {
                    if(day_color[4].equalsIgnoreCase("null")&&day_color[5].equalsIgnoreCase("null")&&day_color[6].equalsIgnoreCase("null")&&day_color[6].equalsIgnoreCase("null")){

                    }else {
                        showTempPopUp(day_color[0]+"-"+day_color[2]+"-"+day_color[3],day_color[4],day_color[5],day_color[6],day_color[7]);
                    }


                    //Toast.makeText(context, "Present"+day_color[4], Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showTempPopUp(String dt,String temp,String time,String rfdIn,String rfdOut)
    {
        ImageView ivCross;
        TextView tvDate;
        LinearLayout llTempInfo,llRFDInfo;
        CustomTextView ctvTemp,ctvTime,ctvrfdTime,ctvrfdTemp;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (null != dialog.getWindow())
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Animations;
        dialog.setContentView(R.layout.custom_popup_attendance_temp);
        ivCross=dialog.findViewById(R.id.ivCross);
        tvDate=dialog.findViewById(R.id.tvDate);
        llTempInfo=dialog.findViewById(R.id.llTempInfo);
        llRFDInfo=dialog.findViewById(R.id.llRFDInfo);
        ctvTemp=dialog.findViewById(R.id.ctvTemp);
        ctvTime=dialog.findViewById(R.id.ctvTime);
        ctvrfdTime=dialog.findViewById(R.id.ctvrfdTime);
        ctvrfdTemp=dialog.findViewById(R.id.ctvrfdTemp);
        tvDate.setText(dt);

        if ( !time.equalsIgnoreCase("null")){
            llTempInfo.setVisibility(View.VISIBLE);
            ctvTemp.setText(temp +" F");
            ctvTime.setText(time);
        }else{
            llTempInfo.setVisibility(View.GONE);
        }

        if ( !rfdIn.equalsIgnoreCase("null")){
            llRFDInfo.setVisibility(View.VISIBLE);
            ctvrfdTemp.setText(rfdOut);
            ctvrfdTime.setText(rfdIn);
        }else{
            llRFDInfo.setVisibility(View.GONE);
        }

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private String getMonthAsString(int i) {
        return months[i];
    }

    private int getNumberOfDaysOfMonth(int yy,int mm) {
        Calendar mycal = new GregorianCalendar(yy, mm, 1);

// Get the number of days in that month
        return mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        // return daysOfMonth[i];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    /**
     * Prints Month
     *
     * @param mm
     * @param yy
     */
    private void printMonth(int mm, int yy) {
        // The number of days to leave blank at
        // the start of this month.
        int trailingSpaces;
        int daysInPrevMonth;
        int prevMonth;
        int prevYear;
        int nextMonth;
        int nextYear;
        boolean found = false;

        int currentMonth = mm - 1;
        int daysInMonth = getNumberOfDaysOfMonth(yy,currentMonth);

        switch (currentMonth) {
            case 11:
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(yy,prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
                break;
            case 0:
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(yy,prevMonth);
                nextMonth = 1;
                break;
            default:
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(yy,prevMonth);
                break;
        }
        // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
        CalenderInstance.setGCalendar(yy, currentMonth, 1);

        // Compute how much to leave before before the first day of the
        // month.
        // getDay() returns 0 for Sunday.
        trailingSpaces = CalenderInstance.gCalendar.get(Calendar.DAY_OF_WEEK) - 1;

        /*if (CalenderInstance.gCalendar.isLeapYear(CalenderInstance.gCalendar.get(Calendar.YEAR)) && mm == 1) {
            ++daysInMonth;
        }*/

        // Trailing Month days
        for (int i = 0; i < trailingSpaces; i++) {
            list.add((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i + "-White" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
        }
        CalenderInstance.setCalendar();
        for (int day = 1; day <= daysInMonth; day++) {
            CalenderInstance.calendar.set(year, month - 1, day);
            Calendar mycal = new GregorianCalendar(yy, currentMonth, 1);
            if (FragmentAPI.report_arraylist.size() > 0) {
                for (int aSize = 0; aSize < FragmentAPI.report_arraylist.size(); aSize++) {

                    if (day < Integer.parseInt(UtilsKt.date_converterDay(FragmentAPI.report_arraylist.get(aSize).getAttDate()))) {
                        found = false;
                        break;
                    } else if (day == Integer.parseInt(UtilsKt.date_converterDay(FragmentAPI.report_arraylist.get(aSize).getAttDate()))) {
                        found = true;

                        switch (FragmentAPI.report_arraylist.get(aSize).getStatus()) {
                            case 1:
                                if (!FragmentAPI.report_arraylist.get(aSize).getLate())
                                    list.add(day + "-present" + "-" + getMonthAsString(currentMonth) + "-" + yy+"-"+FragmentAPI.report_arraylist.get(aSize).getTemp()+"-"+FragmentAPI.report_arraylist.get(aSize).getMarkedTime()+"-"+FragmentAPI.report_arraylist.get(aSize).getRFTagIn()+"-"+FragmentAPI.report_arraylist.get(aSize).getRFTagOut());
                                else
                                    list.add(day + "-late" + "-" + getMonthAsString(currentMonth) + "-" + yy+"-"+FragmentAPI.report_arraylist.get(aSize).getTemp());
                                break;
                            case 2:
                                list.add(day + "-absent" + "-" + getMonthAsString(currentMonth) + "-" + yy+"-"+FragmentAPI.report_arraylist.get(aSize).getTemp());
                                break;
                            case 3:
                                list.add(day + "-leave" + "-" + getMonthAsString(currentMonth) + "-" + yy+"-"+FragmentAPI.report_arraylist.get(aSize).getTemp());
                                break;
                            case 6:
                                list.add(day + "-holiday" + "-" + getMonthAsString(currentMonth) + "-" + yy+"-"+FragmentAPI.report_arraylist.get(aSize).getTemp());
                                break;
                            case 5:
                                if (FragmentAPI.report_arraylist.get(aSize).getDuration() > 0) {
                                    for (int i = 0; i < FragmentAPI.report_arraylist.get(aSize).getDuration(); i++) {
                                        if ((day) <= mycal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                                            list.add(day + "-holiday" + "-" + getMonthAsString(currentMonth) + "-" + yy+"-"+FragmentAPI.report_arraylist.get(aSize).getTemp());
                                            if (i < (FragmentAPI.report_arraylist.get(aSize).getDuration() - 1))
                                                day++;
                                        }
                                    }
                                } else
                                    list.add(day + "-holiday" + "-" + getMonthAsString(currentMonth) + "-" + yy+"-"+FragmentAPI.report_arraylist.get(aSize).getTemp());
                                break;
                            default:
                                list.add(day + "-nothing" + "-" + getMonthAsString(currentMonth) + "-" + yy+"-"+FragmentAPI.report_arraylist.get(aSize).getTemp());
                                break;
                        }
                        break;
                    }

                    found = false;
                }

            } else {
                found = false;
            }

            if (!found) {
                found = false;
                int dayOfWeek = CalenderInstance.calendar.get(Calendar.DAY_OF_WEEK);

                switch (dayOfWeek) {
                    case Calendar.SUNDAY:
                        list.add(String.valueOf(day) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
                        // Or do whatever you need to with the result.
                        break;
                    default:
                        list.add(String.valueOf(day) + "-GREY" + "-" + getMonthAsString(currentMonth) + "-" + yy);
                        break;
                }
            }
        }
        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(i + 1 + "-White" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView gridcell, gridcell2;

        public ViewHolder(View view) {
            super(view);
            gridcell = view.findViewById(R.id.calendar_day_gridcell);
            gridcell2 = view.findViewById(R.id.calendar_day_gridcell2);

        }
    }

}
