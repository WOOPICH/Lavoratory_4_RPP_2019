package com.example.myapplication4;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ConfigActivity extends Activity {

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    static String NUMOFDAYS;
    Button date;
    TextView preview;
    Calendar cal = Calendar.getInstance();
    Intent resultValue;
    DatePickerDialog.OnDateSetListener dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // и проверяем его корректность
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // формируем intent ответа
        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

        // отрицательный ответ
        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.config);


        date = (Button)findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                new DatePickerDialog(ConfigActivity.this,dpd,
                        cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        dpd = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //Создание напоминания
                Calendar notifTime = Calendar.getInstance();
                notifTime.set(year,monthOfYear,dayOfMonth,9,0);
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(ConfigActivity.this, "M_CH_ID")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Событие")
                                .setContentText("Настало")
                                .setWhen(notifTime.getTimeInMillis());

                Notification notification = builder.build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
                //Остаток дней
                NUMOFDAYS = Long.toString(DateSetter(year,monthOfYear,dayOfMonth));
                setResult(RESULT_OK, resultValue);
                finish();
            }
        };


    }

    public static long DateSetter(int y, int m, int d){
        Calendar thatDay = Calendar.getInstance();
        thatDay.set(Calendar.DAY_OF_MONTH,d);
        thatDay.set(Calendar.MONTH,m);
        thatDay.set(Calendar.YEAR, y);
        Calendar today = Calendar.getInstance();
        long mills = today.getTimeInMillis() - thatDay.getTimeInMillis();
        if (mills > 0)
            return 0;
        return Math.abs(mills / (24 * 60 * 60 * 1000));
    }
}