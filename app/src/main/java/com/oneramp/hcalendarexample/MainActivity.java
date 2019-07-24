package com.oneramp.hcalendarexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.oneramp.hjcalendar.CalendarDayModel;
import com.oneramp.hjcalendar.CalendarListener;
import com.oneramp.hjcalendar.CalendarStreaksAdapter;
import com.oneramp.hjcalendar.HJCalenderView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalendarDayModel calendarDayModel = CalendarDayModel.from(23,6,2019);
        HJCalenderView calendarView = findViewById(R.id.clcl);
        //Streaks must be in pairs always.
        //If there is only one day then it will be like {24,24}
        final int[][] streaks = {
         {1, 2},
         {4, 12}};
        calendarView.setStreakAdapter(new CalendarStreaksAdapter() {
            //Return streak of current month
            @Override
            public int[][] getStreakForMonth(CalendarDayModel day) {
                if (day.getMonth() == 7) {
                    return streaks;
                }
                return new int[0][];
            }

            //This callback is for prefetching data for adjacent months.
            //Gets twice: 1. For previous month calendar
            //            2. For Next month calendar
            @Override
            public void prefetchStreakForMonth(CalendarDayModel day) {

            }
        });
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onMonthChanged(CalendarDayModel dayModel) {
                //Use this callback to update month and year in your UI.
            }
        });
        //Calendar Also shows month and year if `showMonthBar` is set to true
        //app:showMonthBar="true"
        //If you need to hide it and show it on your own then set `showMonthBar` is set to false
        // and use the above callback to get current selected date.
        calendarView.jumpToDate(calendarDayModel);
    }
}
