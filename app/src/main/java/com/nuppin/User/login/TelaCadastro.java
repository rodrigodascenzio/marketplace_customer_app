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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.Firebase.updateUserToken;
import com.nuppin.Util.AppConstants;
import com.nuppin.model.User;
import com.nuppin.R;
import com.nuppin.User.activity.TelaListaTodasLojas;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;

public class TelaCadastro extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private EditText nome;
    private TextInputLayout tNome;
    private User user;
    private LoaderManager loaderManager;
    private CardView progress;
    private MaterialButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = new User();

        if (getIntent().getStringExtra("CELULAR") != null) {
            user.setPhone_number(getIntent().getStringExtra("CELULAR"));
        }

        if (getIntent().getStringExtra("EMAIL") != null) {
            user.setEmail(getIntent().getStringExtra("EMAIL"));
        }

        TextView termos = findViewById(R.id.termos);
        termos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TelaCadastro.this, TermosDeUso.class);
                startActivity(it);
            }
        });

        nome = findViewById(R.id.editNome);
        tNome = findViewById(R.id.editNomeT);

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
        if (nome.getText().toString().isEmpty() || nome.getText().toString().length() < 3) {
            tNome.setErrorEnabled(true);
            isGo = false;
            if (nome.getText().toString().isEmpty()) {
                tNome.setError(getResources().getString(R.string.error_enabled_text));
            } else {
                tNome.setError(getResources().getString(R.string.invalid_name));
            }
        } else {
            tNome.setErrorEnabled(false);
        }


        return isGo;
    }

    private void criarPerfil() {
        user.setNome(nome.getText().toString());
        loaderManager = LoaderManager.getInstance(this);
        loaderManager.restartLoader(0, null, this);
    }


    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new UserLoader(this, user);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            UtilShaPre.setDefaults(AppConstants.USER_ID, ((User) data).getId(), this);
            UtilShaPre.setDefaults(AppConstants.USER_NAME, ((User) data).getNome(), this);
            UtilShaPre.setDefaults("refresh_token", ((User) data).getRefresh_token(), this);
            UtilShaPre.setDefaults("user_logged", true, this);

            startService(new Intent(this, updateUserToken.class));

            Intent it;
            it = new Intent(TelaCadastro.this, TelaListaTodasLojas.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(it);

        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class UserLoader extends AsyncTaskLoader<Object> {
        User user;
        Context ctx;

        UserLoader(Context context, User user__) {
            super(context);
            user = user__;
            ctx = context;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String json = ConnectApi.BEARER(user, ConnectApi.CADASTRO, getContext());
            JsonParser parser = new JsonParser();
            try {
                return gson.fromJson(parser.parse(json).getAsJsonObject(), User.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
