package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.database.Appointment;

public class SmsReceiver extends BroadcastReceiver {
    private String TAG = "SmsReceiver";
    private static final int MY_NOTIFICATION_ID = 1;
    private NotificationManager notificationMgr;
    private android.content.Context context;
    private String message = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationMgr = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();
        Bundle smsMessage = null;
        SmsMessage[] msgs = null;
        String sender = "";
        String title = "", memo = "", startDate = "", startTime = "", endDate = "",
                endTime = "", location = "", reminder = "";
        int id = -1;
        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i = 0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                sender = msgs[i].getOriginatingAddress().trim();
                if(sender.contains("+1"))
                    sender = sender.replace("+1", "");
                //else if(sender.contains("1"))
                //    sender = sender.replace("1", "");

                // Fetch the text message
                message = msgs[i].getMessageBody().toString();
                String temp = message;

                // to find information for database in message.

                // added by JS Lim
                if(temp.indexOf("ID:") < 0)
                {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    return;
                }
                //get id from message
                int idPos = temp.indexOf("ID: ") + 4;
                int idEndPos = temp.indexOf("\n");
                id = Integer.parseInt(temp.substring(idPos, idEndPos));
                temp = temp.substring(idEndPos + 1);
                //get title
                int titlePos = temp.indexOf("Title: ") + 6;
                int titleEndPos = temp.indexOf("\n");
                title = temp.substring(titlePos, titleEndPos).trim();
                temp = temp.substring(titleEndPos + 1);

                //get memo information
                int memoPos, memoEndPos;
                if (temp.contains("Memo: ")) {
                    memoPos = temp.indexOf("Memo: ") + 5;
                    memoEndPos = temp.indexOf("\n");
                    memo = temp.substring(memoPos, memoEndPos).trim();
                    temp = temp.substring(memoEndPos + 1);
                }

                //get Start date and time
                int sDatePos, sDateEndPos;
                if (temp.contains("sDate: ")) {
                    sDatePos = temp.indexOf("sDate: ") + 6;
                    sDateEndPos = temp.indexOf("\n");
                    startDate = temp.substring(sDatePos, sDatePos + 17).trim();
                    startTime = temp.substring(sDatePos + 18, sDatePos + 23).trim();
                    temp = temp.substring(sDateEndPos + 1);
                }

                //get End date and time
                int eDatePos, eDateEndPos = temp.indexOf("\n");;
                if (temp.contains("eDate: ") && eDateEndPos > 8) {
                    eDatePos = temp.indexOf("eDate: ") + 6;

                    endDate = temp.substring(eDatePos, eDateEndPos).trim();
                    endTime = temp.substring(eDatePos + 18,eDatePos + 23).trim();
                    Toast.makeText(context, endDate, Toast.LENGTH_SHORT).show();
                    temp = temp.substring(eDateEndPos + 1);
                }

                //get location
                int locationPos, locationEndPos;
                if (temp.contains("Location: ") && temp.length() > 10) {
                    locationPos = temp.indexOf("Location: ") + 10;
                    //locationEndPos = temp.indexOf("\n");
                    location = temp.substring(locationPos);
                    //temp = temp.substring(locationEndPos + 1);
                }

                //get reminder
/*                int reminderPos, reminderEndPos;
                if (temp.contains("Reminder: ") && temp.length() > 10) {
                    reminderPos = temp.indexOf("Reminder: ") + 10;
                    reminderEndPos = temp.indexOf("\n");
                    reminder = temp.substring(reminderPos,reminderEndPos);
                    temp = temp.substring(reminderEndPos + 1);
                }
*/
                assert smsMessage != null;
            }
            Log.d(TAG, sender);

            // query from database, if HostId exist, update; else create a new record.
            Appointment detailAppointment = MainActivity.dbHelper.getAppointment(sender, id);
//            Toast.makeText(context, detailAppointment.getTitle(), Toast.LENGTH_SHORT).show();
            String hostId = sender + id;

            if (detailAppointment == null) {
                long i = MainActivity.dbHelper.insertAppointmentReceive(sender,id, title, memo, startDate, startTime, endDate, endTime, location, reminder, "0");
                // Display the entire SMS Message
                displayNotificationMessage(context, sender, id);
                /*Intent detailIntent = new Intent(SmsReceiver.this, DetailActivity.class);
                context.startActivity(detailIntent);
                finish();*/
                Toast.makeText(context, "Appointment is added successfully", Toast.LENGTH_SHORT).show();
            } else {
                //String hostId = sender + id;
                boolean isUpdate = MainActivity.dbHelper.updateAppointment(sender, id, title, memo, startDate, startTime, endDate, endTime, location, reminder);
                if (isUpdate)
                    Toast.makeText(context, "Appointment is updated successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Error: Appointment has not been updated.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //jump into app, and update related appointment information
    public void displayNotificationMessage(Context context, String sender, int id) {

        Intent intent = new Intent(context, MainActivity.class);
        //intent.putExtra(DetailActivity.HOST_ID,sender);
        //intent.putExtra("DETAIL_OF_APPOINTMENT", "" + id);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(
                context)
                .setSmallIcon(R.drawable.winking)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setContentTitle(sender)
                .setContentText(message);
        notificationMgr.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
    }
}