package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//import android.provider.ContactsContract;
;
;

public class AddAttendeeFromContacts extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private final int REQUEST_CODE = 1;
    public static final String NameList = "edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.NameList";
    public static final String PhoneList = "edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.PhoneList";
    public static final String EmailList = "edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.EmailList";

    private int phase;

    private final ArrayList<Contacts> contactsList = new ArrayList<Contacts>();
    private final ArrayList<Contacts> selectedContactsList = new ArrayList<Contacts>();

    private final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Email.ADDRESS
    };

    private final static String[] FROM_COLUMNS_EMAIL = {
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Email.ADDRESS
    };

    /*
    * Defines an array that contains resource ids for the layout views
    * that get the Cursor column contents. The id is pre-defined in
    * the Android framework, so it is prefaced with "android.R.id"
    */
    private final static int[] TO_IDS = {
            //R.id.contact_name//,R.id.phone_number
            android.R.id.text1//, android.R.id.text2
    };


    private static final String[] PROJECTION1 =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
            };

    private static final String[] PROJECTION2 =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Email.ADDRESS
            };

    private static final String[] PROJECTION_ALL =
            {
                    ContactsContract.Data._ID,
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.Data.DATA1,
                    ContactsContract.Data.DATA2,
                    ContactsContract.Data.DATA3,
                    ContactsContract.Data.DATA4,
                    ContactsContract.Data.DATA5,
                    ContactsContract.Data.DATA6,
                    ContactsContract.Data.DATA7,
                    ContactsContract.Data.DATA8,
                    ContactsContract.Data.DATA9,
                    ContactsContract.Data.DATA10,
                    ContactsContract.Data.DATA11,
                    ContactsContract.Data.DATA12,
                    ContactsContract.Data.DATA13,
                    ContactsContract.Data.DATA14,
                    ContactsContract.Data.DATA15
            };

    private static final String SELECTION_ALL = ContactsContract.Data.LOOKUP_KEY + " = ?";

    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter cursorAdapter;

    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";

    private static final String SELECTION2 =
                    ContactsContract.Contacts._ID + " LIKE ?";

    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = {mSearchString};

    private ListView contactListView;

    private boolean doSearch;
    //private CheckedTextView checkedTextView;

    /*
    * Defines a string that specifies a sort order of MIME type
    */
    private static final String SORT_ORDER = ContactsContract.Data.MIMETYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendee_from_contacts);

        contactListView = (ListView) findViewById(R.id.contactListView);

        cursorAdapter = new SimpleCursorAdapter(
                this,
                //R.layout.contacts_list_item,
                android.R.layout.simple_list_item_multiple_choice,
                null,
                FROM_COLUMNS, TO_IDS,
                0);

        contactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Sets the adapter for the ListView
        //contactListView.setAdapter(adapter);
        contactListView.setAdapter(cursorAdapter);
        //contactListView.setItemsCanFocus(false);

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView item = (CheckedTextView) view;
                contactsList.get(i).setChecked(item.isChecked());
            }
        });

        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String attendees = ((TextView) findViewById(R.id.attendeeList)).getText().toString();
                attendees = "Attendees :";
                //if (!doSearch)
                //    selectedContactsList.clear();
                for (int i = 0; i < contactsList.size(); ++i)
                    if (contactsList.get(i).getChecked() == true) {

                        boolean match = false;

                        if (selectedContactsList.size() > 0) {
                            for (int j = 0; j < selectedContactsList.size(); ++j) {
                                if (selectedContactsList.get(j).equals(contactsList.get(i))) {
                                    match = true;
                                    break;
                                }
                            }
                        }

                        if (!match)
                            selectedContactsList.add(contactsList.get(i));
                    }

                for (int i = 0; i < selectedContactsList.size(); ++i)
                    attendees = attendees + selectedContactsList.get(i).getName() + " ; ";

                ((TextView) findViewById(R.id.attendeeList)).setText(attendees);
                doSearch = false;
                ((EditText)findViewById(R.id.searchName)).setText("");
            }
        });

        Button buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectionArgs[0] = ((EditText)findViewById(R.id.searchName)).getText().toString().trim();
                if(! mSelectionArgs[0].isEmpty()) mSelectionArgs[0] = "%" + mSelectionArgs[0] + "%";
                contactsList.clear();
                doSearch = true;
                phase = 1;
                getLoaderManager().restartLoader(0, null, AddAttendeeFromContacts.this);
            }
        });

        Button buttonDone = (Button)findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();

                ArrayList<String> attendeesNameList = new ArrayList<String>();
                ArrayList<String> attendeesPhoneList = new ArrayList<String>();
                ArrayList<String> attendeesEmailList = new ArrayList<String>();

                for(int i =0; i<selectedContactsList.size(); ++i) {

                    attendeesNameList.add(selectedContactsList.get(i).getName().trim());
                    String phoneNumber = selectedContactsList.get(i).getPhoneNumber().trim();
                    String[] temp = phoneNumber.split("\\(");

                    String temp2 = "";
                    for(int j=0; j< temp.length; ++j )
                        temp2 = temp2 + temp[j].trim();

                    temp = temp2.split("\\)");

                    String temp3 = "";
                    for(int j=0; j< temp.length; ++j )
                        temp3 = temp3 + temp[j].trim();

                    temp = temp3.split("-");

                    phoneNumber = "";
                    for(int j=0; j< temp.length; ++j )
                        phoneNumber = phoneNumber + temp[j].trim();


                    if (phoneNumber.charAt(0) != '1')
                        phoneNumber = '1' + phoneNumber;
                    attendeesPhoneList.add(phoneNumber);

                    attendeesEmailList.add(selectedContactsList.get(i).getEmail().trim());
                }

                data.putStringArrayListExtra(AddAppointmentActivity.NameList, attendeesNameList);
                data.putStringArrayListExtra(AddAppointmentActivity.PhoneList, attendeesPhoneList);
                data.putStringArrayListExtra(AddAppointmentActivity.EmailList, attendeesEmailList);
                setResult(RESULT_OK, data);
                finish();
            }
        });


        Button buttonAddDirectly = (Button) findViewById(R.id.buttonAddDirect);
        buttonAddDirectly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddAttendeeFromContacts.this, AddAttendeeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // Initializes the loader
        mSelectionArgs[0] = "";
        phase = 1;
        getLoaderManager().initLoader(1, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
         /*
         * Makes search string into pattern and
         * stores it in the selection array
         */
        //mSelectionArgs[0] = "";

        if (mSelectionArgs[0].isEmpty()) mSelectionArgs[0] = "%";

        //String selection = ContactsContract.Contacts.DISPLAY_NAME + "LIKE %Dake%";
        // Starts the query

        CursorLoader cursorLoader;

        if(phase == 1) {
            cursorLoader =  new CursorLoader(
                    this,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    //ContactsContract.Data.CONTENT_URI,
                    PROJECTION1,
                    SELECTION,
                    mSelectionArgs,
                    null
            );
        }
        else
        {
            cursorLoader =  new CursorLoader(
                    this,
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    //ContactsContract.Data.CONTENT_URI,
                    PROJECTION2,
                    SELECTION,
                    mSelectionArgs,
                    null
            );
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(phase == 1) {
            cursorAdapter.swapCursor(cursor);

            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); ++i) {
                cursor.moveToPosition(i);
                String t = cursor.getString(0).toString().trim();
                String t1 = cursor.getString(1).toString().trim();
                String t2 = cursor.getString(3).toString().trim();
                String t3 = "";

                contactsList.add(new Contacts(t, t1, t2, t3));
            }

            phase = 2;
            getLoaderManager().restartLoader(2, null, AddAttendeeFromContacts.this);

        }
        else
        {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); ++i) {
                cursor.moveToPosition(i);
                String t = cursor.getString(0).toString().trim();
                String t1 = cursor.getString(1).toString().trim();
                String t3 = cursor.getString(2).toString().trim();

                for(int j=0; j< contactsList.size(); ++j) {
                    if (contactsList.get(j).getName().compareTo(t1) == 0) {
                        contactsList.get(j).setEmail(t3);
                    }
                }
            }
            int kk=0;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || requestCode != REQUEST_CODE) {
            // no result back or request_code does not match
            return;
        }

        if(resultCode == RESULT_OK) {

            ArrayList<String> attendeesNameList = new ArrayList<String>();
            ArrayList<String> attendeesPhoneList = new ArrayList<String>();
            ArrayList<String> attendeesEmailList = new ArrayList<String>();

            attendeesNameList = data.getStringArrayListExtra(NameList) ;
            attendeesPhoneList = data.getStringArrayListExtra(PhoneList) ;
            attendeesEmailList = data.getStringArrayListExtra(EmailList) ;

            String attendees = ((TextView) findViewById(R.id.attendeeList)).getText().toString();

            for(int i=0; i<attendeesNameList.size(); ++i)
            {
                selectedContactsList.add(new Contacts("",attendeesNameList.get(i).toString(), attendeesPhoneList.get(i).toString(), attendeesEmailList.get(i).toString()));
                attendees = attendees + attendeesNameList.get(i).toString() + " ; ";
            }

            ((TextView) findViewById(R.id.attendeeList)).setText(attendees);
        }
    }

}
