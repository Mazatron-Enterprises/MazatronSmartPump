package com.mazatron.mazatronsmartpump;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;

public class TimerBrodcastReciever extends BroadcastReceiver {

    private PendingIntent waitPi;
    final String  MY_PREFS_NAME = "MyMazatronNumber";
    public String finalNumber;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefGet =  context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        finalNumber  = prefGet.getString("GSMnumber",null);

        //Get the SmsManager instance and call the sendTextMessage method to send message
        Intent pumpOffIntent  = new Intent(context,TimerBrodcastReciever.class);
        SmsManager smsOff = SmsManager.getDefault();
        waitPi = PendingIntent.getActivity(context, 0,pumpOffIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        smsOff.sendTextMessage(finalNumber, null, "PUMPOFF", waitPi,null);

        Intent notificationServiceIntent = new Intent(context, NotificationService.class);
        notificationServiceIntent.putExtra("phoneNumber", finalNumber);
        notificationServiceIntent.putExtra("PUMPSTATE", "OFF");
        NotificationService.enqueueWork(context, notificationServiceIntent);

    }
}

