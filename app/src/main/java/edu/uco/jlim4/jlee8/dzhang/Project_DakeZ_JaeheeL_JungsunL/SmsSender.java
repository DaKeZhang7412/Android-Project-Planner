package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jung-sun on 11/15/2015.
 */
public class SmsSender {
    private ArrayList<String> phoneNumberList;
    private String messageContent;
    private boolean flagDone = false;

    public SmsSender(ArrayList<String> phoneNumberList) {
        this.phoneNumberList = phoneNumberList;
    }

    public void SmsContentBuilder_old(String title, String content, String time, String Location)
    {
        messageContent = "Title: " + title + "\n" + "Memo: "+ content +"\n" + "sDate: " + time + "\n";
        messageContent = messageContent + "Location: " + Location;
    }

    public void SmsContentBuilder(int id, String title, String content, String stime, String etime, String Location)
    {
        String tmp="";

        if(Location != null)
        {
            int pos = Location.indexOf("\n");
            tmp = Location.substring(0,pos+1);
        }

        messageContent = "ID: " + String.format("%d",id) + "\nTitle: " + title + "\n" + "Memo: "+ content +"\n" + "sDate: " + stime + "\n" + "eDate: " + etime + "\n";;
        messageContent = messageContent + "Location: " + tmp;
    }

    public boolean SmsSend(Context getContext)
    {
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getContext, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getContext, 0, new Intent("SMS_DELIVERED"), 0);

// For when the SMS has been sent
        getContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
                flagDone = true;
            }
        }, new IntentFilter("SMS_SENT"));

// For when the SMS has been delivered
        getContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
                flagDone = true;
            }
        }, new IntentFilter("SMS_DELIVERED"));

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String phoneNumber:phoneNumberList) {
                smsManager.sendTextMessage(phoneNumber, null, messageContent, sentPendingIntent, deliveredPendingIntent);
            }
        }
        catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

