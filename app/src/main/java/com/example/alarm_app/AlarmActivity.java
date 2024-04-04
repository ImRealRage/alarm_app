package com.example.alarm_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmreciever);

        TextView currentTimeTextView = findViewById(R.id.current_time_textview);
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        currentTimeTextView.setText(currentTime);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Button snoozeButton = findViewById(R.id.snooze);
        Button dismissButton = findViewById(R.id.dismiss);

        snoozeButton.setOnClickListener(v -> snoozeAlarm());
        dismissButton.setOnClickListener(v -> dismissAlarm());

        // Play ringtone when the activity starts
        //playRingtone();    -commented on 4/4/24 (Reason double ringtone issue)
    }

    /*private void playRingtone() {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (ringtoneUri == null) {
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (ringtoneUri == null) {
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        ringtone.play();
    }*/

    private void stopRingtone() {
        if (ringtone != null) {
            ringtone.stop();
            ringtone.stop(); // Release the Ringtone object
            ringtone = null; // Set the reference to null to indicate that it's released
        }
    }

    private void snoozeAlarm() {
        // Stop the ringtone
        //stopRingtone();  --> Didnt work (refer dismissalalarm method)
        //stopRingtone();
        ringtone.stop();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        Intent intent = new Intent(this, AlarmReceiver.class);
        int uniqueId = (int) System.currentTimeMillis();
        pendingIntent = PendingIntent.getBroadcast(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Check for permission to schedule exact alarms on Android S and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                scheduleExactAlarm(calendar.getTimeInMillis());
            } else {
                // Permission not granted, navigate the user to the app's settings screen
                Intent permissionIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(permissionIntent);
            }
        } else {
            // For Android versions below S, schedule the alarm as usual
            scheduleExactAlarm(calendar.getTimeInMillis());
        }
    }


    private void scheduleExactAlarm(long triggerAtMillis) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 60000, pendingIntent);
        Toast.makeText(this, "Alarm snoozed for 5 minutes", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void dismissAlarm() {
        // Stop the ringtone
        ringtone.stop();
        //ringtone.setVolume(0);  ---> This is the last hope man!!!
        //stopRingtone();   ----->> Its works this way man lets gooooo!!!
        //stopRingtone();
        // Cancel the pending intent
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        Toast.makeText(this, "Alarm dismissed", Toast.LENGTH_SHORT).show();
        finish();
    }
}
