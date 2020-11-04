package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Coupon implements Parcelable {

    private String coupon_id;
    private String company_id;
    private double value;
    private String discount_type;
    private double min_purchase;
    private int quantity;


    private int quantity_used;
    private int expires_day;
    private int expires_hour;
    private int expires_minute;
    private Company company;

    private Coupon(Parcel in) {
        coupon_id = in.readString();
        company_id = in.readString();
        value = in.readDouble();
        discount_type = in.readString();
        min_purchase = in.readDouble();
        quantity = in.readInt();
    }

    public Company getCompany() {
        return company;
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel in) {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };

    public int getQuantity_used() {
        return quantity_used;
    }

    public int getExpires_day() {
        return expires_day;
    }

    public int getExpires_hour() {
        return expires_hour;
    }

    public int getExpires_minute() {
        return expires_minute;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public double getValue() {
        return value;
    }

    public double getMin_purchase() {
        return min_purchase;
    }

    public int getQuantity() {
        return quantity;
    }


    public String getDiscount_type() {
        return discount_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(coupon_id);
        parcel.writeString(company_id);
        parcel.writeDouble(value);
        parcel.writeString(discount_type);
        parcel.writeDouble(min_purchase);
        parcel.writeInt(quantity);
    }
}
