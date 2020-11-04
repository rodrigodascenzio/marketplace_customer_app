package com.nuppin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String full_name;
    private String email;
    private String company_id;
    private String user_id = "";
    private String photo;
    private String document_number;
    private String phone_number;
    private String refresh_token;

    public User() {
    }

    public User(String nome, String email) {
       this.full_name = nome;
        this.email = email;
    }

    protected User(Parcel in) {
        full_name = in.readString();
        email = in.readString();
        user_id = in.readString();
        company_id = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDocument_number() {
        return document_number;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String user_telefone) {
        this.phone_number = user_telefone;
    }

    public String getphoto() {
        return photo;
    }


    public String getId() {
        return user_id;
    }

    public String getNome() {return full_name;}

    public void setNome(String nome) {
        this.full_name = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(full_name);
        dest.writeString(email);
        dest.writeString(user_id);
        dest.writeString(company_id);
    }
}
