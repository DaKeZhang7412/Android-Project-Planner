package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * updated by JS Lim on 11/03/2015
 *      onDateSet();
 * Created by jung-sun on 10/24/2015.
 */

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private String[] stringDayOfWeek = new String[]{
            "Sun","Mon","Tue","Wed","Thur","Fri","Sat"
    };

    private int  r_id;
    private Calendar c;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        //final Calendar c;
        c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        r_id = getArguments().getInt("R.id");


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        c.set(year,month,day);  // added for getting changed DAY_OF_WEEK
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        ++month;    // month is 0-based
        TextView textView = (TextView) getActivity().findViewById(r_id);
        textView.setText("  " + year + "." + String.format("%02d",month) + "." + String.format("%02d",day) + " (" + stringDayOfWeek[dayOfWeek - 1] + ")");
    }
}