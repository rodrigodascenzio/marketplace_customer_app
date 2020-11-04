package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CompanyCategory implements Parcelable {

    private String category_name;
    private String subcategory_name;
    private String category_company_id;
    private String subcategory_company_id;

    private CompanyCategory(Parcel in) {
        category_name = in.readString();
        subcategory_name = in.readString();
        category_company_id = in.readString();
        subcategory_company_id = in.readString();
    }

    public String getCategory_company_id() {
        return category_company_id;
    }

    public String getSubcategory_company_id() {
        return subcategory_company_id;
    }

    public static final Creator<CompanyCategory> CREATOR = new Creator<CompanyCategory>() {
        @Override
        public CompanyCategory createFromParcel(Parcel in) {
            return new CompanyCategory(in);
        }

        @Override
        public CompanyCategory[] newArray(int size) {
            return new CompanyCategory[size];
        }
    };

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(category_name);
        parcel.writeString(subcategory_name);
        parcel.writeString(category_company_id);
        parcel.writeString(subcategory_company_id);
    }
}
