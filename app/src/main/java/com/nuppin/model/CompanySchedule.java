package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CompanySchedule implements Parcelable {

    private String company_id;
    private String weekday_id;
    private String weekday_end_id;
    private String start_time;
    private String end_time;

    private CompanySchedule(Parcel in) {
        company_id = in.readString();
        weekday_id = in.readString();
        weekday_end_id = in.readString();
        start_time = in.readString();
        end_time = in.readString();
    }

    public CompanySchedule() {

    }

    public static final Creator<CompanySchedule> CREATOR = new Creator<CompanySchedule>() {
        @Override
        public CompanySchedule createFromParcel(Parcel in) {
            return new CompanySchedule(in);
        }

        @Override
        public CompanySchedule[] newArray(int size) {
            return new CompanySchedule[size];
        }
    };

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getWeekday_id() {
        return weekday_id;
    }

    public void setWeekday_id(String weekday_id) {
        this.weekday_id = weekday_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getWeekday_end_id() {
        return weekday_end_id;
    }

    public void setWeekday_end_id(String weekday_end_id) {
        this.weekday_end_id = weekday_end_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(company_id);
        parcel.writeString(weekday_id);
        parcel.writeString(weekday_end_id);
        parcel.writeString(start_time);
        parcel.writeString(end_time);
    }
}
