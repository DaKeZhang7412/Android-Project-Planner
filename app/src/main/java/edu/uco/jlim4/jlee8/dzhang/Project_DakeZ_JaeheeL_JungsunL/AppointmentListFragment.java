package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.database.DatabaseOperation;

/**
 * Created by Neo on 10/27/2015.
 */
public class AppointmentListFragment extends ListFragment {
    private ListSelectionListener mListener = null;
    Context context;

    private interface ListSelectionListener {
        public void onListSelection(int index);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        getListView().setItemChecked(pos, true);
        mListener.onListSelection(pos);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        DatabaseOperation databse = new DatabaseOperation(context);
        Cursor cr = databse.getInformation(0);
        if (cr!=null && cr.getCount()>0) {
            cr.moveToFirst();
            String appointmentList[] = null;
            int i = 0;
            while (cr.moveToNext()) {
                appointmentList[i] = cr.getString(0);
                i++;
            }
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    R.layout.appointment_list, appointmentList));

            try {
                mListener = (ListSelectionListener) getActivity();
            } catch (ClassCastException e) {
                throw new ClassCastException(getActivity().toString()
                        + " must implement OnArticleSelectedListener");
            }
        }
    }
}
