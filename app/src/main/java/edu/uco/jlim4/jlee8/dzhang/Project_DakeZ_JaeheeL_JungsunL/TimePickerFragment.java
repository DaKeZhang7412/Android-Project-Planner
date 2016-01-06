package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by jung-sun on 10/24/2015.
 */
public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

        int r_id;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current time as the default value
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        r_id = getArguments().getInt("R.id");

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    // Listener for the TimePickerDialog
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        TextView textView = (TextView) getActivity().findViewById(r_id);
        textView.setText("  " + String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
    }
}
