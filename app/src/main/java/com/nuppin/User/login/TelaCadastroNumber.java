package com.nuppin.User.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.Firebase.updateUserToken;
import com.nuppin.Util.AppConstants;
import com.nuppin.model.SmsCode;
import com.nuppin.model.User;
import com.nuppin.R;
import com.nuppin.User.activity.TelaListaTodasLojas;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;

public class TelaCadastroNumber extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private EditText numero, codigo, ccode;
    private CardView progress1, progress2;
    private MaterialButton btn, btnVerify;
    private LoaderManager loaderManager;
    private ViewFlipper viewFlipper;
    private SmsCode smsCode;
    private TextView celularVerifyHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        smsCode = new SmsCode();
        loaderManager = LoaderManager.getInstance(this);

        viewFlipper = findViewById(R.id.viewFlipper);

        viewFlipper.setDisplayedChild(0);

        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);

        numero = findViewById(R.id.edtCelular);
        codigo = findViewById(R.id.codigo);
        ccode = findViewById(R.id.ccode);
        btn = findViewById(R.id.btnSendSMS);
        btnVerify = findViewById(R.id.btnVerify);
        TextView reenviar = findViewById(R.id.reenviar);
        celularVerifyHint = findViewById(R.id.txtSubCelular);

        reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Util.validarTelefone(numero.getText().toString())) {
                    Toast.makeText(TelaCadastroNumber.this, R.string.invalid_numero, Toast.LENGTH_SHORT).show();
                    return;
                }
                btn.setClickable(false);
                progress1.setVisibility(View.VISIBLE);
                smsCode.setTemp_sms_id(ccode.getText().toString() + numero.getText().toString());
                loaderManager.restartLoader(0, null, TelaCadastroNumber.this);
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codigo.getText().toString().equals("")) {
                    Toast.makeText(TelaCadastroNumber.this, R.string.invalid_code, Toast.LENGTH_SHORT).show();
                    return;
                }
                btnVerify.setClickable(false);
                progress2.setVisibility(View.VISIBLE);
                smsCode.setCode_sent(codigo.getText().toString());
                loaderManager.restartLoader(1, null, TelaCadastroNumber.this);
            }
        });
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new SendSms(this, smsCode);
        } else {
            return new VerifyCode(this, smsCode);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress2.setVisibility(View.GONE);
        progress1.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 0) {
                if (data instanceof Boolean && ((Boolean) data)) {
                    Toast.makeText(this, R.string.code_sent, Toast.LENGTH_SHORT).show();
                    viewFlipper.setDisplayedChild(1);
                    celularVerifyHint.setText(smsCode.getTemp_sms_id());
                } else {
                    Toast.makeText(this, R.string.error_send_code, Toast.LENGTH_SHORT).show();
                }
            } else if (loader.getId() == 1) {
                if (data instanceof User) {
                    UtilShaPre.setDefaults(AppConstants.USER_ID, ((User) data).getId(), this);
                    UtilShaPre.setDefaults(AppConstants.USER_NAME, ((User) data).getNome(), this);
                    UtilShaPre.setDefaults("refresh_token", ((User) data).getRefresh_token(), this);
                    UtilShaPre.setDefaults("user_logged", true, this);

                    startService(new Intent(this, updateUserToken.class));

                    Intent it;
                    it = new Intent(TelaCadastroNumber.this, TelaListaTodasLojas.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);

                }
                if (data instanceof Integer) {
                    switch ((int) data) {
                        case 1:
                            Intent it = new Intent(this, TelaCadastro.class);
                            it.putExtra("CELULAR", smsCode.getTemp_sms_id());
                            startActivity(it);
                            break;
                        case 2:
                            Toast.makeText(this, R.string.wrong_code, Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(this, R.string.exp_code, Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(this, R.string.phone_number_exist, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        btnVerify.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class VerifyCode extends AsyncTaskLoader<Object> {
        SmsCode smsCode;
        Context ctx;

        VerifyCode(Context context, SmsCode smsCode) {
            super(context);
            this.smsCode = smsCode;
            ctx = context;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String json = ConnectApi.BEARER(smsCode, ConnectApi.VERIFY_CODE_FROM_CELLPHONE, getContext());
            JsonParser parser = new JsonParser();
            try {
                return gson.fromJson(parser.parse(json).getAsJsonObject(), User.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    return gson.fromJson(parser.parse(json).getAsJsonPrimitive(), Integer.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
        }
    }

    private static class SendSms extends AsyncTaskLoader<Object> {
        SmsCode smsCode;
        Context ctx;

        SendSms(Context context, SmsCode smsCode) {
            super(context);
            this.smsCode = smsCode;
            ctx = context;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                String json = ConnectApi.POST(smsCode, ConnectApi.SEND_CODE_TO_SMS, getContext());
                JsonParser parser = new JsonParser();
                return gson.fromJson(parser.parse(json).getAsJsonPrimitive(), Boolean.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
