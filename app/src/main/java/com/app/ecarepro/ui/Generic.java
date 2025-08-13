package com.app.ecarepro.ui;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Abhinav Singh on 12/15/2017.
 * <p>
 * Generic
 */

public class Generic {

    public static final int DIALOG_LOADING = 1, DIALOG_NOT_LOADING = 0;
    private static Dialog dialogDis;
    private static int currentPage = 0;
    private static Handler handler = new Handler();
    private Context context;
    private static Dialog dialog;
    private final static int SECOND_MILLIS = 1000;
    private final static int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private final static int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private final static int DAY_MILLIS = 24 * HOUR_MILLIS;

    /***************************************************************
     * Hide Keyboard anywhere in activity
     * @param context is the Context of the Activity have to be shown
     * @param getViewContext is used to get the focus of the current screen
     * in current activity just use getViewContext it will return View
     ****************************************************************/
    public static void hideKeyboard(Context context, View getViewContext) {
        if (getViewContext != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            if (imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(getViewContext.getWindowToken(), 0);
            }
        }
    }

    public static void showKeyboard(Context context, View getViewContext) {
        if (getViewContext != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(getViewContext, 0);
        }
    }

    /***************************************************************
     * This method show SnackBar on Any Activity
     * @param activity is the Activity in which SnackBar have to be shown
     * @param snackTitle is the text which have to be shown in Snackbar @snackbar
     ****************************************************************/
    public static void setSnackBar(Activity activity, String snackTitle) {
        Snackbar snackbar = Snackbar.make(getViewContext(activity), snackTitle, Snackbar.LENGTH_SHORT);
        tvCenterHorizontal(getSBTextView(snackbar));
        snackbar.show();
    }









    private static void tvCenterHorizontal(TextView textView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private static View getSBView(Snackbar snackbar) {
        return snackbar.getView();
    }

    private static TextView getSBTextView(Snackbar snackbar) {
        return getSBView(snackbar).findViewById(com.google.android.material.R.id.snackbar_text);
    }

    public static ViewGroup getViewContext(Activity activity) {
        return (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    /***************************************************************
     * This method handel the auto slide functionality for ViewPager
     * @param vPager is the ViewPager
     * get @currentPage from ViewPager by vPager.getCurrentItem();
     * @param al_size is the size of albumDetailArrayList which is ArrayList<AlbumDetail>
     ****************************************************************/
    private static void autoSlider(final ViewPager vPager, final int al_size) {

        currentPage = vPager.getCurrentItem();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == al_size) {
                    currentPage = 0;
                }
                vPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
    }

    public static Handler getHandler() {
        return handler;
    }

    public static Handler getNewHandler() {
        return new Handler();
    }

    public static Handler getHandler2() {

        Handler handler = new Handler();

        handler.post(new Runnable() {

            public void run() {
                //ui stuff here :)
            }
        });
        return handler;
    }





    public static String convertInt2String(int convert) {
        return String.valueOf(convert);
    }



    public static boolean isEqualsIgnoreCase(String firstValue, String secondValue) {
        return firstValue.trim().equalsIgnoreCase(secondValue.trim());
    }


    public static void LogE(String tag, String message) {
        Log.e(tag, message);
    }

    public static void LogE(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }




    public static long convertApiDateTimeToTimeStamp(String serverDate) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS", Locale.ENGLISH).parse(serverDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long currentDateTimeStamp() {
        return System.currentTimeMillis();
    }

    public static int convertTimeStampToYear(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStamp));
/*        calendar.get(Calendar.YEAR);
        calendar.get(Calendar.MONTH);*/
        return calendar.get(Calendar.YEAR);
    }





    public static void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0, // fromXDelta
                0, // toXDelta
                view.getHeight(), // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0, // fromXDelta
                0, // toXDelta
                0, // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        // view.setVisibility(View.GONE);
    }

    public static String getDateTimeFormatted(String DateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date dateTime = null;
        try {
            dateTime = simpleDateFormat.parse(DateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat finalDate = new SimpleDateFormat("dd MMM, yyyy");
        SimpleDateFormat finalTime = new SimpleDateFormat("hh:mm aa");
        String formattedDate = finalDate.format(dateTime).toString();
        String formattedTime = finalTime.format(dateTime).toString().toUpperCase();
        return formattedDate + " at " + formattedTime;
    }



    public static String getDateFormatted(String DateTime) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date dateTime = simpleDateFormat.parse(DateTime);
        SimpleDateFormat finalDate = new SimpleDateFormat("dd MMM, yyyy");
        /*SimpleDateFormat finalTime = new SimpleDateFormat("hh:mm aa");
        String formattedTime = finalTime.format(dateTime).toString().toUpperCase();*/
        return finalDate.format(dateTime);
    }

    public static String getMonth(String DateTime) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date dateTime = simpleDateFormat.parse(DateTime);
        SimpleDateFormat finalDate = new SimpleDateFormat("MMMM");
        return finalDate.format(dateTime);
    }

    public static String getWeekDay(String DateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date dateTime = null;
        try {
            dateTime = simpleDateFormat.parse(DateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        DateFormat format2 = new SimpleDateFormat("EEEE");
        return format2.format(dateTime);
    }

    public static String getYear(String DateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date dateTime = null;
        try {
            dateTime = simpleDateFormat.parse(DateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        DateFormat format2 = new SimpleDateFormat("yyyy");
        return format2.format(dateTime);
    }

    public static boolean checkDateRange(String toDate, String fromDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"/*,Locale.ENGLISH*/);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = simpleDateFormat.parse(toDate);
            date2 = simpleDateFormat.parse(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (null != date1 && null != date2) {
            if (date1.getTime() > date2.getTime())
                return false;
            else return true;
        }
        return false;
    }
    public static boolean checkDateRangeLessonPlan(String toDate, String fromDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"/*,Locale.ENGLISH*/);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = simpleDateFormat.parse(toDate);
            date2 = simpleDateFormat.parse(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (null != date1 && null != date2) {
            if (date1.getTime() >= date2.getTime())
                return false;
            else return true;
        }
        return false;
    }

    public static boolean checkCurrentDate(String dt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        Date dateTime = null;
        SimpleDateFormat finalDate = new SimpleDateFormat("dd MMM, yyyy");
        try {
            dateTime = simpleDateFormat.parse(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (null != dateTime) {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM, yyyy");
            if (finalDate.format(dateTime).equalsIgnoreCase(df.format(c)))
                return true;
        }
        return false;
    }

    public static Bitmap rotateImageIfRequired(String imagePath) throws IOException {
        Bitmap img = BitmapFactory.decodeFile(imagePath);
        ExifInterface ei = new ExifInterface(imagePath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Log.d("orientation", "" + orientation);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            case ExifInterface.ORIENTATION_UNDEFINED:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }



    public static Double getDateDiff(String dateString1, String dateString2) {
        Double diff = 0.0;
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        while (!cal1.after(cal2)) {
            diff++;
            cal1.add(Calendar.DATE, 1);
        }
        return diff;
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
