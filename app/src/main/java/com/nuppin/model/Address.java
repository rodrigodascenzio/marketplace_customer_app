package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {

    private String address_id = "";
    private String user_id;
    private double latitude;
    private double longitude;
    private String full_address;
    private String street;
    private String street_number;
    private String district;
    private String city;
    private String state_code;
    private String complement;
    private String country_code;
    private int is_selected = 1;

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String endu_country_code) {
        this.country_code = endu_country_code;
    }

    public int getIs_selected() {
        return is_selected;
    }

    public void setIs_selected(int is_selected) {
        this.is_selected = is_selected;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAddress_id() {
        return address_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFull_address() {
        return full_address;
    }

    public void setFull_address(String full_address) {
        this.full_address = full_address;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet_number() {
        return street_number;
    }

    public void setStreet_number(String street_number) {
        this.street_number = street_number;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    protected Address(Parcel in) {
        address_id = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        full_address = in.readString();
        street = in.readString();
        street_number = in.readString();
        district = in.readString();
        city = in.readString();
        complement = in.readString();
        user_id = in.readString();
        is_selected = in.readInt();
    }

    public Address() {
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address_id);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(full_address);
        parcel.writeString(street);
        parcel.writeString(street_number);
        parcel.writeString(district);
        parcel.writeString(city);
        parcel.writeString(complement);
        parcel.writeString(user_id);
        parcel.writeInt(is_selected);
    }
}
