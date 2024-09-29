package com.app.ecarepro.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.ecarepro.ui.month_list.TryFragmentEight;
import com.app.ecarepro.ui.month_list.TryFragmentEleven;
import com.app.ecarepro.ui.month_list.TryFragmentFive;
import com.app.ecarepro.ui.month_list.TryFragmentFour;
import com.app.ecarepro.ui.month_list.TryFragmentNine;
import com.app.ecarepro.ui.month_list.TryFragmentOne;
import com.app.ecarepro.ui.month_list.TryFragmentSeven;
import com.app.ecarepro.ui.month_list.TryFragmentSix;
import com.app.ecarepro.ui.month_list.TryFragmentTen;
import com.app.ecarepro.ui.month_list.TryFragmentThree;
import com.app.ecarepro.ui.month_list.TryFragmentTwelve;
import com.app.ecarepro.ui.month_list.TryFragmentTwo;

public class TryViewPagerAdapter extends FragmentPagerAdapter {

    public TryViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TryFragmentOne();
            case 1:
                return new TryFragmentTwo();
            case 2:
                return  new TryFragmentThree();
            case 3:
                return new TryFragmentFour();
            case 4:
                return new TryFragmentFive();
            case 5:
                return new TryFragmentSix();
            case 6:
                return new TryFragmentSeven();
            case 7:
                return new TryFragmentEight();
            case 8:
                return new TryFragmentNine();
            case 9:
                return new TryFragmentTen();
            case 10:
                return new TryFragmentEleven();
            case 11:
                return new TryFragmentTwelve();
            default:
                return null;
        }
    }


/*    @Override
    public Fragment getItem(int position) {
        TryFragmentOneTemp tempFragment = new TryFragmentOneTemp();
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(0));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(0));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 1:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(1));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(1));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 2:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(2));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(2));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 3:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(3));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(3));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 4:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(4));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(4));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 5:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(5));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(5));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 6:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(6));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(6));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 7:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(7));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(7));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 8:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(8));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(8));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 9:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(9));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(9));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 10:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(10));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(10));
                tempFragment.setArguments(bundle);
                return tempFragment;
            case 11:
                bundle.putInt("year", TryAttendanceTest2.session_year_list.get(11));
                bundle.putInt("month", TryAttendanceTest2.session_month_list.get(11));
                tempFragment.setArguments(bundle);
                return tempFragment;
            default:
                return null;
        }
    }*/

    @Override
    public int getCount() {
        return 12; //mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position != -1 && position <= 11) {
            return TryAttendanceTest2.session_month_string_list.get(position);
        } else {
            return "Month";
        }
       /* switch (position) {
            case 0:
                return MainActivity.session_month_string_list.get(0);
            case 1:
                return MainActivity.session_month_string_list.get(1);
            case 2:
                return MainActivity.session_month_string_list.get(2);
            case 3:
                return MainActivity.session_month_string_list.get(3);
            case 4:
                return MainActivity.session_month_string_list.get(4);
            case 5:
                return MainActivity.session_month_string_list.get(5);
            case 6:
                return MainActivity.session_month_string_list.get(6);
            case 7:
                return MainActivity.session_month_string_list.get(7);
            case 8:
                return MainActivity.session_month_string_list.get(8);
            case 9:
                return MainActivity.session_month_string_list.get(9);
            case 10:
                return MainActivity.session_month_string_list.get(10);
            case 11:
                return MainActivity.session_month_string_list.get(11);
            default:
                return "Month";
        }*/
    }
}
