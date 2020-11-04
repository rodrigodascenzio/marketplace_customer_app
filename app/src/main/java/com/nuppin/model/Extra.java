package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Extra implements Parcelable {

    private String extra_id;
    private String name;
    private double price;
    private String description;
    private int quantity;

    public Extra(){}

    protected Extra(Parcel in) {
        extra_id = in.readString();
        name = in.readString();
        price = in.readDouble();
        description = in.readString();
        quantity = in.readInt();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static final Creator<Extra> CREATOR = new Creator<Extra>() {
        @Override
        public Extra createFromParcel(Parcel in) {
            return new Extra(in);
        }

        @Override
        public Extra[] newArray(int size) {
            return new Extra[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtra_id() {
        return extra_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(extra_id);
        parcel.writeString(name);
        parcel.writeDouble(price);
        parcel.writeString(description);
        parcel.writeInt(quantity);
    }
}
