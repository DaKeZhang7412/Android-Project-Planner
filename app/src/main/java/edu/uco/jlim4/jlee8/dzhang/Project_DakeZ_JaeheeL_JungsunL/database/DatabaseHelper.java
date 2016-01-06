package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.Attendee;

/**
 * Created by jung-sun on 11/9/2015.
 * modified by JS Lim on 11/23/2015
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    static final int database_version = 1;
    static final String DBNAME = "MobilePG.db";
    static final String USER_TABLE = "Contact";
    //static final String HOST_ID = "HostID";
    static final String USER_ID = "ContactID";
    static final String USER_NAME = "ContactName";
    static final String USER_PHONE = "ContactPhoneNo";
    static final String USER_EMAIL = "ContactEmail";

    static final String APPOINTMENT_TABLE = "Appointment";
    static final String HOST_ID = "HostID";
    static final String COL_ID = "AppId";
    static final String COL_TITLE = "Title";
    static final String COL_MEMO = "Memo";
    static final String COL_STARTDATE = "StartDate";
    static final String COL_STARTTIME = "StartTime";
    static final String COL_ENDDATE = "EndDate";
    static final String COL_ENDTIME = "EndTime";
    static final String COL_LOCATION = "Location";
    static final String COL_REMIDER = "Remider";
    static final String COL_OWNER = "Owner";

    static final String ATTENDEE_TABLE = "Attendee";
    static final String ATTENDEE_HOST_ID = "HostID";
    static final String ATTENDEE_APP_ID = "AppID";
    static final String ATTENDEE_CONTACT_ID = "ContactID";

    static final String ID_COUNTER_TABLE = "IDCounter";
    static final String ID = "ID";
    static final String NEXT_APP_ID = "NextAppID";

    static final String VIEW_ATTENDEE_TABLE = "ViewAttendee";

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USER_TABLE + "(" + USER_ID + " INTEGER PRIMARY KEY autoincrement , " +
                USER_NAME + " TEXT NOT NULL, " + USER_PHONE + " INTEGER NOT NULL, " + USER_EMAIL + " TEXT)");

        db.execSQL("CREATE TABLE " + APPOINTMENT_TABLE + "(" + HOST_ID + " TEXT," + COL_ID + " INTEGER," +
                COL_TITLE + " TEXT, " + COL_MEMO + " TEXT, " + COL_STARTDATE + " TEXT NOT NULL, " +
                COL_STARTTIME + " TEXT, " + COL_ENDDATE + " TEXT, " + COL_ENDTIME + " TEXT, " +
                COL_LOCATION + " TEXT, " + COL_REMIDER + " TEXT, " + COL_OWNER + " TEXT, PRIMARY KEY (" + HOST_ID + " , " + COL_ID + "))");

        //db.execSQL("CREATE TABLE " + ATTENDEE_TABLE + "( HostID, AppID, ContactID, PRIMARY KEY (HostID, AppID , ContactID))");
        db.execSQL("CREATE TABLE " + ATTENDEE_TABLE + "(AppID, ContactID, PRIMARY KEY ( AppID , ContactID))");

        db.execSQL("CREATE TABLE " + ID_COUNTER_TABLE + "(" + ID + " PRIMARY KEY , " + NEXT_APP_ID + " INTEGER NOT NULL)");
        //db.execSQL("CREATE VIEW " + VIEW_ATTENDEE_TABLE + " AS SELECT " + APPOINTMENT_TABLE + "." + COL_ID + " AS _id," ATTENDEE_CONTACT_ID + ")");

    }

    public boolean initializeID() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + ID_COUNTER_TABLE;
        Cursor cr = db.rawQuery(query, null);

        if (cr.getCount() <= 0) {

            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ID, "ID");
            cv.put(NEXT_APP_ID, 1);
            db.insert(ID_COUNTER_TABLE, null, cv);
            db.close();
        }

        //cr.moveToFirst();
        //int id = cr.getInt(cr.getColumnIndex(NEXT_APP_ID));

        return true;
    }

    public int getNextID() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + ID_COUNTER_TABLE;
        Cursor cr = db.rawQuery(query, null);
        int id = -1;
        if (cr.getCount() > 0) {
            cr.moveToFirst();

            id = cr.getInt(cr.getColumnIndex(NEXT_APP_ID));

            return id;
        } else
            return -1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertAppointment(String hostID, String title, String memo,
                                  String startDate, String startTime,
                                  String endDate, String endTime,
                                  String location, String reminder, String owner) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + APPOINTMENT_TABLE + " WHERE " + COL_STARTDATE + " like '" +
                startDate + "' and " + COL_TITLE + "= '" + title + "'";
        //Cursor cr = db.rawQuery("select * from Appointment WHERE StartDate like '2015.11.%'", null);
        Cursor cr = db.rawQuery(query, null);
        if (cr.getCount() > 0) {
            db.close();
            return -1;
        }

        query = "select * from " + ID_COUNTER_TABLE;
        cr = db.rawQuery(query, null);

        int id = 0;
        if (cr.getCount() > 0) {
            cr.moveToFirst();
            id = cr.getInt(cr.getColumnIndex(NEXT_APP_ID));
        }

        if (id < 1) {
            db.close();
            return -1;
        }

        db.close();

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HOST_ID, hostID);
        cv.put(COL_ID, id);
        cv.put(COL_TITLE, title);
        cv.put(COL_MEMO, memo);
        cv.put(COL_STARTDATE, startDate);
        cv.put(COL_STARTTIME, startTime);
        cv.put(COL_ENDDATE, endDate);
        cv.put(COL_ENDTIME, endTime);
        cv.put(COL_LOCATION, location);
        cv.put(COL_REMIDER, reminder);
        cv.put(COL_OWNER, owner);
        long k = db.insert(APPOINTMENT_TABLE, null, cv);
        Log.d("Database", "One raw inserted.");

        if (k > 0) {
            cv = new ContentValues();
            cv.put(NEXT_APP_ID, id + 1);
            int row = db.update(ID_COUNTER_TABLE, cv, "ID = ?", new String[]{"ID"});

            k = id;
        }

        db.close();

        return k;
    }

    public long insertAppointmentReceive(String hostID, int id, String title, String memo,
                                  String startDate, String startTime,
                                  String endDate, String endTime,
                                  String location, String reminder, String owner) {

       /* SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + APPOINTMENT_TABLE + " WHERE " + COL_STARTDATE + " like '" +
                startDate + "' and " + COL_TITLE + "= '" + title + "'";
        //Cursor cr = db.rawQuery("select * from Appointment WHERE StartDate like '2015.11.%'", null);
        Cursor cr = db.rawQuery(query, null);
        if (cr.getCount() > 0) {
            db.close();
            return -1;
        }

        query = "select * from " + ID_COUNTER_TABLE;
        cr = db.rawQuery(query, null);

        int id = 0;
        if (cr.getCount() > 0) {
            cr.moveToFirst();
            id = cr.getInt(cr.getColumnIndex(NEXT_APP_ID));
        }

        if (id < 1) {
            db.close();
            return -1;
        }

        db.close();
*/
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HOST_ID, hostID);
        cv.put(COL_ID, id);
        cv.put(COL_TITLE, title);
        cv.put(COL_MEMO, memo);
        cv.put(COL_STARTDATE, startDate);
        cv.put(COL_STARTTIME, startTime);
        cv.put(COL_ENDDATE, endDate);
        cv.put(COL_ENDTIME, endTime);
        cv.put(COL_LOCATION, location);
        cv.put(COL_REMIDER, reminder);
        cv.put(COL_OWNER, owner);
        long k = db.insert(APPOINTMENT_TABLE, null, cv);
        Log.d("Database", "One raw inserted.");

/*        if (k > 0) {
            cv = new ContentValues();
            cv.put(NEXT_APP_ID, id + 1);
            int row = db.update(ID_COUNTER_TABLE, cv, "ID = ?", new String[]{"ID"});

            k = id;
        }
*/
        db.close();

        return k;
    }

    /*
    public long insertAppointment (String title, String memo,
                                 String startDate, String startTime,
                                 String endDate, String endTime,
                                 String location, String reminder){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + APPOINTMENT_TABLE + " WHERE " + COL_STARTDATE + " like '" +
                startDate + "' and " + COL_TITLE + "= '" + title + "'";
        //Cursor cr = db.rawQuery("select * from Appointment WHERE StartDate like '2015.11.%'", null);
        Cursor cr = db.rawQuery(query, null);
        if(cr.getCount() > 0)
        {
            db.close();
            return -1;
        }

        db.close();

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_MEMO, memo);
        cv.put(COL_STARTDATE, startDate);
        cv.put(COL_STARTTIME, startTime);
        cv.put(COL_ENDDATE, endDate);
        cv.put(COL_ENDTIME,endTime);
        cv.put(COL_LOCATION,location);
        cv.put(COL_REMIDER, reminder);
        long k = db.insert(APPOINTMENT_TABLE, null, cv);
        Log.d("Database", "One raw inserted.");
        db.close();
        return k;
    }
*/
    // modify format of month from "%2." to "%02d"
    public ArrayList<Appointment> getAppointment(int year, int month) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + APPOINTMENT_TABLE + " WHERE " + COL_STARTDATE + " like '" + String.format("%4d.%02d.", year, month) + "%'";
        //Cursor cr = db.rawQuery("select * from Appointment WHERE StartDate like '2015.11.%'", null);
        Cursor cr = db.rawQuery(query, null);

        ArrayList<Appointment> appointmentArrayList = new ArrayList<Appointment>();
        if (cr.getCount() > 0) {
            cr.moveToFirst();
            for (int i = 0; i < cr.getCount(); ++i) {
                appointmentArrayList.add(new Appointment(cr.getString(cr.getColumnIndex(HOST_ID)), cr.getInt(cr.getColumnIndex(COL_ID)), cr.getString(cr.getColumnIndex(COL_TITLE)), cr.getString(cr.getColumnIndex(COL_MEMO)),
                        cr.getString(cr.getColumnIndex(COL_STARTDATE)), cr.getString(cr.getColumnIndex(COL_STARTTIME)),
                        cr.getString(cr.getColumnIndex(COL_ENDDATE)), cr.getString(cr.getColumnIndex(COL_ENDTIME)),
                        cr.getString(cr.getColumnIndex(COL_LOCATION)), cr.getString(cr.getColumnIndex(COL_REMIDER)), cr.getString(cr.getColumnIndex(COL_OWNER))));
                cr.moveToNext();
            }
        }
        db.close();

        return appointmentArrayList;
    }

    public Appointment getAppointment(String hostID, int appID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + APPOINTMENT_TABLE + " WHERE " + HOST_ID + " = " + hostID + " AND " + COL_ID + " = " + appID;
        Cursor cr = db.rawQuery(query, null);

        Appointment appointment = null;
        //ArrayList<Appointment> appointmentArrayList = new ArrayList<Appointment>();
        if (cr.getCount() > 0) {
            cr.moveToFirst();
            //for(int i =0; i< cr.getCount(); ++i)
            {
                appointment = new Appointment(cr.getString(cr.getColumnIndex(HOST_ID)), cr.getInt(cr.getColumnIndex(COL_ID)), cr.getString(cr.getColumnIndex(COL_TITLE)), cr.getString(cr.getColumnIndex(COL_MEMO)),
                        cr.getString(cr.getColumnIndex(COL_STARTDATE)), cr.getString(cr.getColumnIndex(COL_STARTTIME)),
                        cr.getString(cr.getColumnIndex(COL_ENDDATE)), cr.getString(cr.getColumnIndex(COL_ENDTIME)),
                        cr.getString(cr.getColumnIndex(COL_LOCATION)), cr.getString(cr.getColumnIndex(COL_REMIDER)), cr.getString(cr.getColumnIndex(COL_OWNER)));
                //    cr.moveToNext();
            }
        }
        db.close();
        return appointment;
    }

    //public boolean insertAttendee (String host_ID, long appointmentID, ArrayList<Attendee> attendees) {
    public boolean insertAttendee(long appointmentID, ArrayList<Attendee> attendees) {
        insertContact(attendees);

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> contactIDArrayList = new ArrayList<Integer>();

        for (Attendee contact : attendees) {
            String query = "select * from " + USER_TABLE + " WHERE " + USER_PHONE + " like '" +
                    contact.getPhoneNumber().toString() + "'";
            Cursor cr = db.rawQuery(query, null);
            if (cr.getCount() == 0) {
                return false;
            } else {
                cr.moveToFirst();
                int index = cr.getColumnIndex(USER_ID);
                String id = cr.getString(index);
                Integer iid = Integer.valueOf(id);
                contactIDArrayList.add(iid);
            }
        }

        db.close();

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        for (Integer contactID : contactIDArrayList) {
            cv.put(ATTENDEE_APP_ID, appointmentID);
            cv.put(ATTENDEE_CONTACT_ID, contactID.intValue());
            long k = db.insert(ATTENDEE_TABLE, null, cv);
            Log.d("Database", "One raw inserted.");
        }
        db.close();

        return true;

    }

    /*
    public boolean insertAttendee (long appointmentID, ArrayList<Attendee> attendees) {

        insertContact(attendees);

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> contactIDArrayList= new ArrayList<Integer>();

        for (Attendee contact:attendees) {
            String query = "select * from " + USER_TABLE + " WHERE " + USER_PHONE + " like '" +
                    contact.getPhoneNumber().toString() + "'";
            Cursor cr = db.rawQuery(query, null);
            if(cr.getCount() == 0)
            {
                return false;
            }
            else
            {
                cr.moveToFirst();
                int index = cr.getColumnIndex(USER_ID);
                String id = cr.getString(index) ;
                Integer iid = Integer.valueOf(id);
                contactIDArrayList.add(iid);
            }
        }

        db.close();

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        for (Integer contactID:contactIDArrayList) {
            cv.put(ATTENDEE_APP_ID, appointmentID);
            cv.put(ATTENDEE_CONTACT_ID, contactID.intValue());
            long k = db.insert(ATTENDEE_TABLE, null, cv);
            Log.d("Database", "One raw inserted.");
        }
        db.close();

        return true;

    }

*/
    public void insertContact(ArrayList<Attendee> attendees) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Attendee> newContact = new ArrayList<Attendee>();
        ArrayList<Attendee> existContact = new ArrayList<Attendee>();
        ArrayList<Integer> existUserID = new ArrayList<Integer>();

        for (Attendee contact : attendees) {
            String query = "select * from " + USER_TABLE + " WHERE " + USER_PHONE + " like '" +
                    contact.getPhoneNumber().toString() + "'";
            Cursor cr = db.rawQuery(query, null);
            if (cr.getCount() == 0) {
                newContact.add(contact);
            } else {
                cr.moveToNext();
                existUserID.add(Integer.valueOf(cr.getInt(cr.getColumnIndex(USER_ID))));
                existContact.add(contact);
            }
        }
        db.close();

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (Attendee contact : newContact) {
            cv.put(USER_NAME, contact.getName());
            cv.put(USER_PHONE, contact.getPhoneNumber());
            cv.put(USER_EMAIL, contact.getEmail());
            long k = db.insert(USER_TABLE, null, cv);
            Log.d("Database", "One raw inserted.");
        }

        int i = 0;
        for (Attendee contact : existContact) {
            cv.put(USER_NAME, contact.getName());
            cv.put(USER_PHONE, contact.getPhoneNumber());
            cv.put(USER_EMAIL, contact.getEmail());
            String id = existUserID.get(i).toString() + "";
            long k = db.update(USER_TABLE, cv, "ContactID = ?", new String[]{id});
            Log.d("Database", "One raw inserted.");
        }
        db.close();
    }

    public ArrayList<String> getAttendeeNameList(long appointmentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + ATTENDEE_TABLE + " WHERE " + ATTENDEE_APP_ID + " like '" +
                appointmentID + "'";
        //Cursor cr = db.rawQuery("select * from Appointment WHERE StartDate like '2015.11.%'", null);
        Cursor cr = db.rawQuery(query, null);

        ArrayList<String> contactArrayList = new ArrayList<String>();
        if (cr.getCount() > 0) {
            cr.moveToFirst();
            for (int i = 0; i < cr.getCount(); ++i) {
                contactArrayList.add(cr.getString(cr.getColumnIndex(ATTENDEE_CONTACT_ID)));
                cr.moveToNext();
            }
        }
        db.close();

        return contactArrayList;
    }

    // Added by JS Lim on 11/16/2015
    public ArrayList<Attendee> getAttendeeList(long appointmentID) {

        ArrayList<Attendee> contactArrayList = new ArrayList<Attendee>();

        SQLiteDatabase db = this.getWritableDatabase();

        //db.execSQL("DROP VIEW " + VIEW_ATTENDEE_TABLE);
        db.execSQL("CREATE VIEW " + VIEW_ATTENDEE_TABLE + " AS SELECT ContactID from " + ATTENDEE_TABLE + " WHERE AppID = " + appointmentID);
        db.close();

        try {

            db = this.getReadableDatabase();

            String query = "select " + USER_NAME + "," + USER_PHONE + ", " + USER_EMAIL + " from " + USER_TABLE + " INNER JOIN " + VIEW_ATTENDEE_TABLE + " ON " + USER_TABLE + "." + USER_ID + " = " + VIEW_ATTENDEE_TABLE +
                    "." + ATTENDEE_CONTACT_ID;
            //Cursor cr = db.rawQuery("select * from Appointment WHERE StartDate like '2015.11.%'", null);
            Cursor cr = db.rawQuery(query, null);

            if (cr.getCount() > 0) {
                cr.moveToFirst();
                for (int i = 0; i < cr.getCount(); ++i) {
                    contactArrayList.add(new Attendee(cr.getString(cr.getColumnIndex(USER_NAME)), cr.getString(cr.getColumnIndex(USER_PHONE)), cr.getString(cr.getColumnIndex(USER_EMAIL))));
                    cr.moveToNext();
                }
            }
            db.close();

        } catch (Exception e) {
            db = this.getWritableDatabase();
            db.execSQL("DROP VIEW " + VIEW_ATTENDEE_TABLE);
            db.close();

            return null;
        }

        db = this.getWritableDatabase();
        db.execSQL("DROP VIEW " + VIEW_ATTENDEE_TABLE);
        db.close();

        return contactArrayList;
    }

    //====>added by JH Lee 11/10/2015
    public boolean deleteAppointment(String hostID, int appointmentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(APPOINTMENT_TABLE, HOST_ID + " LIKE " + hostID + " and " + COL_ID + "=" + appointmentID, null) > 0;
    }

    public boolean updateAppointment(String hostID, int appointmentID, String title, String memo,
                                     String startDate, String startTime,
                                     String endDate, String endTime,
                                     String location, String reminder) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_MEMO, memo);
        cv.put(COL_STARTDATE, startDate);
        cv.put(COL_STARTTIME, startTime);
        cv.put(COL_ENDDATE, endDate);
        cv.put(COL_ENDTIME, endTime);
        cv.put(COL_LOCATION, location);
        cv.put(COL_REMIDER, reminder);
        String i = appointmentID + "";
        int row = db.update(APPOINTMENT_TABLE, cv, "AppId = ? and HostID = ?", new String[]{i, hostID});
        Log.d("Table", "one raw has been updated.");
        db.close();

        return true;
    }
}
