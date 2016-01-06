package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by DakeZhang on 10/22/2015.
 */
public class DatabaseOperation extends SQLiteOpenHelper {

    public  static final int database_version = 1;
    public static final String DATABASE = "database.db";
    public static final String TABLE = "aptTable";
    public static final String  ID = "Id";
    public static final String TITLE = "Title";
    public static final String MEMO = "Memo";
    public static final String STARTDATE = "StartDate";
    public static final String STARTTIME = "StartTime";
    public static final String ENDDATE = "EndDate";
    public static final String ENDTIME = "EndTime";
    public static final String LOCATION = "Location";
    public static final String ATTENDEE ="Attendee";

   // SQLiteDatabase db = this.getWritableDatabase();

    public String CREATE_QUERY = "CREATE TABLE " + TABLE
            + " ("  + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TITLE + " TEXT, "
            + MEMO + " TEXT,"
            + STARTDATE + " TEXT, "
            + STARTTIME + " TEXT, "
            + ENDDATE + " TEXT, "
            + ENDTIME + " TEXT, "
            + LOCATION + " TEXT, "
            + ATTENDEE + " TEXT)";

    public DatabaseOperation(Context context) {
        super(context, DATABASE, null, database_version);
        Log.d("Database", "Database is created.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
        Log.d("Table", "Table is created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void pushInformation (String title, String memo,
                                 String startDate, String startTime,
                                 String endDate, String endTime,
                                 String location, String attendee){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(MEMO, memo);
        cv.put(STARTDATE, startDate);
        cv.put(STARTTIME, startTime);
        cv.put(ENDDATE, endDate);
        cv.put(ENDTIME,endTime);
        cv.put(LOCATION,location);
        cv.put(ATTENDEE, attendee);
        long k = db.insert(TABLE, null, cv);
        Log.d("Database", "One raw inserted.");
    }

    public Cursor getInformation(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select * from " + TABLE + " WHERE Id = " + id, null);
        return cr;
    }

    public Cursor getRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.rawQuery("select * from " + TABLE, null);
        return cr;
    }

    public boolean updateData ( int id,
                                String title, String memo,
                               String startDate, String startTime,
                               String endDate, String endTime,
                               String location, String attendee) {
        //update information in table

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(MEMO, memo);
        cv.put(STARTDATE, startDate);
        cv.put(STARTTIME, startTime);
        cv.put(ENDDATE, endDate);
        cv.put(ENDTIME, endTime);
        cv.put(LOCATION, location);
        cv.put(ATTENDEE, attendee);
        String i = id + "";
        db.update(TABLE, cv, "ID = ?", new String[] { i});
        Log.d("Table", "one raw has been updated.");
        return true;
    }

    public void deleteData (int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE,"ID = id", new String[] {});
    }
}
