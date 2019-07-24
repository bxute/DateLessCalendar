# DateLessCalendar
<img src="https://github.com/bxute/DateLessCalendar/blob/master/device-2019-07-24-151208.png" height="640px" width="360px"/>  <img src="https://github.com/bxute/DateLessCalendar/blob/master/device-2019-07-24-151208.png" height="640px" width="360px"/>  <img src="https://github.com/bxute/DateLessCalendar/blob/master/device-2019-07-24-151239.png" height="640px" width="360px"/>  <img src="https://github.com/bxute/DateLessCalendar/blob/master/device-2019-07-24-151245.png" height="640px" width="360px"/>
 <img src="https://github.com/bxute/DateLessCalendar/blob/master/device-2019-07-24-151254.png" height="640px" width="360px"/>

### Usage

**How to Add Streaks**
1. Prepare 2D array 
```
        //If there is only one day then it will be like {24,24}
        final int[][] streaks = {
         {1, 2},
         {4, 12}};
```
2. Set Adapter
```
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
            //Gets called twice: 1. For previous month calendar
            //            2. For Next month calendar
            @Override
            public void prefetchStreakForMonth(CalendarDayModel day) {

            }
        });
```
Inside `getStreakForMonth` method return the month requested in `CalendarDayModel day`

### Callback for month change
```
calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onMonthChanged(CalendarDayModel dayModel) {
                //Use this callback to update month and year in your UI.
            }
        });
        //Calendar Also shows month and year if `showMonthBar` is set to true
        //app:showMonthBar="true"
        //If you need to hide it and show it on your own then set `showMonthBar` to false
        //And use the above callback to get current selected month, year.
```
### Jump to given month
```
  calendarView.jumpToDate(calendarDayModel);
```


### Attributes
```

<resources>
    <declare-styleable name="HJCalenderView">
        <attr name="cellSize" format="dimension" />
        <attr name="dayTextSize" format="dimension" />
        <attr name="dayNameFormat" format="enum">
            <enum name="FULL" value="0" />
            <enum name="MEDIUM" value="1" />
            <enum name="SINGLE_CHAR" value="2" />
            <enum name="DUAL_CHAR" value="3" />
        </attr>
        <attr name="showMonthBar" format="boolean" />
    </declare-styleable>
</resources>
```
