package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CartCompany implements Parcelable {

    
    private String company_id;
    private String user_address;
    private String user_latitude;
    private String user_longitude;
    private String name;
    private double min_purchase;
    private double delivery_fixed_fee;
    private String category_company_id;
    private String subcategory_company_id;
    private double subtotal_amount;
    private int is_available;
    private double distance;
    private int delivery_type_value;
    private String delivery_min_time;
    private String delivery_max_time;
    private double delivery_variable_fee;
    private int max_radius_free;
    private int max_radius;
    private String model_type;
    private int is_delivery;
    private int is_local;
    private int is_online;
    private String photo;
    private String full_address;
    private String latitude;
    private String longitude;
    private String user_id;
    private double rating;
    private int num_rating;
    private List<CartProduct> product;
    private double discount_amount;
    private double delivery_amount;
    private String cart_note;
    private CartInfo info;
    private double total_amount;
    private String not_enough_amount_for_free_delivery;
    private String free_delivery_over_available;

    public String getNot_enough_amount_for_free_delivery() {
        return not_enough_amount_for_free_delivery;
    }

    public String getFree_delivery_over_available() {
        return free_delivery_over_available;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public CartInfo getInfo() {
        return info;
    }

    public int getIs_delivery() {
        return is_delivery;
    }

    public int getIs_local() {
        return is_local;
    }

    public double getDelivery_amount() {
        return delivery_amount;
    }

    public double getRating() {
        return rating;
    }

    public int getCart_sto_numRating() {
        return num_rating;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFull_address() {
        return full_address;
    }


    public String getphoto() {
        return photo;
    }

    public String getCart_note() {
        return cart_note;
    }

    public void setCart_note(String cart_note) {
        this.cart_note = cart_note;
    }

    public int getDelivery_type_value() {
        return delivery_type_value;
    }

    public String getUser_address() {
        return user_address;
    }

    public String getCategory_company_id() {
        return category_company_id;
    }

    public int getIs_available() {
        return is_available;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getName() {
        return name;
    }

    public double getMin_purchase() {
        return min_purchase;
    }

    public int getIs_online() {
        return is_online;
    }

    public double getSubtotal_amount() {
        return subtotal_amount;
    }

    public List<CartProduct> getProduct() {
        return product;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }


    protected CartCompany(Parcel in) {
        company_id = in.readString();
        name = in.readString();
        min_purchase = in.readDouble();
        subtotal_amount = in.readDouble();
        is_available = in.readInt();
        product = in.createTypedArrayList(CartProduct.CREATOR);
        category_company_id = in.readString();
        subcategory_company_id = in.readString();
        user_address = in.readString();
        distance = in.readDouble();
        delivery_type_value = in.readInt();
        delivery_min_time = in.readString();
        delivery_max_time = in.readString();
        delivery_variable_fee = in.readDouble();
        max_radius_free = in.readInt();
        max_radius = in.readInt();
        model_type = in.readString();
        is_delivery = in.readInt();
        is_local = in.readInt();
        discount_amount = in.readDouble();
        delivery_fixed_fee = in.readDouble();
        cart_note = in.readString();
        is_online = in.readInt();
    }

    public static final Creator<CartCompany> CREATOR = new Creator<CartCompany>() {
        @Override
        public CartCompany createFromParcel(Parcel in) {
            return new CartCompany(in);
        }

        @Override
        public CartCompany[] newArray(int size) {
            return new CartCompany[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(company_id);
        parcel.writeString(name);
        parcel.writeDouble(min_purchase);
        parcel.writeDouble(subtotal_amount);
        parcel.writeInt(is_available);
        parcel.writeTypedList(product);
        parcel.writeString(category_company_id);
        parcel.writeString(subcategory_company_id);
        parcel.writeString(user_address);
        parcel.writeDouble(distance);
        parcel.writeInt(delivery_type_value);
        parcel.writeString(delivery_min_time);
        parcel.writeString(delivery_max_time);
        parcel.writeDouble(delivery_variable_fee);
        parcel.writeInt(max_radius_free);
        parcel.writeInt(max_radius);
        parcel.writeString(model_type);
        parcel.writeInt(is_delivery);
        parcel.writeInt(is_local);
        parcel.writeDouble(discount_amount);
        parcel.writeDouble(delivery_fixed_fee);
        parcel.writeString(cart_note);
        parcel.writeInt(is_online);
    }
}