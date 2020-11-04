package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String product_id;
    private String company_id;
    private String name;
    private String description;
    private double price;
    private int stock_quantity;
    private int is_stock;
    private String photo;
    private int is_multi_stock;
    private int multi_stock_quantity;

    //when has this product in cart
    private int cart_quantity;
    private String cart_note;


    public Product() {
    }


    private Product(Parcel in) {
        product_id = in.readString();
        company_id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        stock_quantity = in.readInt();
        cart_quantity = in.readInt();
        is_stock = in.readInt();
        cart_note = in.readString();
        is_multi_stock = in.readInt();
        multi_stock_quantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getphoto() {
        return photo;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public int getCart_quantity() {
        return cart_quantity;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getIs_stock() {
        return is_stock;
    }

    public String getCart_note() {
        return cart_note;
    }

    public int getIs_multi_stock() {
        return is_multi_stock;
    }

    public int getMulti_stock_quantity() {
        return multi_stock_quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(company_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeInt(stock_quantity);
        dest.writeInt(cart_quantity);
        dest.writeInt(is_stock);
        dest.writeString(cart_note);
        dest.writeInt(is_multi_stock);
        dest.writeInt(multi_stock_quantity);
    }
}