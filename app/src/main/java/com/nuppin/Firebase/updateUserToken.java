package com.nuppin.Firebase;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class updateUserToken extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            sendRegistrationToServer();
                        }
                    });
                    stopSelf();
        return START_NOT_STICKY;
    }

    private void sendRegistrationToServer() {
        try {
            ConnectApi.strongerRequest(ConnectApi.USER_TOKEN, UtilShaPre.getDefaultsString("userToken", getApplicationContext()),getApplicationContext());
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getApplicationContext()));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
