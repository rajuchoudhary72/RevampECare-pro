package com.app.ecarepro.ui;



import com.app.ecarepro.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Abhinav on 12/23/2017.
 */

public class CalenderInstance {

    public static GregorianCalendar gCalendar;
    public static Calendar calendar;


    public static int BLUE = R.color.holiday_color;
    public static int ABSENT = R.color.absent_red;
    public static int PRESENT = R.color.disabled;
    public static int currentMonth;
    public static int currentYear;
    public static int currentDateDD;

    public static void setCalendar() {
        CalenderInstance.calendar = Calendar.getInstance();
    }

    public static void setGCalendar(int yy, int mm, int dd) {
        CalenderInstance.gCalendar = new GregorianCalendar(yy, mm, dd);
    }

    public static int getWeekDayF() {
        return gCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int setCDayMonth() {
        return gCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int setSunday() {
        return Calendar.SUNDAY;
    }

    public static int daysInMonth() {
        return gCalendar.get(Calendar.DATE);
    }

/*    public static void printMonth(){
        int daysInMonth = daysInMonth();
        while(daysInMonth >= 1){
            Log.e("days in month", daysInMonth + "");
            daysInMonth--;
        }
    }*/

    public static int currentMonth() {
        return currentMonth;
    }
    public static int currentDateDD() {
        return currentDateDD;
    }
    public static int currenYear() {
        return currentYear;
    }

}
