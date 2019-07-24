package com.oneramp.hcalendarexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.oneramp.hjcalendar.CalendarDayModel;
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
        calendarView.setEventAdapter(new CalendarStreaksAdapter() {
            @Override
            public int[][] getStreakForMonth(CalendarDayModel day) {
                if (day.getMonth() == 7) {
                    return streaks;
                }
                return new int[0][];
            }

            @Override
            public void prefetchStreakForMonth(CalendarDayModel day) {

            }
        });
        calendarView.jumpToDate(calendarDayModel);
    }
}
