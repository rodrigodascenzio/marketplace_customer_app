package com.nuppin.User.activity;

import android.app.Application;
import android.text.TextUtils;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.nuppin.Util.UtilShaPre;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClassBaseApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                    .newBuilder(getApplicationContext(), okHttpClient)
                    .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(this)
                            .setMaxCacheSize(100L * ByteConstants.MB)
                            .setMaxCacheSizeOnLowDiskSpace(10L * ByteConstants.MB)
                            .setMaxCacheSizeOnVeryLowDiskSpace(5L * ByteConstants.MB)
                            .build())
                    .build();
            Fresco.initialize(getApplicationContext(), config);
        }
}