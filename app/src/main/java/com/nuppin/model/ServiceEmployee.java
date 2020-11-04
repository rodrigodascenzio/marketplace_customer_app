package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ServiceEmployee implements Parcelable {

    private String employee_id;
    private String user_name;
    private List<Scheduling> scheduling;

    private ServiceEmployee(Parcel in) {
    }

    public List<Scheduling> getScheduling() {
        return scheduling;
    }

    public void setScheduling(List<Scheduling> scheduling) {
        this.scheduling = scheduling;
    }

    public String getUser_name() {
        return user_name;
    }

    public static final Creator<ServiceEmployee> CREATOR = new Creator<ServiceEmployee>() {
        @Override
        public ServiceEmployee createFromParcel(Parcel in) {
            return new ServiceEmployee(in);
        }

        @Override
        public ServiceEmployee[] newArray(int size) {
            return new ServiceEmployee[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}