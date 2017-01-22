package gr.eap.dxt.marmaris.tools;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import gr.eap.dxt.R;

/**
 * Created by GEO on 22/1/2017.
 */

public class MyDateTimePicker extends Dialog{

    public interface MyListener {
        void onDateSelected(Date date);
    }

    private MyListener myListener;

    private DatePicker myDatePicker;
    private TimePicker myTimePicker;
    private Date startDate;

    public MyDateTimePicker(Context context, Date startDate, MyListener myListener) {
        super(context,  R.style.my_dialog_no_title);
        this.startDate = startDate;
        this.myListener = myListener;
    }

    public void setContentAndShow(final boolean showOnlyDate, boolean showOnlyTime){
        setContentView(R.layout.dialog_date_und_time_picker);

        myDatePicker = (DatePicker) findViewById(R.id.my_date_picker);
        myTimePicker = (TimePicker) findViewById(R.id.my_time_picker);
        if (showOnlyDate) {
            myDatePicker.setVisibility(View.VISIBLE);
            myTimePicker.setVisibility(View.GONE);
        }else if(showOnlyTime){
            myDatePicker.setVisibility(View.GONE);
            myTimePicker.setVisibility(View.VISIBLE);
            myTimePicker.setIs24HourView(true);
        }else{
            myDatePicker.setVisibility(View.VISIBLE);
            myTimePicker.setVisibility(View.VISIBLE);
            myTimePicker.setIs24HourView(true);
        }

        Calendar calendar = Calendar.getInstance();
        if (startDate != null) {
            calendar.setTime(startDate);
            myDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }

        setTimePickerHourAccordingToAndroidVersion(myTimePicker, calendar.get(Calendar.HOUR_OF_DAY));
        setTimePickerMinuteAccordingToAndroidVersion(myTimePicker, calendar.get(Calendar.MINUTE));

        Button ok = (Button) findViewById(R.id.date_selected);
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, myDatePicker.getYear());
                calendar.set(Calendar.MONTH, myDatePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, myDatePicker.getDayOfMonth());

                if (showOnlyDate) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                }else{
                    calendar.set(Calendar.HOUR_OF_DAY, getTimePickerHourAccordingToAndroidVersion(myTimePicker));
                    calendar.set(Calendar.MINUTE, getTimePickerMinuteAccordingToAndroidVersion(myTimePicker));
                    calendar.set(Calendar.SECOND, 0);
                }

                myListener.onDateSelected(calendar.getTime());

                dismiss();
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (!isShowing()) show();
    }


    private static int getTimePickerHourAccordingToAndroidVersion(TimePicker mTimePicker){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getTimerHourForAndroidM(mTimePicker);
        }else{
            return getTimerHourBeforeAndroidM(mTimePicker);
        }
    }
    @TargetApi(23)
    private static int getTimerHourForAndroidM(TimePicker mTimePicker){
        if (mTimePicker == null) return 0;
        return mTimePicker.getHour();
    }
    @SuppressWarnings("deprecation")
    private static int getTimerHourBeforeAndroidM(TimePicker mTimePicker){
        if (mTimePicker == null) return 0;
        return mTimePicker.getCurrentHour();
    }


    public static int getTimePickerMinuteAccordingToAndroidVersion(TimePicker mTimePicker){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getTimerMinuteForAndroidM(mTimePicker);
        }else{
            return getTimerMinuteBeforeAndroidM(mTimePicker);
        }
    }
    @TargetApi(23)
    private static int getTimerMinuteForAndroidM(TimePicker mTimePicker){
        if (mTimePicker == null) return 0;
        return mTimePicker.getMinute();
    }
    @SuppressWarnings("deprecation")
    private static int getTimerMinuteBeforeAndroidM(TimePicker mTimePicker){
        if (mTimePicker == null) return 0;
        return mTimePicker.getCurrentMinute();
    }

    public static void setTimePickerHourAccordingToAndroidVersion(TimePicker mTimePicker, Integer hour){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTimerHourForAndroidM(mTimePicker, hour);
        }else{
            setTimerHourBeforeAndroidM(mTimePicker, hour);
        }
    }
    @TargetApi(23)
    private static void setTimerHourForAndroidM(TimePicker mTimePicker, Integer hour){
        if (mTimePicker == null || hour == null) return;
        if (hour < 0 || hour > 23) return;
        mTimePicker.setHour(hour);
    }
    @SuppressWarnings("deprecation")
    private static void setTimerHourBeforeAndroidM(TimePicker mTimePicker, Integer hour){
        if (mTimePicker == null || hour == null) return;
        if (hour < 0 || hour > 23) return;
        mTimePicker.setCurrentHour(hour);
    }


    public static void setTimePickerMinuteAccordingToAndroidVersion(TimePicker mTimePicker, Integer minute){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTimerMinuteForAndroidM(mTimePicker, minute);
        }else{
            setTimerMinuteBeforeAndroidM(mTimePicker, minute);
        }
    }
    @TargetApi(23)
    private static void setTimerMinuteForAndroidM(TimePicker mTimePicker, Integer minute){
        if (mTimePicker == null || minute == null) return;
        if (minute < 0 || minute > 59) return;
        mTimePicker.setMinute(minute);
    }
    @SuppressWarnings("deprecation")
    private static void setTimerMinuteBeforeAndroidM(TimePicker mTimePicker, Integer minute){
        if (mTimePicker == null || minute == null) return;
        if (minute < 0 || minute > 59) return;
        mTimePicker.setCurrentMinute(minute);
    }

}
