package com.nuppin.User.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nuppin.model.EmailCode;
import com.nuppin.model.User;
import com.nuppin.R;
import com.nuppin.User.activity.TelaListaTodasLojas;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;

public class TelaCadastroEmailVerifyCode extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private EditText code;
    private EmailCode emailCode;
    private LoaderManager loaderManager;
    private CardView progress;
    private MaterialButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        loaderManager = LoaderManager.getInstance(this);

        emailCode = new EmailCode();

        if (getIntent().getStringExtra("EMAIL") != null) {
            emailCode.setTemp_email_id(getIntent().getStringExtra("EMAIL"));
        }

        TextView reenviar = findViewById(R.id.reenviar);
        reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        code = findViewById(R.id.codigo);
        progress = findViewById(R.id.progress);
        btn = findViewById(R.id.criarConta);

        TextView email = findViewById(R.id.txtSubEmail);
        email.setText(getResources().getString(R.string.email_caption, emailCode.getTemp_email_id()));


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!code.getText().toString().equals("")) {
                    emailCode.setCode_sent(code.getText().toString());
                    btn.setClickable(false);
                    progress.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(0, null, TelaCadastroEmailVerifyCode.this);
                }
            }
        });
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new ConfirmCode(this, emailCode);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof User) {
                UtilShaPre.setDefaults(AppConstants.USER_ID, ((User) data).getId(), this);
                UtilShaPre.setDefaults(AppConstants.USER_NAME, ((User) data).getNome(), this);
                UtilShaPre.setDefaults("refresh_token", ((User) data).getRefresh_token(), this);
                UtilShaPre.setDefaults("user_logged", true, this);

                startService(new Intent(this, updateUserToken.class));

                Intent it;
                it = new Intent(TelaCadastroEmailVerifyCode.this, TelaListaTodasLojas.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);

            }
            if (data instanceof Integer) {
                switch ((int) data) {
                    case 1:
                        Intent it = new Intent(this, TelaCadastro.class);
                        it.putExtra("EMAIL", emailCode.getTemp_email_id());
                        startActivity(it);
                        break;
                    case 2:
                        Toast.makeText(this, R.string.wrong_code, Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(this, R.string.exp_code, Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(this, R.string.email_exist, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class ConfirmCode extends AsyncTaskLoader<Object> {
        EmailCode emailCode;
        Context ctx;

        ConfirmCode(Context context, EmailCode emailCode) {
            super(context);
            this.emailCode = emailCode;
            ctx = context;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String json = ConnectApi.BEARER(emailCode, ConnectApi.VERIFY_CODE_FROM_EMAIL, getContext());
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
}
