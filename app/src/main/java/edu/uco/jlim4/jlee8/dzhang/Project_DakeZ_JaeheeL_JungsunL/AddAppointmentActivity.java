
package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

/****
 * Updated on 10/25 by JS Lim
 * missing title check in [Save] button.
 * [All Day] check box, Date & Time picker related part update
 * add getCurrentStartDateTime() method in order to set current date and time when this activity is created or clicked [Clear] button
 * [Clear] button update
 * At the end of [Save] button onClick(), start MainActivity to go back to main
 *
 */

/**** Updated on 11/03/2015 by JS Lim
 *  bug fix in the saveButton.OnClick() - remove getCurrentStartDateTime().
 *
 */

/**** Updated on 11/09/2015 by JS Lim
 *  update Add Attendee related part.
 *  Save DB
 */

/**** Updated on 11/16/2015 by JS Lim
 *  add send SMS function into  Save Button
 *
 */

/**** remained things to do
 *  Location button
  *  Setting Reminder
 *
 *
 */

/****  added on 11/24/2015 by JH Lee
 *  sending notification email
 *  private void send_NotificationEmail(long id)
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Calendar;

import static edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.R.id.buttonAddAttendee;
import static edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.R.id.buttonClear;
import static edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.R.id.buttonDeleteAttendee;
import static edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.R.id.buttonSave;

public class AddAppointmentActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private String[] stringDayOfWeek = new String[]{
            "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"
    };
    private EditText titleEditText;
    private EditText memoEditText;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private TextView startTimeTextView;
    private TextView endTimeTextView;

    private String location;
    private Button locationButton;


    private EditText attendeeEditText;
    private Button saveButton;
    private Button clearButton;
    private Button detailButton;
    private CheckBox checkAllDay;

    private static int timeEditTextWidth;

    //add more for the future.
    private String title, memo,
            dbStartDate, dbStartTime,
            dbEndTime, dbEndDate,
            dbLocation,dbReminder;
    private int appointmentId;


    // added by JS Lim on 11/03/2015
    private final int REQUEST_CODE = 1;
    private final int REQUEST_CODE_DELETE= 2;
    private final int RESULT_EMAIL_OK = 4;
    public static final String NameList = "edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.NameList";
    public static final String PhoneList = "edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.PhoneList";
    public static final String EmailList = "edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.EmailList";

    private ArrayList<Attendee> attendeeList = new ArrayList<Attendee>();
    private ListView listView;
    private String myPhoneNumber;
    private ArrayAdapter<Attendee> adapter;

    private GoogleApiClient mGoogleApiClient;
    private final int PLACE_PICKER_REQUEST = 5;
    private String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tmpPhoneNumber = tMgr.getLine1Number().trim();

        if(tmpPhoneNumber.contains("+1")) {
            myPhoneNumber = (tmpPhoneNumber.substring(2));
        }
        else if(tmpPhoneNumber.charAt(0) == '1') {
            myPhoneNumber = (tmpPhoneNumber.substring(1));
        }
        else
            myPhoneNumber = (tmpPhoneNumber);

        titleEditText = (EditText) findViewById(R.id.setupTitle);
        memoEditText = (EditText) findViewById(R.id.setupMemo);

        // Suggestion: we could use SimpleDateFormat to format the date and time,
        // and store them into String, Date type. (will much more easier to manipulate later.)
        // Here is some example:
        // http://stackoverflow.com/questions/7363112/best-way-to-work-with-dates-in-android-sqlite

        // updated by JS Lim on 10/25

        startDateTextView = (TextView) findViewById(R.id.setupStartDate);
        startTimeTextView = (TextView) findViewById(R.id.setupStartTime);
        endDateTextView = (TextView) findViewById(R.id.endDate);
        endTimeTextView = (TextView) findViewById(R.id.endTime);

        //<=== added by JH Lee on 11/28/2015  :  Google Place
        locationButton = (Button) findViewById(R.id.location);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLocationButtonClick(view);
            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener(this)
                .build();
        //====>

        //attendeeEditText = (EditText) findViewById(R.id.attendees);

        listView = (ListView)findViewById(R.id.attendeeListView);

        adapter = new ArrayAdapter<Attendee>(this,R.layout.simple_list_item_multiple_choice,attendeeList);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        //Problem with SmsReceiver. set condition for 
        if(savedInstanceState != null) {
//            String nameList = savedInstanceState.getString("name");
//            attendeeEditText.setText(nameList);
        }

        getCurrentStartDateTime();

        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) startTimeTextView.getLayoutParams();
        timeEditTextWidth = layoutParams1.width;

        (checkAllDay = (CheckBox) findViewById(R.id.checkAllDay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAllDay.isChecked()) {
                    RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) startTimeTextView.getLayoutParams();
                    layoutParams1.width = 0;
                    startTimeTextView.setLayoutParams(layoutParams1);

                    RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) endTimeTextView.getLayoutParams();
                    layoutParams2.width = 0;
                    endTimeTextView.setLayoutParams(layoutParams2);
                } else {
                    RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) startTimeTextView.getLayoutParams();
                    layoutParams1.width = timeEditTextWidth;
                    startTimeTextView.setLayoutParams(layoutParams1);

                    RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) endTimeTextView.getLayoutParams();
                    layoutParams2.width = timeEditTextWidth;
                    endTimeTextView.setLayoutParams(layoutParams2);
                }

            }
        });

        Button addButton = (Button) findViewById(buttonAddAttendee);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(AddAppointmentActivity.this, AddAttendeeActivity.class);
                //startActivityForResult(intent, REQUEST_CODE);

                Intent intent = new Intent(AddAppointmentActivity.this, AddAttendeeFromContacts.class);
                startActivityForResult(intent, REQUEST_CODE);
            }

        });

        final Button deleteButton = (Button) findViewById(buttonDeleteAttendee);
        final Drawable backup = deleteButton.getBackground();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listView.getChoiceMode() != listView.CHOICE_MODE_MULTIPLE) {
                    listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
                    Bitmap original = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_trash_green_background);
                    Bitmap b = Bitmap.createScaledBitmap(original, deleteButton.getWidth() + 3, deleteButton.getHeight() + 3, false);
                    Drawable d = new BitmapDrawable(getApplicationContext().getResources(), b);
                    deleteButton.setBackground(d);
                } else {
                    ArrayList<Attendee> tempAttendeeList = new ArrayList<Attendee>();

                    tempAttendeeList.clear();

                    for (int i = 0; i < attendeeList.size(); ++i) {
                        if (attendeeList.get(i).getChecked() == true)
                            tempAttendeeList.add(attendeeList.get(i));
                    }

                    attendeeList.removeAll(tempAttendeeList);

                    adapter = new ArrayAdapter<Attendee>(getApplicationContext(),R.layout.simple_list_item_multiple_choice,attendeeList);
                    listView.setAdapter(adapter);
                    listView.setChoiceMode(listView.CHOICE_MODE_NONE);
                    deleteButton.setBackground(backup);
                }
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView item = (CheckedTextView) view;
                item.setTextColor(Color.BLACK);
                attendeeList.get(i).setChecked(item.isChecked());
            }
        });

        //Saving data to database.
        saveButton = (Button) findViewById(buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = titleEditText.getText().toString().trim();
                memo = memoEditText.getText().toString().trim();
                if (title.trim().isEmpty()) {
                    Toast.makeText(AddAppointmentActivity.this, "Please input title", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (attendeeList.size() == 0) {
                    Toast.makeText(AddAppointmentActivity.this, "Please add attendee", Toast.LENGTH_SHORT).show();
                    return;
                }
                // /getCurrentStartDateTime();    // removed by JS Lim on 11/03/2015
                dbStartDate = startDateTextView.getText().toString().trim();
                dbEndDate = endDateTextView.getText().toString().trim();

                if (checkAllDay.isChecked()) {
                    dbStartTime = "00:00";
                    dbEndTime = "23:59";
                } else {
                    dbStartTime = startTimeTextView.getText().toString().trim();
                    dbEndTime = endTimeTextView.getText().toString().trim();
                }

                String location = locationButton.getText().toString().trim();
                dbLocation = location.substring(location.indexOf("]") + 1, location.length());

                //dbLocation = locationName;
                // Add more details in the future.
                dbReminder = "";

                //db.pushInformation(db,title,memo, date, time);
                // updated by JS Lim on 10/25
                // example) dbStartDate => "2015.9.25 (sun), dbStartTime => "21:40"

                appointmentId =  MainActivity.dbHelper.getNextID();

                if (sendMessage() == true) {

                    final long id = MainActivity.dbHelper.insertAppointment(myPhoneNumber, title, memo, dbStartDate, dbStartTime, dbEndDate, dbEndTime, dbLocation, dbReminder, "1");
                    if(id == -1)
                    {
                        Toast.makeText(AddAppointmentActivity.this, "Add Appointment is failed ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    MainActivity.dbHelper.insertAttendee(appointmentId, attendeeList);

                    Toast.makeText(AddAppointmentActivity.this, "Appointment is added successfully", Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(AddAppointmentActivity.this)
                            .setMessage("Do you want to send emails for this appointment?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                   send_NotificationEmail(id);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(AddAppointmentActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();

                    /*  Toast.makeText(AddAppointmentActivity.this, "Sent Notification email successfully!!", Toast.LENGTH_SHORT).show();
                        //finish add activity and return to the main activity
                        Intent intent = new Intent(AddAppointmentActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    */
                } else
                    Toast.makeText(AddAppointmentActivity.this, "Add Appointment is failed", Toast.LENGTH_SHORT).show();
            }
        });

        //clear the view.
        clearButton = (Button) findViewById(buttonClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set all TextView as "".
                titleEditText.setText("");
                memoEditText.setText("");
                getCurrentStartDateTime();
                endDateTextView.setText("");
                endTimeTextView.setText("");
                locationButton.setText("Click for picking a location!");

                attendeeList.clear();

                adapter = new ArrayAdapter<Attendee>(getApplicationContext(), R.layout.simple_list_item_multiple_choice, attendeeList);
                listView.setAdapter(adapter);
                listView.setChoiceMode(listView.CHOICE_MODE_NONE);
                listView.setItemsCanFocus(false);
            }
        });
    }
    //<=== added by JH Lee on 11/28/2015  :  Google Place
    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //By clicking location button this app is connected to Google Place Services
    public void onLocationButtonClick(View view) {

        int PLACE_PICKER_REQUEST = 5;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this),PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    //=====>


//<===== added on 11-24-2015
    private void send_NotificationEmail(long id){

        //create an email for notification that will be sent to the attendees
        ArrayList<Attendee> attendees = MainActivity.dbHelper.getAttendeeList(id);
        String[] attendeeEmails;
        ArrayList<String> emailArrayList = new ArrayList<>();

        //create String[] of the attendees' email addresses for putExtra intent.EMAIL_CC
        emailArrayList.clear();
        for(Attendee attendee:attendees){
            if(attendee.getEmail() != null){
                emailArrayList.add(attendee.getEmail());
            }
        }

        attendeeEmails = new String[emailArrayList.size()];
        int index = 0;
        for(String email: emailArrayList){
            attendeeEmails[index] = email;
            index++;
        }

        //create automatic email contents with string builder
        StringBuilder sb = new StringBuilder("Hello!");
        sb.append("\n");
        sb.append("You are invited to the following event. ").append("\n");
        sb.append("Please, R.S.V.P!").append("\n");
        sb.append("Title\n" + "        " + titleEditText.getText().toString()).append("\n");
        sb.append("Memo\n" + "        " + memoEditText.getText().toString()).append("\n");

        if (checkAllDay.isChecked()) {
            sb.append("Date & Time\n" +"        All day on " + startDateTextView.getText().toString()).append("\n");
        }
        else
        {
            sb.append("Date\n"+"        from " + startTimeTextView.getText().toString() + "," + startDateTextView.getText().toString()).append("\n");
            sb.append("        to     " + endTimeTextView.getText().toString() + "," + endDateTextView.getText().toString()).append("\n");
        }

        sb.append(locationButton.getText().toString()).append("\n");

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_CC, attendeeEmails);
        i.putExtra(Intent.EXTRA_SUBJECT, "Appointment Notification for " + titleEditText.getText().toString());
        i.putExtra(android.content.Intent.EXTRA_TEXT, sb.toString());
        startActivityForResult(Intent.createChooser(i, "Sending Email..."), RESULT_EMAIL_OK);

    }
    //=========>

    private boolean sendMessage()
    {
        boolean ret;

        Toast.makeText(getApplicationContext(), "SMS is going out.", Toast.LENGTH_SHORT).show();

        ArrayList<String> phoneList = new ArrayList<String>();
        for(int i=0; i< attendeeList.size(); ++i)
            phoneList.add(attendeeList.get(i).getPhoneNumber());
        //phoneList.add("14053433724");
        //phoneList.add("13312762583");

        SmsSender smsSender = new SmsSender(phoneList);
        smsSender.SmsContentBuilder(appointmentId, title, memo, dbStartDate + " " + dbStartTime, dbEndDate + " " + dbEndTime, dbLocation);

        ret = smsSender.SmsSend(getApplicationContext());
        if(ret) {
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Fail to send SMS.", Toast.LENGTH_LONG).show();
        }
        return ret;
    }

    private void getCurrentStartDateTime() {
        Calendar c;
        c = Calendar.getInstance();

        final int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;      // month is 0-based.
        int day = c.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        startDateTextView.setText("  " + year + "." + month + "." + day + " (" + stringDayOfWeek[dayOfWeek - 1] + ")");
        startTimeTextView.setText("  " + String.format("%02d", hour) + ":" + String.format("%02d", minute));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RESULT_EMAIL_OK)
        {
            //finish add activity and return to the main activity
            Intent intent = new Intent(AddAppointmentActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(requestCode == REQUEST_CODE){
            if (data == null) {
                // no result back or request_code does not match
                return;
            }

            if (resultCode == RESULT_OK) {

                ArrayList<String> attendeesNameList = new ArrayList<String>();
                ArrayList<String> attendeesPhoneList = new ArrayList<String>();
                ArrayList<String> attendeesEmailList = new ArrayList<String>();

                attendeesNameList = data.getStringArrayListExtra(NameList);
                attendeesPhoneList = data.getStringArrayListExtra(PhoneList);
                attendeesEmailList = data.getStringArrayListExtra(EmailList);

                for (int i = 0; i < attendeesNameList.size(); ++i) {
                    attendeeList.add(new Attendee(attendeesNameList.get(i).toString(), attendeesPhoneList.get(i).toString(), attendeesEmailList.get(i).toString()));
                }

                adapter = new ArrayAdapter<Attendee>(getApplicationContext(),R.layout.simple_list_item_multiple_choice,attendeeList);
                listView.setAdapter(adapter);
                listView.setChoiceMode(listView.CHOICE_MODE_NONE);

                // listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);

                //CheckedTextView chkView = (CheckedTextView)findViewById(R.id.checkedText);
                //chkView.setCheckMarkDrawable(null);

            }
        }
        //<=== updated by JH Lee on 11/28/2015
        // picked place result from Google place services
        //Extract the name and address of a selected place
        if(requestCode == PLACE_PICKER_REQUEST)
        {
            if( resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, this);
                String toastMag = String.format("Place: %s  is selected!", place.getName());
                Toast.makeText(this, toastMag, Toast.LENGTH_LONG).show();

                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();

                location = "[Place]"+ name + "\n" + address;
                locationName = name.toString();
                locationButton.setText(location);
            }
        }
        else
        {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i("TAG", String.format("Place :   %s has likelihood %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                likelyPlaces.release();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    //=====>
}
