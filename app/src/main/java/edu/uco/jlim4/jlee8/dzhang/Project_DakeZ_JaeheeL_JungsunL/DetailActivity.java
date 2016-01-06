package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.database.Appointment;

/**
 * This detail activity displays Database records from the first one to the end.
 *
 * Created by Neo on 2015/10/30.
 */

/**
 * Modified by JS Lim on 11/16/2015
 */

public class DetailActivity extends Activity {

    public static final String APP_ID = "edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.AppID";
    public static final String HOST_ID = "edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.HostID";

    private TextView startDate, startTime,endDate, endTime;
    private EditText title, memo, location, attendee;
    private Button editBtn, deleteBtn, preBtn, nextBtn;
    private String hostID;
    int id = 0;

    //Cursor cr = MainActivity.db.getInformation(i);
    //Cursor cRowNumber = MainActivity.db.getRows();

    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        // added by JS Lim on 09/11/2015
        //int appID = getIntent().getIntExtra(APP_ID, 1) ;
        //Appointment detailAppointment = MainActivity.dbHelper.getAppointment(appID);

        title = (EditText) findViewById(R.id.title);
        memo = (EditText) findViewById(R.id.memo);
        startDate = (TextView) findViewById(R.id.setupStartDate);
        startTime = (TextView) findViewById(R.id.setupStartTime);
        endDate = (TextView) findViewById(R.id.endDate);
        endTime = (TextView) findViewById(R.id.endTime);
        location = (EditText) findViewById(R.id.location);
        attendee = (EditText) findViewById(R.id.attendees);

        editBtn = (Button) findViewById(R.id.buttonEdit);
        deleteBtn = (Button) findViewById(R.id.buttonDelete);

        // removed by JS LIM on 11/10
        //preBtn = (Button) findViewById(R.id.preBtn);
        //nextBtn = (Button) findViewById(R.id.nextBtn);

        //get information from database, * Id is primary key for finding record.
        //showData(1);

        //===>added by JH Lee 11/10/2015
        Intent intent= getIntent();
        Bundle b = intent.getExtras();
        id = Integer.parseInt(intent.getStringExtra("DETAIL_OF_APPOINTMENT"));
        // id = Integer.parseInt(b.getString("DETAIL_OF_APPOINTMENT"));
        hostID = getIntent().getStringExtra(this.HOST_ID);
        showData(hostID,id);
        //===> JH L

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isUpdate = MainActivity.dbHelper.updateAppointment(hostID, id, title.getText().toString().trim(),
                        memo.getText().toString().trim(),
                        startDate.getText().toString().trim(),
                        startTime.getText().toString().trim(),
                        endDate.getText().toString().trim(),
                        endTime.getText().toString().trim(),
                        location.getText().toString().trim(),
                        "");
                if (isUpdate) showMessage("Message", "Appointment has been updated.");
                else
                    showMessage("Message", "Appointment has not been updated.");

                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
// why dont get data from database?
    public void showData (String hostID,int id) {

        Appointment detailAppointment = MainActivity.dbHelper.getAppointment(hostID,id);

        // added by JS Lim on 11/16/2015
        ArrayList<Attendee> attendeeArrayList = MainActivity.dbHelper.getAttendeeList(id);

        title.setText(detailAppointment.getTitle(), TextView.BufferType.EDITABLE);
        memo.setText(detailAppointment.getMemo(), TextView.BufferType.EDITABLE);
        startDate.setText(detailAppointment.getStartDate() , TextView.BufferType.EDITABLE);
        startTime.setText(detailAppointment.getStartTime() , TextView.BufferType.EDITABLE);
        endDate.setText(detailAppointment.getEndDate() , TextView.BufferType.EDITABLE);
        endTime.setText(detailAppointment.getEndTime() , TextView.BufferType.EDITABLE);
        location.setText(detailAppointment.getLocation(), TextView.BufferType.EDITABLE);

        // added by JS Lim on 11/16/2015
        String attendeeDisplay = "Attendees : ";
        if(attendeeArrayList == null)
        {
            attendeeDisplay = "Attendees : DB Error";
        }
        else {
            for (int i = 0; i < attendeeArrayList.size(); ++i) {
                attendeeDisplay = attendeeDisplay + attendeeArrayList.get(i).toString() + " ; ";
            }
        }
        attendee.setText(attendeeDisplay);

        /*
        if (cr.moveToFirst()) {
            title.setText(cr.getString(1), TextView.BufferType.EDITABLE);
            memo.setText(cr.getString(2), TextView.BufferType.EDITABLE);
            startDate.setText(cr.getString(3), TextView.BufferType.EDITABLE);
            startTime.setText(cr.getString(4), TextView.BufferType.EDITABLE);
            endDate.setText(cr.getString(5), TextView.BufferType.EDITABLE);
            endTime.setText(cr.getString(6), TextView.BufferType.EDITABLE);
            location.setText(cr.getString(7), TextView.BufferType.EDITABLE);
            attendee.setText(cr.getString(8), TextView.BufferType.EDITABLE);
        }
        else if (cRowNumber.getCount() == 0) {
            showMessage("Error:", "No Data found.");
            return;
        }
*/
        /*while (cr.moveToNext()){
            //this part for multiple record
              String data = cr.getString(0) + "\n" + cr.getString(1) + "\n" + cr.getString(2);
            showMessage("Data", data);
        }*/
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void onDateTimePickerClicked(View view) {
        DialogFragment fragment;
        Bundle args;
        switch (view.getId()) {
            case R.id.setupStartDate:
                fragment = new DatePickerFragment();
                args = new Bundle();
                args.putInt("R.id", R.id.setupStartDate);
                fragment.setArguments(args);
                fragment.show(getFragmentManager(), "datepicker");
                break;
            case R.id.setupStartTime:
                fragment = new TimePickerFragment();
                args = new Bundle();
                args.putInt("R.id", R.id.setupStartTime);
                fragment.setArguments(args);
                fragment.show(getFragmentManager(), "timepicker");
                break;
            case R.id.endDate:
                fragment = new DatePickerFragment();
                args = new Bundle();
                args.putInt("R.id", R.id.endDate);
                fragment.setArguments(args);
                fragment.show(getFragmentManager(), "datepicker");
                break;
            case R.id.endTime:
                fragment = new TimePickerFragment();
                args = new Bundle();
                args.putInt("R.id", R.id.endTime);
                fragment.setArguments(args);
                fragment.show(getFragmentManager(), "timepicker");
                break;
        }
    }
}
