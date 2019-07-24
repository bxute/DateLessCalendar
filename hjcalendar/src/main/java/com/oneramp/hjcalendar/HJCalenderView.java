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
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.format.TextStyle;

import java.util.Locale;

import static com.oneramp.hjcalendar.MonthView.DEFAULT_WEEK_LABEL_HEIGHT_DP;

public class HJCalenderView extends ViewGroup {
    public static final int MIN_YEAR = 1800;
    public static final int MAX_YEAR = 2200;
    public static final int DEFAULT_CELL_SIZE = 38;
    private final int MONTH_VIEW_HEIGHT_DP = 36;
    private int dayNameTextSize;
    private int dayLabelFormat;
    private float cellDimension;
    private boolean showMonthBar;
    private Context mContext;
    private ViewPager viewPager;
    //Adapter for providing event information.
    private CalendarStreaksAdapter eventAdapter;
    private MonthViewAdapter monthViewAdapter;
    private TextView monthNameView;
    private int monthYearBarHeight;
    private int calendarHeight;
    private CalendarListener calendarListener;
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            CalendarDayModel dayModel = CalendarDayModel.fromMonthNumber(position);
            dispatchMonthChangedCall(dayModel);
            dispatchPrefetchCall(dayModel);
            updateUi();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private CalendarDayModel today;

    public HJCalenderView(Context context) {
        this(context, null);
    }

    public HJCalenderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HJCalenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.HJCalenderView);
            showMonthBar = a.getBoolean(R.styleable.HJCalenderView_showMonthBar,
             true);
            cellDimension = a.getDimension(R.styleable.HJCalenderView_cellSize,
             DEFAULT_CELL_SIZE);
            dayLabelFormat = a.getInt(R.styleable.HJCalenderView_dayNameFormat,
             1);
            dayNameTextSize = (int) a.getDimension(R.styleable.HJCalenderView_dayTextSize, 16);
            a.recycle();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        initialize(context);
    }

    private void initialize(Context context) {
        this.mContext = context;
        this.today = CalendarDayModel.today();
        //calculate sizes
        calendarHeight = (int) (inPx(DEFAULT_WEEK_LABEL_HEIGHT_DP) + (6 * cellDimension));
        monthYearBarHeight = showMonthBar ? inPx(MONTH_VIEW_HEIGHT_DP) : 0;
        setupViewPager(context);
        dispatchPrefetchCall(today);
        if (showMonthBar) {
            addView(monthNameView);
        }
        addView(viewPager);
        dispatchMonthChangedCall(today);
    }

    private int inPx(int dp) {
        return (int) TypedValue
         .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources()
          .getDisplayMetrics());
    }

    private void setupViewPager(Context context) {
        monthViewAdapter = new MonthViewAdapter();
        monthViewAdapter.setCalendarCellConfig(new CalendarConfig((int) cellDimension,
         dayLabelFormat,
         dayNameTextSize));
        monthViewAdapter.setEventAdapter(eventAdapter);
        View view = LayoutInflater.from(context).inflate(R.layout.calendar_top_bar,
         null, false);
        monthNameView = view.findViewById(R.id.month_name);
        viewPager = new ViewPager(mContext);
        viewPager.setId(R.id.pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setOnPageChangeListener(pageChangeListener);
        viewPager.setPageTransformer(false,
         new ViewPager.PageTransformer() {
             @Override
             public void transformPage(@NonNull View page, float position) {
                 position = (float) Math.sqrt(1 - Math.abs(position));
                 page.setAlpha(position);
             }
         });
        viewPager.setAdapter(monthViewAdapter);
        viewPager.setCurrentItem(today.monthNumber());
    }

    private void dispatchPrefetchCall(CalendarDayModel today) {
        if (eventAdapter != null) {
            //current month
            eventAdapter.prefetchStreakForMonth(today);
            LocalDate prevMonth = today.getDate().minusMonths(1);
            //for prev month
            eventAdapter.prefetchStreakForMonth(CalendarDayModel.from(prevMonth));
            //for next month
            LocalDate nextMonth = today.getDate().plusMonths(1);
            eventAdapter.prefetchStreakForMonth(CalendarDayModel.from(nextMonth));
        }
    }

    private void dispatchMonthChangedCall(CalendarDayModel dayModel) {
        if (calendarListener != null) {
            calendarListener.onMonthChanged(dayModel);
        }
    }

    public void setCellDimension(float cellDimension) {
        this.cellDimension = cellDimension;
        initialize(mContext);
    }

    public void setDayNameTextSize(int dayNameTextSize) {
        this.dayNameTextSize = dayNameTextSize;
    }

    public void setDayLabelFormat(int dayLabelFormat) {
        this.dayLabelFormat = dayLabelFormat;
    }

    public void setShowMonthBar(boolean showMonthBar) {
        this.showMonthBar = showMonthBar;
    }

    public void setEventAdapter(CalendarStreaksAdapter eventAdapter) {
        this.eventAdapter = eventAdapter;
        if (monthViewAdapter != null) {
            monthViewAdapter.setEventAdapter(eventAdapter);
        }
    }

    private void updateUi() {
        if (showMonthBar) {
            int monthNumber = viewPager.getCurrentItem();
            CalendarDayModel day = CalendarDayModel.fromMonthNumber(monthNumber);
            String year = String.valueOf(day.getYear());
            String month = Month.of(day.getMonth()).getDisplayName(TextStyle.FULL, Locale.getDefault());
            monthNameView.setText(String.format("%s, %s", month, year));
        }
    }

    public void jumpToDate(CalendarDayModel calendarDay) {
        if (calendarDay
         .isInRange(
          CalendarDayModel.from(LocalDate.of(MIN_YEAR, 1, 1)),
          CalendarDayModel.from(LocalDate.of(MAX_YEAR, 12, 31)))) {
            int monthNumber = calendarDay.monthNumber();
            viewPager.setCurrentItem(monthNumber);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int top = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE)
                continue;
            int childHeight = child.getMeasuredHeight();
            child.layout(l, top, r, top + childHeight);
            top += childHeight;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(parentWidth, calendarHeight + monthYearBarHeight);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            int childWidthMeasureSpec;
            int childHeightMeasureSpec;
            if (i == 0 && showMonthBar) {
                //month view
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY);
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(monthYearBarHeight, MeasureSpec.EXACTLY);
            } else {
                //pager
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY);
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(calendarHeight, MeasureSpec.AT_MOST);
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    public void setCalendarListener(CalendarListener calendarListener) {
        this.calendarListener = calendarListener;
        if (monthViewAdapter != null) {
            monthViewAdapter.setCalendarListener(calendarListener);
        }
    }
}
