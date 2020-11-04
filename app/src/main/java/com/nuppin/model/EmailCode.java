package com.nuppin.model;

public class EmailCode {

    private String temp_email_id;
    private String code_sent;
    private String user_id;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setTemp_email_id(String temp_email_id) {
        this.temp_email_id = temp_email_id;
    }

    public void setCode_sent(String code_sent) {
        this.code_sent = code_sent;
    }

    public String getTemp_email_id(){
        return temp_email_id;
    }
}