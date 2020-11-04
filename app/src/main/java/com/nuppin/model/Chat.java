package com.nuppin.model;

public class Chat {

    private String order_id;
    private String chat_from;
    private String chat_to;
    private String chat_id;
    private String message;
    private String created_date;
    private String seen_date;
    private String user_id;
    private String company_id;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getCompany_id() {
        return company_id;
    }



    public String getSeen_date() {
        return seen_date;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getChat_from() {
        return chat_from;
    }

    public String getChat_to() {
        return chat_to;
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getMessage() {
        return message;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public void setChat_from(String chat_from) {
        this.chat_from = chat_from;
    }

    public void setChat_to(String chat_to) {
        this.chat_to = chat_to;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
