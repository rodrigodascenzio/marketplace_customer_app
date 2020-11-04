package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Scheduling implements Parcelable {

    @Expose
    private String scheduling_id = "";
    @Expose
    private String company_id;
    @Expose
    private String user_id;
    @Expose
    private String service_id;
    @Expose
    private String employee_id;
    @Expose
    private String start_time;
    @Expose
    private String end_time;
    @Expose
    private String status = "pending";
    @Expose
    private String service_name;
    @Expose
    private String user_name;
    @Expose
    private String employee_name;
    @Expose
    private int rating;
    @Expose
    private String company_name;
    @Expose
    private double subtotal_amount;
    @Expose
    private int service_duration;
    @Expose
    private String address;
    @Expose
    private String note;
    @Expose
    private double discount_amount;
    @Expose
    private double total_amount;
    @Expose
    private String payment_method;
    @Expose
    private String coupon_id;
    @Expose
    private String rating_note;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private String type;
    @Expose
    private String source;

    //not exposed
    private String photo;
    private String created_date;
    private String start_date;
    private String target;
    private double coupon_value;
    private int is_chat_available;

    public Scheduling() {
    }

    protected Scheduling(Parcel in) {
        scheduling_id = in.readString();
        company_id = in.readString();
        user_id = in.readString();
        service_id = in.readString();
        employee_id = in.readString();
        start_time = in.readString();
        end_time = in.readString();
        status = in.readString();
        service_name = in.readString();
        user_name = in.readString();
        employee_name = in.readString();
        rating = in.readInt();
        company_name = in.readString();
        service_duration = in.readInt();
        address = in.readString();
        note = in.readString();
        subtotal_amount = in.readDouble();
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIs_chat_available() {
        return is_chat_available;
    }

    public double getCoupon_value() {
        return coupon_value;
    }

    public void setCoupon_value(double coupon_value) {
        this.coupon_value = coupon_value;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public void setRating_note(String rating_note) {
        this.rating_note = rating_note;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getphoto() {
        return photo;
    }

    public double getSubtotal_amount() {
        return subtotal_amount;
    }

    public void setSubtotal_amount(double subtotal_amount) {
        this.subtotal_amount = subtotal_amount;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public static final Creator<Scheduling> CREATOR = new Creator<Scheduling>() {
        @Override
        public Scheduling createFromParcel(Parcel in) {
            return new Scheduling(in);
        }

        @Override
        public Scheduling[] newArray(int size) {
            return new Scheduling[size];
        }
    };

    public String getScheduling_id() {
        return scheduling_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getStatus() {
        return status;
    }

    public String getService_name() {
        return service_name;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setService_duration(int service_duration) {
        this.service_duration = service_duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(scheduling_id);
        parcel.writeString(company_id);
        parcel.writeString(user_id);
        parcel.writeString(service_id);
        parcel.writeString(employee_id);
        parcel.writeString(start_time);
        parcel.writeString(end_time);
        parcel.writeString(status);
        parcel.writeString(service_name);
        parcel.writeString(user_name);
        parcel.writeString(employee_name);
        parcel.writeInt(rating);
        parcel.writeString(company_name);
        parcel.writeInt(service_duration);
        parcel.writeString(address);
        parcel.writeString(note);
        parcel.writeDouble(subtotal_amount);
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
