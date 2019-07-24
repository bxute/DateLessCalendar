/*
 * Developer email: hiankit.work@gmail.com
 * GitHub: https://github.com/bxute
 */
package com.oneramp.hjcalendar;

/**
 * Abstract class to be implemented by user of {@code CleanCalendarView}
 */
public abstract class CalendarStreaksAdapter {
    public abstract int[][] getStreakForMonth(CalendarDayModel day);

    public abstract void prefetchStreakForMonth(CalendarDayModel day);
}
