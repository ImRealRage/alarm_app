package com.example.alarm_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText[] editTexts;
    private ToggleButton[] toggleButtons;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        editTexts = new EditText[]{
                findViewById(R.id.editText0),
                findViewById(R.id.editText1),
                findViewById(R.id.editText2),
                findViewById(R.id.editText3)
        };

        toggleButtons = new ToggleButton[]{
                findViewById(R.id.toggleButton0),
                findViewById(R.id.toggleButton1),
                findViewById(R.id.toggleButton2),
                findViewById(R.id.toggleButton3)
        };

        for (int i = 0; i < editTexts.length; i++) {
            final int index = i;
            editTexts[i].setOnClickListener(v -> showTimePickerDialog(index));

            editTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        editTexts[index].setTextSize(45);
                        // Enable toggle button when time is set
                        toggleButtons[index].setEnabled(false);
                        // Enable alarm when time is set
                        toggleButtons[index].setChecked(true);
                    } else {
                        editTexts[index].setTextSize(18);
                        // Disable toggle button when time is not set
                        toggleButtons[index].setEnabled(false);
                        // Disable alarm when time is not set
                        toggleButtons[index].setChecked(false);
                    }
                }
            });

            toggleButtons[i].setOnClickListener(v -> {
                ToggleButton toggleButton = (ToggleButton) v;
                if (toggleButton.isChecked()) {
                    // Enable alarm
                    setAlarm(index);
                } else {
                    // Disable alarm
                    cancelAlarm(index);
                }
            });
        }
    }

    private void showTimePickerDialog(final int index) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                (view, hourOfDay1, minute1) -> {
                    String time = String.format("%02d:%02d", hourOfDay1, minute1);
                    editTexts[index].setText(time);
                    // Enable toggle button when time is set
                    toggleButtons[index].setEnabled(true);
                    // Enable alarm when time is set
                    toggleButtons[index].setChecked(true);
                }, hourOfDay, minute, true);

        timePickerDialog.show();
    }

    private void setAlarm(int index) {
        String timeText = editTexts[index].getText().toString();
        if (!timeText.isEmpty()) {
            String[] timeArray = timeText.split(":");
            int hourOfDay = Integer.parseInt(timeArray[0]);
            int minute = Integer.parseInt(timeArray[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            int alarmId = index; // Use index as the alarm ID
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Toast.makeText(MainActivity.this, "Alarm set for " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
                } else {
                    Intent permissionIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(permissionIntent);
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(MainActivity.this, "Alarm set for " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cancelAlarm(int index) {
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        int alarmId = index; // Use index as the alarm ID
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(MainActivity.this, "Alarm canceled", Toast.LENGTH_SHORT).show();
    }
}