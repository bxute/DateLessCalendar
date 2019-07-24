/*
 * Developer email: hiankit.work@gmail.com
 * GitHub: https://github.com/bxute
 */

/*
 * Developer email: hiankit.work@gmail.com
 * GitHub: https://github.com/bxute
 */

package com.oneramp.hjcalendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.threeten.bp.Month;
import org.threeten.bp.format.TextStyle;

import java.util.Locale;

public class MonthView extends ViewGroup {
    public static final int DEFAULT_WEEK_LABEL_HEIGHT_DP = 36;
    private static final int NO_DATE_SELECTION = -11;
    private static final int DEFAULT_MAX_WEEKS_IN_A_MONTH = 6;
    private static String[] weeks = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static String[] dual_char = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
    private static String[] full_weeks = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static String[] char_weeks = {"M", "T", "W", "T", "F", "S", "S"};
    private final Context mContext;
    private CalendarDayModel firstDayOfMonth;
    private int weeksInThisMonth;
    private int totalHeight;
    private int dayColumnWidth;
    private int totalDays;
    private int dayNameRowHeight;
    private int firstWeekDay;
    private CalendarDayModel today;
    private int mSelectedDate = NO_DATE_SELECTION;
    private int fullWeeksHeight;
    private CalendarStreaksAdapter mEventAdapter;
    private View eventIndicator;
    private CalendarListener mCalendarListener;
    private float calendarWidth;
    private int extraVerticalSpaceOffset;
    private CalendarConfig mCalendarConfig;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    /**
     * Setter for month index.
     *
     * @param monthNumber index of month
     */
    public void setMonthNumber(int monthNumber) {
        firstDayOfMonth = CalendarDayModel.fromMonthNumber(monthNumber);
        today = CalendarDayModel.today();
        firstWeekDay = firstDayOfMonth.dayOfWeek().getValue();
        weeksInThisMonth = firstDayOfMonth.weeksCount();
        totalDays = firstDayOfMonth.daysInMonth();
        calcualateSizes();
        addWeekDays();
        addDayViews();
        requestLayout();
    }

    private void calcualateSizes() {
        dayNameRowHeight = inPx(DEFAULT_WEEK_LABEL_HEIGHT_DP);
        fullWeeksHeight = (DEFAULT_MAX_WEEKS_IN_A_MONTH * mCalendarConfig.getCellSize());
        calendarWidth = 7 * mCalendarConfig.getCellSize();
        extraVerticalSpaceOffset = ((DEFAULT_MAX_WEEKS_IN_A_MONTH - weeksInThisMonth) * mCalendarConfig.getCellSize()) / weeksInThisMonth;
        totalHeight = fullWeeksHeight + dayNameRowHeight;
    }

    private void addWeekDays() {
        String[] weekNames;
        switch (mCalendarConfig.getDayLabelFormat()) {
            case 0:
                weekNames = full_weeks;
                break;
            case 1:
                weekNames = weeks;
                break;
            case 2:
                weekNames = char_weeks;
                break;
            case 3:
                weekNames = dual_char;
                break;
            default:
                weekNames = weeks;
        }
        for (int i = 0; i < 7; i++) {
            TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.week_name_label_view,
             null, false);
            view.setText(weekNames[i]);
            if (mCalendarConfig.getDayNameTextStyle() == 0) {
                //BOLD
                view.setTypeface(null, Typeface.BOLD);
            } else {
                view.setTypeface(null, Typeface.NORMAL);
            }
            view.setTextSize(mCalendarConfig.getDayNameTextSize());
            if (i == todaysWeekDay() && isCurrentMonth()) {
                view.setTextColor(ContextCompat.getColor(getContext(),R.color.mustard));
            } else {
                view.setTextColor(Color.BLACK);
            }
            addView(view);
        }
    }

    private void addDayViews() {
        for (int i = 1; i <= totalDays; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.day_view,
             null, false);
            view.setLayoutParams(new RelativeLayout.LayoutParams(mCalendarConfig.getCellSize(), mCalendarConfig.getCellSize()));
            view.setTag(i);
            addView(view);
        }
        showStreaks();

    }

    private int inPx(int dp) {
        return (int) TypedValue
         .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources()
          .getDisplayMetrics());
    }

    private int todaysWeekDay() {
        return today.dayOfWeek().getValue();
    }

    private boolean isCurrentMonth() {
        return firstDayOfMonth.getMonth() == today.getMonth() && firstDayOfMonth.getYear() == today.getYear();
    }

    private void showStreaks() {
        deSelectRange(1, totalDays);
        updateUpcommingDays();
        if (mEventAdapter != null) {
            int[][] streakRanges = mEventAdapter.getStreakForMonth(firstDayOfMonth);
            for (int[] streakRange : streakRanges) {
                selectStreakDaysInRange(streakRange[0], streakRange[1]);
            }
        }
    }

    private void deSelectRange(int startDay, int endDay) {
        for (int i = startDay; i <= endDay; i++) {
            View leftStripe = getChildAtDate(i).findViewById(R.id.left_stripe);
            View rightStripe = getChildAtDate(i).findViewById(R.id.right_stripe);
            AppCompatImageView icon = getChildAtDate(i).findViewById(R.id.icon);
            icon.setImageResource(0);
            icon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cir_solid_ffffff_stroke_cccccc_1_3));
            leftStripe.setVisibility(View.GONE);
            rightStripe.setVisibility(GONE);
        }
    }

    private void updateUpcommingDays() {
        if (!isPastMonth(firstDayOfMonth)) {
            int startDay = (isFuture(firstDayOfMonth.getDay())) ? 1 : today.getDay();
            for (int i = startDay; i <= totalDays; i++) {
                AppCompatImageView icon = getChildAtDate(i).findViewById(R.id.icon);
                icon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.cir_solid_ecba00));
                int padding = Utils.dpToPx(getContext(), 6);
                icon.setColorFilter(0);
                icon.setPadding(padding, padding, padding, padding);
                if (isToday(i)) {
                    icon.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.cir_stroke_ecba00_1_3));
                } else {
                    icon.setBackgroundResource(0);
                }
            }
        }
    }

    private void selectStreakDaysInRange(int startDay, int endDay) {
        int startViewIndex = startDay;
        int endViewIndex = endDay;
        for (int i = startViewIndex; i <= endViewIndex; i++) {
            View leftStripe = getChildAtDate(i).findViewById(R.id.left_stripe);
            View rightStripe = getChildAtDate(i).findViewById(R.id.right_stripe);
            AppCompatImageView icon = getChildAtDate(i).findViewById(R.id.icon);
            icon.setImageResource(R.drawable.ic_tick);
            icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
            int padding = Utils.dpToPx(getContext(), 4);
            icon.setPadding(padding, padding, padding, padding);
            icon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cir_solid_ecba00));
            leftStripe.setVisibility(View.VISIBLE);
            rightStripe.setVisibility(VISIBLE);
            if (i == startViewIndex) {
                leftStripe.setVisibility(View.GONE);
            }
            if (i == endViewIndex) {
                rightStripe.setVisibility(View.GONE);
            }
        }
    }

    private View getChildAtDate(int date) {
        int index = date + 6;
        return getChildAt(index);
    }

    private boolean isPastMonth(CalendarDayModel day) {
        int currentMonth = today.getMonth();
        int currentYear = today.getYear();
        if (day.getYear() <= currentYear) {
            if (day.getYear() < currentYear) {
                return true;
            } else {
                return day.getMonth() < currentMonth;
            }
        }
        return false;
    }

    private boolean isFuture(int date) {
        return CalendarDayModel.from(date, firstDayOfMonth.getMonth(), firstDayOfMonth.getYear()).isAfter(today);
    }

    private boolean isToday(int date) {
        return (firstDayOfMonth.getYear() == today.getYear()
         && firstDayOfMonth.getMonth() == today.getMonth()
         && date == today.getDay());
    }

    private boolean isPast(int date) {
        return CalendarDayModel.from(date, firstDayOfMonth.getMonth(), firstDayOfMonth.getYear()).isBefore(today);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int parentWidth = getMeasuredWidth();
        int spaceOffset = (int) ((parentWidth - calendarWidth) / 2);
        int top = 0;
        int left = spaceOffset;
        int currentDayCol = 1;
        boolean startDayLayout = false;
        for (int i = 1; i <= childCount; ) {
            View child = getChildAt(i - 1);
            if (child.getVisibility() == View.GONE)
                continue;
            //layout if week labels
            if (i < 8) {
                i++;
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
                currentDayCol++;
            } else {
                //layout days
                if (startDayLayout) {
                    i++;
                    //layout day child which exists such as 1,2,3
                    child.layout(left,
                     top,
                     left + child.getMeasuredWidth(),
                     (top + child.getMeasuredHeight()));
                    currentDayCol++;
                } else {
                    //skip for non-existent days, advance the left and column. Such as 31 of prev Month or 1 of next month
                    left += child.getMeasuredWidth();
                    currentDayCol++;
                    if (currentDayCol >= firstWeekDay) {
                        startDayLayout = true;
                    }
                    continue;
                }
            }

            left += child.getMeasuredWidth();
            //wrap to the next row
            if (currentDayCol > 7) {
                top += child.getMeasuredHeight() + 0;//extraVerticalSpaceOffset;
                left = spaceOffset;
                currentDayCol = 1;
            }
        }
    }

    @Override
    public String toString() {
        return String.valueOf(Month.of(firstDayOfMonth.getMonth()).getDisplayName(TextStyle.SHORT, Locale.getDefault()));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight = totalHeight;
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(parentWidth, measuredHeight);
        //measure childs
        dayColumnWidth = mCalendarConfig.getCellSize();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int childWidth = dayColumnWidth;
            int childHeight;
            if (i < 7) {
                childHeight = dayNameRowHeight;
            } else {
                childHeight = mCalendarConfig.getCellSize();
            }
            int childHeighSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            view.measure(childWidthSpec, childHeighSpec);
        }
    }

    public void setEventAdapter(CalendarStreaksAdapter eventAdapter) {
        this.mEventAdapter = eventAdapter;
    }

    public void setCalendarListener(CalendarListener mCalendarListener) {
        this.mCalendarListener = mCalendarListener;
    }

    public void setCalendarConfig(CalendarConfig calendarConfig) {
        this.mCalendarConfig = calendarConfig;
    }
}
