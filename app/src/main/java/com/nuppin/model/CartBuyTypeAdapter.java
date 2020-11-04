package com.nuppin.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.UtilShaPre;

import java.io.IOException;

public class CartBuyTypeAdapter extends TypeAdapter<CartCompany> {

    Context ctx;

    public CartBuyTypeAdapter(Context context) {
        ctx = context;
    }

    @Override
    public void write(JsonWriter out, CartCompany value) throws IOException {
                out.beginObject();
                out.name("user_id").value(UtilShaPre.getDefaultsString(AppConstants.USER_ID,ctx));
                out.name("note").value(value.getCart_note());
        out.endObject();
    }

        @Override
    public CartCompany read(JsonReader in) throws IOException {
        return null;
    }
}