package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CompanyPayment implements Parcelable {

    private String payment_id;
    private String name;
    private String is_checked;
    private String company_id;

    public CompanyPayment() {

    }


    private CompanyPayment(Parcel in) {
        payment_id = in.readString();
        name = in.readString();
        is_checked = in.readString();
        company_id = in.readString();
    }

    public static final Creator<CompanyPayment> CREATOR = new Creator<CompanyPayment>() {
        @Override
        public CompanyPayment createFromParcel(Parcel in) {
            return new CompanyPayment(in);
        }

        @Override
        public CompanyPayment[] newArray(int size) {
            return new CompanyPayment[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getPayment_id() {
        return payment_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(payment_id);
        parcel.writeString(name);
        parcel.writeString(is_checked);
        parcel.writeString(company_id);
    }
}
