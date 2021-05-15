package com.example.vaccineslottracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.example.vaccineslottracker.services.DatePickerFragment;
import com.example.vaccineslottracker.services.VaccineTrackerService;


import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private int notificationId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
    }

    /**
     * creates a notification channel
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            String CHANNEL_ID = getString(R.string.channel_id);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * starts tracking the vaccine slot per min
     * @param view
     */
    public void startTracking(View view){

        EditText pinCodesTextBox = (EditText) findViewById(R.id.pinCodes);
        EditText dateView = (EditText) findViewById(R.id.date);

        String pinCodesString = pinCodesTextBox.getText().toString();
        String date = dateView.getText().toString();

        if(pinCodesString != null && date != null) {
            String[] pinCodes = pinCodesString.split(",");

            startTrackingService(pinCodes, date);
        }
    }

    private void startTrackingService(String[] pinCodes, String date) {

        Toast serviceStarted = Toast.makeText(getApplicationContext(), "Tracking service is started", Toast.LENGTH_LONG);
        serviceStarted.show();

        createServiceStartedNotification();

        VaccineTrackerService vaccineTrackerService = intiVaccineTrackingService(pinCodes, date);

        Timer vaccineTrackerTimer = getVaccineTrackerTimer();

        vaccineTrackerTimer.schedule(vaccineTrackerService, 5000,60000);

    }

    private Timer getVaccineTrackerTimer() {
        Timer vaccineTrackerTimer = new Timer("Vaccine slot tracker");
        return vaccineTrackerTimer;
    }

    private VaccineTrackerService intiVaccineTrackingService(String[] pinCodes, String date) {
        VaccineTrackerService vaccineTrackerService = new VaccineTrackerService();

        vaccineTrackerService.setPinCodes(pinCodes);
        vaccineTrackerService.setApplicationContext(getApplicationContext());
        vaccineTrackerService.setChannelId(getString(R.string.channel_id));
        vaccineTrackerService.setDate(date);
        return vaccineTrackerService;
    }

    private void createServiceStartedNotification() {
        String CHANNEL_ID = getString(R.string.channel_id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.service_started_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId++, builder.build());
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();

        EditText dateView = (EditText) findViewById(R.id.date);
        ((DatePickerFragment)newFragment).setDateView(dateView);

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}