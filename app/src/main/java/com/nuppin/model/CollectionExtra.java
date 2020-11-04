package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CollectionExtra implements Parcelable {

    private String collection_id = "";
    private String company_id;
    private String name;
    private String description;
    private int min_quantity;
    private int max_quantity;
    private int quantity_selected;
    private List<Extra> extra;
    private double total_price;

    public CollectionExtra(){

    }

    protected CollectionExtra(Parcel in) {
        collection_id = in.readString();
        company_id = in.readString();
        name = in.readString();
        description = in.readString();
        max_quantity = in.readInt();
        min_quantity = in.readInt();
        min_quantity = in.readInt();
        quantity_selected = in.readInt();
        total_price = in.readDouble();
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public int getQuantity_selected() {
        return quantity_selected;
    }

    public void setQuantity_selected(int quantity_selected) {
        this.quantity_selected = quantity_selected;
    }

    public static final Creator<CollectionExtra> CREATOR = new Creator<CollectionExtra>() {
        @Override
        public CollectionExtra createFromParcel(Parcel in) {
            return new CollectionExtra(in);
        }

        @Override
        public CollectionExtra[] newArray(int size) {
            return new CollectionExtra[size];
        }
    };

    public List<Extra> getConjuct_extras() {
        return extra;
    }

    public int getMin_quantity() {
        return min_quantity;
    }

    public int getMax_quantity() {
        return max_quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCollection_id() {
        return collection_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(collection_id);
        parcel.writeString(company_id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeInt(max_quantity);
        parcel.writeInt(min_quantity);
        parcel.writeInt(quantity_selected);
        parcel.writeDouble(total_price);
    }
}
