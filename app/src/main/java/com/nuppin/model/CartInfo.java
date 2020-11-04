package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartInfo implements Parcelable {

    private String payment_method;
    private String type;
    private String user_id;
    private String company_id;
    private String coupon_id;

    protected CartInfo(Parcel in) {
        payment_method = in.readString();
        type = in.readString();
        user_id = in.readString();
        company_id = in.readString();
    }

    public static final Creator<CartInfo> CREATOR = new Creator<CartInfo>() {
        @Override
        public CartInfo createFromParcel(Parcel in) {
            return new CartInfo(in);
        }

        @Override
        public CartInfo[] newArray(int size) {
            return new CartInfo[size];
        }
    };

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(payment_method);
        parcel.writeString(type);
        parcel.writeString(user_id);
        parcel.writeString(company_id);
        parcel.writeString(coupon_id);
    }
}
