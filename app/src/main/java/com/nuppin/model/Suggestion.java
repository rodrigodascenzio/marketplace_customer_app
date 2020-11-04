package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Suggestion implements Parcelable {
    private String company_name;
    private String detail;
    private String user_id;
    private String latitude;
    private String longitude;
    private String instagram, facebook, whatsapp;

    protected Suggestion(Parcel in) {
        company_name = in.readString();
        detail = in.readString();
        user_id = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<Suggestion> CREATOR = new Creator<Suggestion>() {
        @Override
        public Suggestion createFromParcel(Parcel in) {
            return new Suggestion(in);
        }

        @Override
        public Suggestion[] newArray(int size) {
            return new Suggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(company_name);
        parcel.writeString(detail);
        parcel.writeString(user_id);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public Suggestion() {

    }
}
