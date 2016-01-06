package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class AddAttendeeActivity extends Activity {
    private String attendeeList = "";
    private EditText editName;
    private EditText editPhone;
    private EditText editEmail;
    private int index = 0;
    private ArrayList<String> attendeesNameList = new ArrayList<String>();
    private ArrayList<String> attendeesPhoneList = new ArrayList<String>();
    private ArrayList<String> attendeesEmailList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendee);

        editName = (EditText)findViewById(R.id.name);
        editPhone = (EditText)findViewById(R.id.phoneNumber);
        editEmail = (EditText)findViewById(R.id.emailAddress);
        ((TextView)findViewById(R.id.attendeeList)).setMovementMethod(new ScrollingMovementMethod());

        Button addButton = (Button)findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save input data
                attendeesNameList.add(editName.getText().toString().trim());
                String phoneNumber = editPhone.getText().toString().trim();
                if(phoneNumber.charAt(0) !=  '1')
                    phoneNumber = '1' + phoneNumber;
                attendeesPhoneList.add(phoneNumber);
                attendeesEmailList.add( editEmail.getText().toString().trim());

                attendeeList = attendeeList + editName.getText().toString().trim() + "; ";
                ((TextView) findViewById(R.id.attendeeList)).setText("Attendees : " + attendeeList);

                editName.setText("");
                editPhone.setText("");
                editEmail.setText("");
            }
        });

        Button closeButton = (Button)findViewById(R.id.buttonClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putStringArrayListExtra(AddAttendeeFromContacts.NameList, attendeesNameList);
                data.putStringArrayListExtra(AddAttendeeFromContacts.PhoneList, attendeesPhoneList);
                data.putStringArrayListExtra(AddAttendeeFromContacts.EmailList, attendeesEmailList);
                //intent.putExtra(AddAppointmentActivity.RESULT, attendeeList);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

}
