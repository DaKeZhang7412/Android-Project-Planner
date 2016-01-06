package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.database.Appointment;
import edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.database.DatabaseHelper;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;

public class MainActivity extends Activity {

    //Updated by JH Lee on 10/27/2015
    String[] appointmentBrief, appointmentDetail;
    Context context = this;
    ArrayAdapter adapter;

    // added by JS Lim on 11/09/2015
    static DatabaseHelper dbHelper;
    private ArrayList<Appointment> appointmentArrayList;

    private int m_year, m_month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //=====> added by JS Lim on 11/09/2015
        dbHelper = new DatabaseHelper(this);    // create database
        dbHelper.initializeID();
        //dbHelper.insertAppointment("test1","","2015.11.07","01:20","","","","");
        //dbHelper.insertAppointment("test2","","2015.11.08","01:20","","", "", "");
        //dbHelper.insertAppointment("test3","","2015.11.09","01:20","","","","");


        //Appointment appointment = dbHelper.getAppointment(1);      // get the first appointment.

        //ArrayList<String>contactArrayList = dbHelper.getAttendeeList(0);

        //<===== added by JS Lim on 11/09/2015

        ((Button) findViewById(R.id.addAppointment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddAppointmentActivity.class);
                startActivity(intent);
            }
        });


        //Calendar
        CalendarView calendar = (CalendarView) findViewById(R.id.calendarView);

        //==> Added by JS Lim on 11/17/2015
        Calendar c;
        c = Calendar.getInstance();

        m_year = c.get(Calendar.YEAR);
        m_month = c.get(Calendar.MONTH) + 1;      // month is 0-based.

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                if (m_year != year || m_month != month + 1) {
                    m_year = year;
                    m_month = month + 1;
                    displayList(m_year, m_month);
                }
            }
        });
        //<========

        //=====> added by  JH LEE on 11/10/2015
        //ListView for registered appointment list of the month in the calendar
        ListView listView = (ListView) findViewById(R.id.appointmentListView);
        displayList(m_year, m_month);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                @Override
                                                public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                                                    final int position = pos;

                                                    if (appointmentArrayList.get(position).getOwner().contains("1")) {
                                                        new AlertDialog.Builder(context)
                                                                .setTitle("Edit or Delete ")
                                                                .setMessage(appointmentDetail[pos])
                                                                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                                                        intent.putExtra(DetailActivity.HOST_ID, "" + appointmentArrayList.get(position).getHostid());
                                                                        intent.putExtra("DETAIL_OF_APPOINTMENT", "" + appointmentArrayList.get(position).getId());
                                                                        startActivity(intent);
                                                                    }
                                                                })
                                                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        new AlertDialog.Builder(context)
                                                                                .setMessage("Do you really want to delete this appointment?")
                                                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        //delete the chosen appointment
                                                                                        Boolean isDeleteSuccess = dbHelper.deleteAppointment(appointmentArrayList.get(position).getHostid(), appointmentArrayList.get(position).getId());
                                                                                        if (isDeleteSuccess) {
                                                                                            Toast.makeText(MainActivity.this, "Successfully deleted !!", Toast.LENGTH_SHORT).show();
                                                                                            displayList(m_year, m_month);
                                                                                            return;
                                                                                        }
                                                                                    }
                                                                                })
                                                                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                    }
                                                                                })
                                                                                .show();
                                                                    }
                                                                })
                                                                .show();
                                                        //return true;
                                                    } else {
                                                        new AlertDialog.Builder(context)
                                                                .setTitle("Appointment Detail")//.setTitle("Edit or Delete ")
                                                                .setMessage(appointmentDetail[pos])
                                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                    }
                                                                })
                                                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        new AlertDialog.Builder(context)
                                                                                .setMessage("Do you really want to delete this appointment?")
                                                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        //delete the chosen appointment
                                                                                        Boolean isDeleteSuccess = dbHelper.deleteAppointment(appointmentArrayList.get(position).getHostid(), appointmentArrayList.get(position).getId());
                                                                                        if (isDeleteSuccess) {
                                                                                            Toast.makeText(MainActivity.this, "Successfully deleted !!", Toast.LENGTH_SHORT).show();
                                                                                            displayList(m_year, m_month);
                                                                                            return;
                                                                                        }
                                                                                    }
                                                                                })
                                                                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                    }
                                                                                })
                                                                                .show();
                                                                    }
                                                                }).show();

                                                    }
                                                    return true;
                                                }
                                            }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, "android.permission.SMS_RECEIVED")
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.SMS_RECEIVED"},
                    1);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        displayList(m_year, m_month);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        displayList(m_year, m_month);
    }

    public void displayList(int year, int month) {
        appointmentArrayList = dbHelper.getAppointment(year, month);

        Collections.sort(appointmentArrayList);

        ListView listView = (ListView) findViewById(R.id.appointmentListView);

        //build format for displaying the appointment
        appointmentBrief = new String[appointmentArrayList.size()];
        appointmentDetail = new String[appointmentArrayList.size()];

        for (int i = 0; i < appointmentArrayList.size(); i++) {
            appointmentDetail[i] =
                      "Date  : " + appointmentArrayList.get(i).getStartDate()  + "\n"
                    + "Title : " + appointmentArrayList.get(i).getTitle() + "\n"
                    + "Time  :  " + appointmentArrayList.get(i).getStartTime() + "\n"
                    + "Place : " + appointmentArrayList.get(i).getLocation() + "\n"
                    + "Memo  :  " + appointmentArrayList.get(i).getMemo();

            appointmentBrief[i] = "DATE     " + appointmentArrayList.get(i).getStartDate() + "\n"
                    + "TITLE    " + appointmentArrayList.get(i).getTitle();
        }
        adapter = new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.item_view,
                appointmentBrief
        );

        listView.setAdapter(adapter);
        listView.setChoiceMode(CHOICE_MODE_SINGLE);
    }
}
