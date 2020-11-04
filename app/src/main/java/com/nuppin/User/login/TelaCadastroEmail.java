package com.nuppin.User.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.model.EmailCode;
import com.nuppin.R;
import com.nuppin.connection.ConnectApi;


public class TelaCadastroEmail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private EditText email;
    private TextInputLayout tEmail;
    private EmailCode emailCode;
    private LoaderManager loaderManager;
    private CardView progress;
    private MaterialButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailCode = new EmailCode();

        email = findViewById(R.id.editEmail);
        tEmail = findViewById(R.id.editEmailT);
        progress = findViewById(R.id.progress);
        btn = findViewById(R.id.criarConta);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    btn.setClickable(false);
                    progress.setVisibility(View.VISIBLE);
                    criarPerfil();
                }
            }
        });
    }

    public boolean validar() {
        boolean isGo = true;
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            tEmail.setErrorEnabled(true);
            tEmail.setError(getResources().getString(R.string.invalid_email));
            isGo = false;
        } else {
            tEmail.setErrorEnabled(false);
        }
        return isGo;
    }

    private void criarPerfil() {
        emailCode.setTemp_email_id(email.getText().toString());
        loaderManager = LoaderManager.getInstance(this);
        loaderManager.restartLoader(0, null, this);
    }


    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new UserLoader(this, emailCode);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof Boolean && ((Boolean)data)) {
                Toast.makeText(this, R.string.code_sent, Toast.LENGTH_SHORT).show();
                Intent it = new Intent(this, TelaCadastroEmailVerifyCode.class);
                it.putExtra("EMAIL", emailCode.getTemp_email_id());
                startActivity(it);
            } else {
                Toast.makeText(this, R.string.error_send_code, Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class UserLoader extends AsyncTaskLoader<Object> {
        EmailCode emailCode;
        Context ctx;

        UserLoader(Context context, EmailCode emailCode) {
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
            try {
                String json = ConnectApi.POST(emailCode, ConnectApi.SEND_CODE_TO_EMAIL, getContext());
                JsonParser parser = new JsonParser();
                return gson.fromJson(parser.parse(json).getAsJsonPrimitive(), Boolean.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (loaderManager != null) {
            loaderManager.destroyLoader(0);
        }
    }

}
