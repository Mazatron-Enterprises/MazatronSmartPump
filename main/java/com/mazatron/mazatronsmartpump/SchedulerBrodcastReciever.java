package com.mazatron.mazatronsmartpump;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;

public class SchedulerBrodcastReciever extends BroadcastReceiver {

    private PendingIntent waitPi;
    final String  MY_PREFS_NAME = "MyMazatronNumber";
    public String finalNumber;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefGet =  context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        finalNumber  = prefGet.getString("GSMnumber",null);

        //Get the SmsManager instance and call the sendTextMessage method to send message
        Intent pumpOnIntent  = new Intent(context,SchedulerBrodcastReciever.class);
        SmsManager smsOn = SmsManager.getDefault();
        waitPi = PendingIntent.getActivity(context, 0,pumpOnIntent,0);
        smsOn.sendTextMessage(finalNumber, null, "PUMPON", waitPi,null);

        Intent notificationServiceInt = new Intent(context, NotificationService.class);
        notificationServiceInt.putExtra("phoneNumber", finalNumber);
        notificationServiceInt.putExtra("PUMPSTATE", "ON");
        NotificationService.enqueueWork(context, notificationServiceInt);

    }
}
