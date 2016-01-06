package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL.database;

/**
 * Created by jung-sun on 11/9/2015.
 * updated by JS Lim on 11/23 - implement comparable
 */
public class Appointment implements Comparable<Appointment>{
    private String Hostid;
    private int id;
    private String title;
    private String memo;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String location;
    private String reminder;
    private String owner;

    public Appointment(String Hostid, int id, String title, String memo, String startDate, String startTime, String endDate, String endTime,String location,String reminder,String owner) {
        this.Hostid = Hostid;
        this.id = id;
        this.title = title;
        this.memo = memo;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.location = location;
        this.reminder = reminder;
        this.owner = owner;
    }


    public Appointment(int id, String startDate, String title) {
        this.id = id;
        this.startDate = startDate;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getMemo() {
        return memo;
    }

    public String getreminder() {
        return reminder;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getLocation() {
        return location;
    }

    public String getHostid() {
        return Hostid;
    }

    public String getOwner() {
        return owner;
    }

    // added by JS Lim on 11/23/2015
    @Override
    public int compareTo(Appointment o) {
        return getStartDate().compareTo(o.getStartDate());
    }
}
