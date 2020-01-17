package com.mazatron.mazatronsmartpump;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import androidx.core.app.JobIntentService;

import com.mazatron.mazatronsmartpump.R;

import java.util.Calendar;
import java.util.Date;

import static java.lang.Thread.sleep;


public class NotificationService extends JobIntentService {


    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_DRAFT = "content://sms/draft";
        final String SMS_URI_SENT = "content://sms/sent";
        String phoneNumber;
        String pumpState = null;
        String lastMessage = null;
        boolean firstmessage;
        StringBuilder smsBuilder = new StringBuilder();


        final long NANOSEC_PER_SEC = 1000l*1000*1000;
        long startTime = System.nanoTime();

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        do {
            try {
                Uri uri = Uri.parse(SMS_URI_INBOX); //cSMS_URI_SENT
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};

                lastMessage = null;
                phoneNumber = intent.getStringExtra("phoneNumber");
                pumpState   = intent.getStringExtra("PUMPSTATE");

                String myNumber = "address=" + "'+91" + phoneNumber + "'"; //concatenate phone number of pump sim card

                Cursor cur = getContentResolver().query(uri, projection, myNumber, null, "date desc"); //asc
                if (cur != null && cur.moveToFirst()) {
                    long index_MessageId     = cur.getColumnIndex("_id");
                    int index_Address = cur.getColumnIndex("address");
                    int index_Person = cur.getColumnIndex("person");
                    int index_Body = cur.getColumnIndex("body");
                    int index_Date = cur.getColumnIndex("date");
                    int index_Type = cur.getColumnIndex("type");
                    do {
                        String strAddress = cur.getString(index_Address);
                        int intPerson = cur.getInt(index_Person);
                        String strbody = cur.getString(index_Body);
                        long longDate = cur.getLong(index_Date);
                        int int_Type = cur.getInt(index_Type);

                        firstmessage = true;

                        //Check with today's Date
                        String smsDate = millisToDate(longDate);
                        String todaysDate = Calendar.getInstance().getTime().toString();

                        String[] smsDateArrSplit = smsDate.split("\\s");
                        String[] todaysDateArrSplit = todaysDate.split("\\s");

                            if ( smsDateArrSplit[1].equals(todaysDateArrSplit[1]) && smsDateArrSplit[2].equals(todaysDateArrSplit[2]) ) {
                                smsBuilder.append("[ ");
                                smsBuilder.append(strAddress + ", ");
                                smsBuilder.append(intPerson + ", ");
                                smsBuilder.append(strbody + ", ");
                                smsBuilder.append(longDate + ", ");
                                smsBuilder.append(int_Type);
                                smsBuilder.append(" ]\n\n");

                            } else {
                                lastMessage = null;
                            }

                    } while (!firstmessage);
                    if (!cur.isClosed()) {
                        cur.close();
                        cur = null;
                    }
                } else {
                  //  smsBuilder.append("no result!");
                }
            } catch (
                    SQLiteException ex) {
                Log.d("SQLiteException", ex.getMessage());
            }

            //Check result
            boolean lv_true = true;

            if (smsBuilder.length() > 1) {
                String[] lines = smsBuilder.toString().split("\\n");
                for (String s : lines) {

                    if ((!s.equals("null")) && (lv_true)) {
                        lastMessage = s;
                        break;
                    }
                    lv_true = false;
                    System.out.println("Content = " + s);
                    System.out.println("Length = " + s.length());
                }
            }
            if (lastMessage != null) {
                if (lastMessage.contains("PUMP ON OK") && pumpState.contains("ON")) {

                    //Pump ON
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification.Builder pumpOnBuilder = new Notification.Builder(this);
                    pumpOnBuilder.setSmallIcon(R.mipmap.ic_logo)
                            .setContentTitle("Mazatron Smart Pump")
                            .setContentText("आपका Pump ON होगया")
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_HIGH);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String channelId = "Mazatron Smart Pump ON";
                        NotificationChannel channel = new NotificationChannel(channelId,
                                "Mazatron Smart Pump ON",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                        pumpOnBuilder.setChannelId(channelId);
                    }
                    notificationManager.notify(0, pumpOnBuilder.build());
                    break;

                } else if (lastMessage.contains("PUMP OFF OK") && pumpState.contains("OFF")) {

                    //Pump OFF
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification.Builder pumpOffBuilder = new Notification.Builder(this);
                    pumpOffBuilder.setSmallIcon(R.mipmap.ic_logo)
                            .setContentTitle("Mazatron Smart Pump")
                            .setContentText("आपका Pump OFF होगया")
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_HIGH);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String channelId = "Mazatron Smart Pump OFF";
                        NotificationChannel channel = new NotificationChannel(channelId,
                                "Mazatron Smart Pump OFF",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                        pumpOffBuilder.setChannelId(channelId);
                    }
                    notificationManager.notify(0, pumpOffBuilder.build());
                    break;
                }

            }
        }while (lastMessage == null || lastMessage.matches("\\s") || ((System.nanoTime()- startTime)< 2*60*NANOSEC_PER_SEC));
     }

    public static String millisToDate(long currentTime) {
        String finalDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        Date date = calendar.getTime();
        finalDate = date.toString();
        return finalDate;
    }
}
