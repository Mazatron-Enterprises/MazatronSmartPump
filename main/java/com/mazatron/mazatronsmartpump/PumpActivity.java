package com.mazatron.mazatronsmartpump;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mazatron.mazatronsmartpump.R;

import java.util.Calendar;

public class PumpActivity extends AppCompatActivity implements DateTimeDialog.DateTimeDialogListener, PumpTimerDialog.PumpTimerDialogListener {

    public String phoneNumber;
    boolean permissionSMS;
    Integer scheduleHour;
    Integer scheduleMin;
    Integer AM_PM = null;
    ImageView mMazatronView;

    //Shared Prefrence
    final String  MY_PREFS_NAME_SCHEDULE = "MyMazatronSchedule";
    Button mCancelTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump);

        TimerBrodcastReciever pumpTimerReceiver = new TimerBrodcastReciever();
        IntentFilter notificationServiceIntentPump = new IntentFilter(NOTIFICATION_SERVICE);
        registerReceiver(pumpTimerReceiver, notificationServiceIntentPump);

        SchedulerBrodcastReciever pumpScheduleReceiver = new SchedulerBrodcastReciever();
        IntentFilter notificationServiceIntentSchedule = new IntentFilter(NOTIFICATION_SERVICE);
        registerReceiver(pumpScheduleReceiver, notificationServiceIntentSchedule);

        mCancelTimer = findViewById(R.id.resettimer);
        mCancelTimer.setVisibility(View.INVISIBLE);
        mCancelTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentTimerCancel = new Intent(getApplicationContext(), TimerBrodcastReciever.class);
                PendingIntent pendingIntentTimerCancel = PendingIntent.getBroadcast(getApplicationContext(), 234324243, intentTimerCancel, 0);
                AlarmManager alarmTimerCancel = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmTimerCancel.cancel(pendingIntentTimerCancel);
                mCancelTimer.setVisibility(View.INVISIBLE);
            }
        });

        mMazatronView = findViewById(R.id.mazlogopump);
        mMazatronView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentWeb = new Intent();
                intentWeb.setAction(Intent.ACTION_VIEW);
                intentWeb.addCategory(Intent.CATEGORY_BROWSABLE);
                intentWeb.setData(Uri.parse("https://www.mazatron.com/index.php?route=information/contact"));
                startActivity(intentWeb);
            }
        });

        phoneNumber = getIntent().getStringExtra("FinalNumber");
        if (ActivityCompat.checkSelfPermission(PumpActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(PumpActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PumpActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WAKE_LOCK,Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},123);

        }else {
            // Permission has already been granted.
            permissionSMS = true;
        }

    }

    public void pumpoff(View view) {
        if(permissionSMS){

            Intent pumpOffIntent  = new Intent(getApplicationContext(),PumpActivity.class);
            pumpOffIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pumpOffIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent waitPiOff = PendingIntent.getActivity(getApplicationContext(), 0, pumpOffIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            //Get the SmsManager instance and call the sendTextMessage method to send message
            SmsManager smsOff = SmsManager.getDefault();
            smsOff.sendTextMessage(phoneNumber, null, "PUMPOFF", waitPiOff,null);
            Toast.makeText(getApplicationContext(), "आपने पंप बंद किया !",Toast.LENGTH_SHORT).show();

            //Call Service
            Intent notificationServiceIntPumpOn = new Intent(this, NotificationService.class);
            notificationServiceIntPumpOn.putExtra("phoneNumber", phoneNumber);
            notificationServiceIntPumpOn.putExtra("PUMPSTATE","OFF");
            NotificationService.enqueueWork(this, notificationServiceIntPumpOn);

        }else{
            Toast.makeText(getApplicationContext(),"SMS परमिशन चेक करें अथवा App Restart करें !",Toast.LENGTH_SHORT).show();
        }
    }

    public void pumpon(View view) {
        if(permissionSMS){

            Intent pumpOnIntent  = new Intent(getApplicationContext(),PumpActivity.class);
            pumpOnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pumpOnIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent waitPi = PendingIntent.getActivity(getApplicationContext(), 0, pumpOnIntent,0);

             //Get the SmsManager instance and call the sendTextMessage method to send message
            SmsManager smsOn = SmsManager.getDefault();
            smsOn.sendTextMessage(phoneNumber, null, "PUMPON", waitPi,null);
            Toast.makeText(getApplicationContext(),"आपने पंप चालू किया !",Toast.LENGTH_SHORT).show();

            //Call Service
            Intent notificationServiceIntPumpOff = new Intent(this, NotificationService.class);
            notificationServiceIntPumpOff.putExtra("phoneNumber", phoneNumber);
            notificationServiceIntPumpOff.putExtra("PUMPSTATE", "ON");
            NotificationService.enqueueWork(this, notificationServiceIntPumpOff);
        }else{
            Toast.makeText(getApplicationContext(),"SMS परमिशन चेक करें अथवा App Restart करें !",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 5 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission has been granted.
            Toast.makeText(getApplicationContext(),"SMS/CALL परमिशन  मिलगई !" ,  Toast.LENGTH_SHORT).show();
            permissionSMS = true;
        } else {
            // Permission request was denied.
            Toast.makeText(getApplicationContext(), "SMS/CALL परमिशन नहीं मिली !",  Toast.LENGTH_SHORT).show();
            permissionSMS = false;
        }

    }

    public void lightalert(View view) {
        if(permissionSMS) {
            openLightDialog();
        }else {
            Toast.makeText(getApplicationContext(),"SMS परमिशन चेक करें अथवा App Restart करें !",Toast.LENGTH_SHORT).show();
        }
    }

    public void pumpschedule(View view) {
        if(permissionSMS) {
            openSchedulerDialog();
        }else {
            Toast.makeText(getApplicationContext(),"SMS परमिशन चेक करें अथवा App Restart करें !",Toast.LENGTH_SHORT).show();
        }
    }

    public void pumptimer(View view) {
        if(permissionSMS) {
            if (mCancelTimer.getVisibility() == View.INVISIBLE){
                openTimerDialog();
            }else if(mCancelTimer.getVisibility() == View.VISIBLE){
                Toast.makeText(getApplicationContext(),"RESET TIMER दबाएं !",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"SMS परमिशन चेक करें अथवा App Restart करें !",Toast.LENGTH_SHORT).show();
        }
    }

    public void openLightDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Pairing Alert")
                .setMessage("Do you want to Configure Light Alert? This requires Pairing.")

                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Configure", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                            Intent pumpLightAlert  = new Intent(getApplicationContext(),PumpActivity.class);
                            pumpLightAlert.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            pumpLightAlert.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent waitPi = PendingIntent.getActivity(getApplicationContext(), 0, pumpLightAlert,PendingIntent.FLAG_UPDATE_CURRENT);

                            //Get the SmsManager instance and call the sendTextMessage method to send message
                            SmsManager smsOn = SmsManager.getDefault();
                            smsOn.sendTextMessage(phoneNumber, null, "LIGHTCONFIG", waitPi,null);
                            Toast.makeText(getApplicationContext(),"\n" + "आपने Light Alert चालू किया !",Toast.LENGTH_SHORT).show();
                            }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void openTimerDialog(){

        PumpTimerDialog pumpTimerDialog = new  PumpTimerDialog();
        pumpTimerDialog.show(getSupportFragmentManager(),"Pump Timer");
    }

    public void openSchedulerDialog(){

        DateTimeDialog dateTimeDialog = new DateTimeDialog();
        dateTimeDialog.show(getSupportFragmentManager(),"Schedule Pump");

    }

    @Override
    public void applyTexts(Integer CurrentHour,Integer CurrentMinute,
                           Boolean sundaychk,
                           Boolean mondaychk,
                           Boolean tuedaychk,
                           Boolean weddaychk,
                           Boolean thudaychk,
                           Boolean fridaychk,
                           Boolean satdaychk,
                           Boolean bNeutral) {

        scheduleMin  = CurrentMinute;
        scheduleHour = CurrentHour;

        /*
        * Start Alarm Manager , Save and read from Prefreneces.
        */

        if (!bNeutral) {
            SharedPreferences prefSchedule = getApplicationContext().getSharedPreferences(MY_PREFS_NAME_SCHEDULE, MODE_PRIVATE);
            SharedPreferences.Editor prefScehudlEdit = prefSchedule.edit();

            if (sundaychk) {
                prefScehudlEdit.putBoolean("SundayAlarm", true);
            } else {
                prefScehudlEdit.putBoolean("SundayAlarm", false);
            }

            if (tuedaychk) {
                prefScehudlEdit.putBoolean("TuesdayAlarm", true);
            } else {
                prefScehudlEdit.putBoolean("TuesdayAlarm", false);
            }

            if (weddaychk) {
                prefScehudlEdit.putBoolean("WednesdayAlarm", true);
            } else {
                prefScehudlEdit.putBoolean("WednesdayAlarm", false);
            }

            if (mondaychk) {
                prefScehudlEdit.putBoolean("MondayAlarm", true);
            } else {
                prefScehudlEdit.putBoolean("MondayAlarm", false);
            }

            if (thudaychk) {
                prefScehudlEdit.putBoolean("ThursdayAlarm", true);
            } else {
                prefScehudlEdit.putBoolean("ThursdayAlarm", false);
            }

            if (fridaychk) {
                prefScehudlEdit.putBoolean("FridayAlarm", true);
            } else {
                prefScehudlEdit.putBoolean("FridayAlarm", false);
            }

            if (satdaychk) {
                prefScehudlEdit.putBoolean("SaturdayAlarm", true);
            } else {
                prefScehudlEdit.putBoolean("SaturdayAlarm", false);
            }




            if (mondaychk || tuedaychk || satdaychk || sundaychk || thudaychk || fridaychk || weddaychk) {
                prefScehudlEdit.putInt("ScheduleHour", scheduleHour);
                prefScehudlEdit.putInt("ScheduleMinute", scheduleMin);
                prefScehudlEdit.apply();
                Toast.makeText(getApplicationContext(),"Pump Schedule",Toast.LENGTH_SHORT).show();

                if (scheduleHour < 12) {
                    AM_PM = 1;
                } else {
                    AM_PM = 0;
                }


                if (tuedaychk) {
                    setAlarm(Calendar.TUESDAY, scheduleHour, scheduleMin, AM_PM); //set the alarm for this day of the week
                }
                if (sundaychk) {
                    setAlarm(Calendar.SUNDAY, scheduleHour, scheduleMin, AM_PM); //set the alarm for this day of the week
                }
                if (weddaychk) {
                    setAlarm(Calendar.WEDNESDAY, scheduleHour, scheduleMin, AM_PM); //set the alarm for this day of the week
                }
                if (thudaychk) {
                    setAlarm(Calendar.THURSDAY, scheduleHour, scheduleMin, AM_PM); //set the alarm for this day of the week
                }
                if (fridaychk) {
                    setAlarm(Calendar.FRIDAY, scheduleHour, scheduleMin, AM_PM); //set the alarm for this day of the week
                }
                if (satdaychk) {
                    setAlarm(Calendar.SATURDAY, scheduleHour, scheduleMin, AM_PM); //set the alarm for this day of the week
                }
                if(mondaychk){
                    setAlarm(Calendar.MONDAY, scheduleHour, scheduleMin, AM_PM);
                }

            } else {
                prefScehudlEdit.clear();
                prefScehudlEdit.apply();
            }
        }else {
            Intent intentSchedule = new Intent(this, SchedulerBrodcastReciever.class);
            PendingIntent pendingIntentSchedule = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intentSchedule, 0);
            AlarmManager alarmSchedule = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmSchedule.cancel(pendingIntentSchedule);
        }

    }

    @Override
    public void applytexts(int pumpTime) {

        Calendar timerCalendar = Calendar.getInstance();
        timerCalendar.add(Calendar.MINUTE,pumpTime);
        timerCalendar.set(Calendar.SECOND, 0);
        timerCalendar.set(Calendar.MILLISECOND, 0);

        Intent intentTimerSet = new Intent(this, TimerBrodcastReciever.class);
        PendingIntent pendingIntentTimerSet = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intentTimerSet, 0);
        AlarmManager alarmTimer = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmTimer.setExact(AlarmManager.RTC_WAKEUP,timerCalendar.getTimeInMillis(),pendingIntentTimerSet);

        mCancelTimer.setVisibility(View.VISIBLE);
        mCancelTimer.setText("Reset Timer: "+String.valueOf(pumpTime)+" Min");
    }

    public void setAlarm(int dayOfWeek, int AlarmHrsInInt, int AlarmMinsInInt, int amorpm) {
        // Add this day of the week line to your existing code
        Calendar alarmCalendar  = Calendar.getInstance();
        //Calendar calSet = (Calendar) alarmCalendar.clone();

        alarmCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        alarmCalendar.set(Calendar.HOUR, AlarmHrsInInt);
        alarmCalendar.set(Calendar.MINUTE, AlarmMinsInInt);
        alarmCalendar.set(Calendar.SECOND, 0);
        alarmCalendar.set(Calendar.AM_PM, amorpm);

        //Also change the time to 24 hours.
        // Check we aren't setting it in the past which would trigger it to fire instantly
        if(alarmCalendar.getTimeInMillis() < System.currentTimeMillis()) {
            alarmCalendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        Intent intentScheduleSet = new Intent(this, SchedulerBrodcastReciever.class);
        PendingIntent pendingIntentScheduleSet = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intentScheduleSet, 0);
        AlarmManager alarmSchedule = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmSchedule.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(),  AlarmManager.INTERVAL_DAY * 7,pendingIntentScheduleSet);
    }

}
