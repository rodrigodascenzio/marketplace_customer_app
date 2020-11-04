package com.nuppin.connection;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nuppin.BuildConfig;
import com.nuppin.R;
import com.nuppin.User.login.SplashScreen;
import com.nuppin.Util.AppConstants;
import com.nuppin.model.CartInfo;
import com.nuppin.model.CartProduct;
import com.nuppin.model.Feedback;
import com.nuppin.model.Scheduling;
import com.nuppin.model.Cart;
import com.nuppin.model.CartBuyTypeAdapter;
import com.nuppin.model.CartCompany;
import com.nuppin.model.Chat;
import com.nuppin.model.EmailCode;
import com.nuppin.model.Address;
import com.nuppin.model.Order;
import com.nuppin.model.SmsCode;
import com.nuppin.model.Suggestion;
import com.nuppin.model.User;
import com.nuppin.Util.UtilShaPre;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectApi {

    //Servidor AWS:
    public static final String API_VERSION = "v4";
    public static final String SERVIDOR = "https://api.nuppin.com/"+API_VERSION;

    public static final String HORARIOS = SERVIDOR + "/service/scheduling";
    public static final String AGENDAMENTO = SERVIDOR + "/scheduling/detail";
    public static final String AGENDAR = SERVIDOR + "/scheduling";
    public static final String ATUALIZA_STATUS_AGENDAMENTO = ConnectApi.SERVIDOR + "/scheduling/user/status";


    public static final String CUPOM_USERS = SERVIDOR + "/users/coupon";
    public static final String CUPOM_USERS_FROM_CART = SERVIDOR + "/users/coupon/cart";


    public static final String SEND_CODE_TO_EMAIL = ConnectApi.SERVIDOR + "/sendemail";
    public static final String SEND_CODE_TO_SMS = ConnectApi.SERVIDOR + "/sendsms";
    public static final String VERIFY_CODE_FROM_EMAIL = ConnectApi.SERVIDOR + "/verifycodeemail";
    public static final String VERIFY_CODE_FROM_CELLPHONE = ConnectApi.SERVIDOR + "/verifycodephonenumber";


    public static final String ATUALIZA_ORDER = ConnectApi.SERVIDOR + "/orders/user/status";
    public static final String PEDIDO_DETALHE = SERVIDOR + "/orders/user/detail";
    public static final String ORDERS = SERVIDOR + "/orders";
    public static final String ORDERS_USER = SERVIDOR + "/orders/user";
    public static final String ORDERS_CHAT = SERVIDOR + "/orders/chat";
    public static final String SEND_CHAT_TO_COMPANY = ORDERS_CHAT + "/all_company";


    public static final String USERS = SERVIDOR + "/users";


    public static final String STORES = SERVIDOR + "/company";
    public static final String STORES_RATING = SERVIDOR + "/company/rating";
    public static final String STORES_RATING_AGENDAMENTO = SERVIDOR + "/company/rating/scheduling";
    public static final String STORES_MEIOS_PAGAMENTO = SERVIDOR + "/company/payment/checked";
    public static final String STORES_POR_CATEGORIA = SERVIDOR + "/company/category";
    public static final String STORES_DESCRIPTION = SERVIDOR + "/company/description";

    public static final String PRODUCTS = SERVIDOR + "/product";
    public static final String PRODUCT_DETAIL = SERVIDOR + "/product/detail";

    public static final String SERVICES_STORE = SERVIDOR + "/service/user";

    public static final String CART = SERVIDOR + "/cart";
    public static final String CART_ITEM = SERVIDOR + "/cart/item";
    public static final String CART_INFO = SERVIDOR + "/cart/info";
    public static final String CART_INFO_COUPON = SERVIDOR + "/cart/info/coupon";
    public static final String CART_LIMPA_TUDO = SERVIDOR + "/cart/clear";
    public static final String CART_ATUALIZA_ITEM = SERVIDOR + "/cart/item";

    public static final String ADDRESS = SERVIDOR + "/address";
    public static final String ADDRESS_PATCH_IS_SELECTED = SERVIDOR + "/address/addressIsSelected";

    public static final String CADASTRO = SERVIDOR + "/signup";
    public static final String LEGAL = SERVIDOR + "/legal";
    public static final String FEEDBACK = ConnectApi.SERVIDOR + "/users/feedback";
    public static final String SUGGESTION = ConnectApi.SERVIDOR + "/users/suggestion";
    public static final String USERS_REFRESH_TOKEN = ConnectApi.SERVIDOR + "/users/refreshtoken/newaccesstoken";
    public static final String USER_TOKEN = ConnectApi.SERVIDOR + "/users/registerTokenNotification";
    public static final String USERS_LOGOUT = ConnectApi.SERVIDOR + "/users/logout";
    public static final String USER_TEMP_TOKEN = ConnectApi.SERVIDOR + "/tempNotification";
    public static final String VERIFY_CODE_TO_CHANGE_EMAIL = ConnectApi.SERVIDOR + "/users/changeemail/verifycode";
    public static final String VERIFY_CODE_TO_CHANGE_CELLPHONE = ConnectApi.SERVIDOR + "/users/changephonenumber/verifycode";


    public static String BEARER(User user, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(user);
            return SENDJSON("POST",json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String BEARER(EmailCode emailCode, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(emailCode);
            return SENDJSON("POST",json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String BEARER(SmsCode smsCode, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(smsCode);
            return SENDJSON("POST",json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(EmailCode emailCode, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(emailCode);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(SmsCode smsCode, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(smsCode);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String PATCH(SmsCode smsCode, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(smsCode);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String PATCH(EmailCode emailCode, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(emailCode);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }


    public static String PATCH(User user, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(user);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }


    public static String PATCH(CartInfo info, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(info);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(Feedback feedback, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(feedback);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(Suggestion suggestion, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(suggestion);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String DELETE(User user, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(user);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String DELETE(Cart cart, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(cart);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(CartProduct cart, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(cart);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(Address address, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(address);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(CartCompany cartCompany, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(CartCompany.class, new CartBuyTypeAdapter(ctx));
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(cartCompany);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Cart cart, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(cart);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Address address, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(address);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }


    public static String POST(Chat chat, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(chat);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Scheduling scheduling, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(scheduling);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Cart cart, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(cart);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Address address, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(address);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Order order, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(order);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }


    public static String PATCH(Scheduling scheduling, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(scheduling);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    //=========================================================================================


    public static int REQUESTNEWTOKEN(String route, Context ctx) {
        OkHttpClient client = new OkHttpClient();
        final String BEARER = UtilShaPre.getDefaultsString("refresh_token", ctx);

        RequestBody body = new FormBody.Builder()
                .add("api_version", API_VERSION)
                .add("aplication_version", BuildConfig.VERSION_NAME)
                .add("device_type", !ctx.getResources().getBoolean(R.bool.isTablet) ? "Smartphone" : "Tablet")
                .add("source", "nuppin_company")
                .add(AppConstants.USER_ID, String.valueOf(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx)))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(route)
                .post(body)
                .header("Authorization", "Bearer " + BEARER)
                .build();


        try (Response response = client.newCall(request).execute()) {
            String token = response.header("token");
            if (token != null && !token.isEmpty()) {
                UtilShaPre.setDefaults("auth_token", token, ctx);
            }
            int code = response.code();

            return code;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            return 500;
        }
    }

    private static String SENDJSON(String metodoHttp, String json, String route, Context ctx) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request;
        final String BEARER = UtilShaPre.getDefaultsString("auth_token", ctx);

        Log.d("rota", route);
        Log.d("api_post", json);

        if (metodoHttp.equals("POST")) {
            RequestBody body = RequestBody.create(JSON, json);
            request = new okhttp3.Request.Builder()
                    .url(route)
                    .header("Authorization", "Bearer " + BEARER)
                    .post(body)
                    .build();
        } else if (metodoHttp.equals("PATCH")) {
            RequestBody body = RequestBody.create(JSON, json);
            request = new okhttp3.Request.Builder()
                    .url(route)
                    .header("Authorization", "Bearer " + BEARER)
                    .patch(body)
                    .build();
        } else {
            RequestBody body = RequestBody.create(JSON, json);
            request = new okhttp3.Request.Builder()
                    .url(route)
                    .header("Authorization", "Bearer " + BEARER)
                    .delete(body)
                    .build();
        }

        try (Response response = client.newCall(request).execute()) {

            String token = response.header("token");
            if (token != null && !token.isEmpty()) {
                UtilShaPre.setDefaults("auth_token", token, ctx);
            }

            String s = response.body().string();
            int code = response.code();
            Log.d("api_post_response", s);

            if (code == 401) {
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    if (REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx) != 500) {
                        UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                        return SENDJSON(metodoHttp, json, route, ctx);
                    } else {
                        return "false";
                    }
                } else {
                    Toast.makeText(ctx, "Deslogado", Toast.LENGTH_SHORT).show();
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    loggout(ctx);
                    return s;
                }
            } else if (code == 403) {
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                    REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx);
                    return SENDJSON(metodoHttp, json, route, ctx);
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    return s;
                }
            } else if (code == 500) {
                UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    return SENDJSON(metodoHttp, json, route, ctx);
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    return s;
                }
            } else {
                UtilShaPre.setDefaults("counter", 0, ctx);
                return s;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    public static String GET(String route, Context ctx) {
        final OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(route)
                .header("Authorization", "Bearer " + UtilShaPre.getDefaultsString("auth_token", ctx))
                .build();

        try {
            try (Response response = client.newCall(request).execute()) {
                String s = response.body().string();
                int code = response.code();

                Log.d("rota", route);
                Log.d("api_get", s);

                if (code == 401) {
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        if (REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx) != 500) {
                            UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                            return GET(route, ctx);
                        } else {
                            return "false";
                        }
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        loggout(ctx);
                        return s;
                    }
                } else if (code == 403) {
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                        REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx);
                        return GET(route, ctx);
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        return s;
                    }
                } else if (code == 500) {
                    UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        return GET(route, ctx);
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        return s;
                    }
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    return s;
                }
            }
        } catch (
                Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }

    }

    public static void loggout(Context ctx) {
        UtilShaPre.setDefaults("auth_token", "", ctx);
        UtilShaPre.setDefaults("refresh_token", "", ctx);
        UtilShaPre.setDefaults("user_logged", false, ctx);
        Intent intent = new Intent(ctx, SplashScreen.class);
        ctx.startActivity(intent);
    }

    //=========================================================================================

    //=========================== UPLOAD IMAGE =======================================

    public static String enviarFoto(Context ctx, String path, String id, String route, byte[] imageCompressed) {
        OkHttpClient client = new OkHttpClient();
        final String urlDoServidor = SERVIDOR + "/upload/" + route + "," + id;

        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", path,
                        RequestBody.create(MEDIA_TYPE_PNG, imageCompressed))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("Authorization", "Bearer " + UtilShaPre.getDefaultsString("auth_token", ctx))
                .url(urlDoServidor)
                .post(requestBody)
                .build();

        //Check the response
        try {
            try (Response response = client.newCall(request).execute()) {
                String s = response.body().string();
                int code = response.code();

                if (code == 401) {
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        if (REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx) != 500) {
                            UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                            return enviarFoto(ctx, path, id, route, imageCompressed);
                        } else {
                            return "false";
                        }
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        loggout(ctx);
                        return s;
                    }
                } else if (code == 403) {
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                        REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx);
                        return enviarFoto(ctx, path, id, route, imageCompressed);
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        return s;
                    }
                } else if (code == 500) {
                    UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        return enviarFoto(ctx, path, id, route, imageCompressed);
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        return s;
                    }
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    return s;
                }
            }
        } catch (
                Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
}


    public static void strongerRequest(String route, String token, Context ctx) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add(AppConstants.USER_ID, String.valueOf(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx)))
                .build();

        Request request = new Request.Builder()
                .url(route)
                .addHeader("Authorization", "Bearer " + UtilShaPre.getDefaultsString("auth_token", ctx))
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            int code = response.code();

            if (code == 401) {
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    if (REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx) != 500) {
                        UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                        strongerRequest(route, token, ctx);
                    }
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    ConnectApi.loggout(ctx);
                }
            } else if (code == 403) {
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                    ConnectApi.REQUESTNEWTOKEN(ConnectApi.USERS_REFRESH_TOKEN, ctx);
                    strongerRequest(route, token, ctx);
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                }
            } else if (code == 500) {
                UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    ConnectApi.REQUESTNEWTOKEN(ConnectApi.USERS_REFRESH_TOKEN, ctx);
                    strongerRequest(route, token, ctx);
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                }
            } else {
                UtilShaPre.setDefaults("counter", 0, ctx);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

}
