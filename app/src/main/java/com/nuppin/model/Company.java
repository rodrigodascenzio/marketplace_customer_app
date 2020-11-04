package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Company implements Parcelable {

    private String name;
    private String company_id;
    private int max_radius_free;
    private String full_address;
    private String category_company_id;
    private String subcategory_company_id;
    private double latitude, longitude;
    private double rating;
    private int num_rating;
    private String model_type;
    private int delivery_type_value;
    private int is_delivery;
    private int is_local;
    private String delivery_max_time;
    private int is_online;
    private double delivery_fixed_fee;
    private double delivery_variable_fee;
    private int max_radius;
    private String category_name;
    private String subcategory_name;
    private double distance;
    private String photo;
    private String banner_photo;
    private String instagram;
    private String facebook;
    private String site;
    private String description;

    public String getBanner_photo() {
        return banner_photo;
    }

    public String getDescription() {
        return description;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getSite() {
        return site;
    }

    public String getFull_address() {
        return full_address;
    }

    public double getDistance() {
        return distance;
    }

    public void setphoto(String photo) {
        this.photo = photo;
    }

    public int getIs_online() {
        return is_online;
    }

    public String getModel_type() {
        return model_type;
    }

    public String getphoto() {
        return photo;
    }

    public Company() {
    }


    protected Company(Parcel in) {
        name = in.readString();
        company_id = in.readString();
        category_company_id = in.readString();
        subcategory_company_id = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        rating = in.readDouble();
        num_rating = in.readInt();
        model_type = in.readString();
        delivery_type_value = in.readInt();
        is_delivery = in.readInt();
        is_local = in.readInt();
        delivery_max_time = in.readString();
        is_online = in.readInt();
        max_radius_free = in.readInt();
        delivery_fixed_fee = in.readDouble();
        delivery_variable_fee = in.readDouble();
        max_radius = in.readInt();
        category_name = in.readString();
        subcategory_name = in.readString();
        distance = in.readDouble();
    }

    public static final Creator<Company> CREATOR = new Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    public String getId() {
        return company_id;
    }

    public void setId(String id) {
        this.company_id = id;
    }

    public String getNome() {
        return name;
    }

    public void setNome(String nome) {
        this.name = nome;
    }

    public double getRating() {
        return rating;
    }

    public int getNum_rating() {
        return num_rating;
    }

    public String getCategory_company_id() {
        return category_company_id;
    }

    public void setCategory_company_id(String category_company_id) {
        this.category_company_id = category_company_id;
    }

    public int getDelivery_type_value() {
        return delivery_type_value;
    }

    public int getIs_delivery() {
        return is_delivery;
    }

    public int getIs_local() {
        return is_local;
    }

    public String getDelivery_max_time() {
        return delivery_max_time;
    }

    public int getMax_radius_free() {
        return max_radius_free;
    }

    public double getDelivery_variable_fee() {
        return delivery_variable_fee;
    }

    public double getDelivery_fixed_fee() {
        return delivery_fixed_fee;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(company_id);
        dest.writeString(category_company_id);
        dest.writeString(subcategory_company_id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(num_rating);
        dest.writeDouble(rating);
        dest.writeInt(is_delivery);
        dest.writeInt(is_local);
        dest.writeString(delivery_max_time);
        dest.writeInt(is_online);
        dest.writeInt(max_radius_free);
        dest.writeDouble(delivery_fixed_fee);
        dest.writeDouble(delivery_variable_fee);
        dest.writeInt(max_radius);
        dest.writeString(category_name);
        dest.writeString(subcategory_name);
        dest.writeDouble(distance);
    }
}

