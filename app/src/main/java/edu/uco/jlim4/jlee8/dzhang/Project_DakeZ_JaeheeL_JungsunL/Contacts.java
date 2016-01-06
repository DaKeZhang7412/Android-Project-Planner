package edu.uco.jlim4.jlee8.dzhang.Project_DakeZ_JaeheeL_JungsunL;

/**
 * Created by jung-sun on 11/20/2015.
 */
public class Contacts {
    private String _ID;
    private String name;
    private String phoneNumber;
    private String email;
    private Boolean checked;

    public Contacts(String _ID, String name, String phoneNumber, String email) {
        this._ID = _ID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.checked = false;
    }

    public String get_ID() {
        return _ID;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
