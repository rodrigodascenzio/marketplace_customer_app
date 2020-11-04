package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Order implements Parcelable{

    private String order_id;
    private String company_id;
    private String company_name;
    private String address;
    private String user_id;
    private double delivery_amount;
    private String note;
    private String status;
    private String payment_method;
    private double total_amount;
    private int rating;
    private String rating_note;
    private int is_chat_available;
    private String photo;
    private String created_date;
    private String type;
    private String category_company_id;
    private double discount_amount;
    private double subtotal_amount;
    private double latitude;
    private double longitude;
    private List<OrderItem> item;

    public Order() {
    }

    protected Order(Parcel in) {
        order_id = in.readString();
        company_id = in.readString();
        user_id = in.readString();
        company_name = in.readString();
        delivery_amount = in.readDouble();
        note = in.readString();
        status = in.readString();
        payment_method = in.readString();
        total_amount = in.readDouble();
        rating = in.readInt();
        address = in.readString();
        rating_note = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getIs_chat_available() {
        return is_chat_available;
    }

    public double getSubtotal_amount() {
        return subtotal_amount;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public String getNote() {
        return note;
    }

    public String getType() {
        return type;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getCategory_company_id() {
        return category_company_id;
    }

    public String getCreated_date() {
        return created_date;
    }

    public List<OrderItem> getItem() {
        return item;
    }

    public String getphoto() {
        return photo;
    }

    public String getAddress() {
        return address;
    }

    public int getRating() {
        return rating;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public double getDelivery_amount() {
        return delivery_amount;
    }

    public String getStatus() {
        return status;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setRating_note(String rating_note) {
        this.rating_note = rating_note;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(order_id);
        dest.writeString(company_id);
        dest.writeString(user_id);
        dest.writeString(company_name);
        dest.writeDouble(delivery_amount);
        dest.writeString(note);
        dest.writeString(status);
        dest.writeString(payment_method);
        dest.writeDouble(total_amount);
        dest.writeInt(rating);
        dest.writeString(address);
        dest.writeString(rating_note);
    }
}