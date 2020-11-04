package com.nuppin.model;

public class SmsCode {

    private String temp_sms_id;
    private String code_sent;
    private String user_id;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setTemp_sms_id(String temp_sms_id) {
        this.temp_sms_id = temp_sms_id;
    }

    public void setCode_sent(String code_sent) {
        this.code_sent = code_sent;
    }

    public String getTemp_sms_id(){
        return temp_sms_id;
    }
}
