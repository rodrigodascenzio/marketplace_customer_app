package com.nuppin.User.perfil;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.R;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.model.Suggestion;

public class FrSuggestion extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

    private LoaderManager loaderManager;
    private EditText detail, name, whats, insta, face;
    private TextInputLayout nameWrap;
    private CardView progress;
    private MaterialButton btn;
    private Suggestion suggestion;


    public FrSuggestion() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_suggestion, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);


        name = view.findViewById(R.id.name);
        nameWrap = view.findViewById(R.id.nameWrap);
        detail = view.findViewById(R.id.detail);
        btn = view.findViewById(R.id.botao);
        progress = view.findViewById(R.id.progress);
        face = view.findViewById(R.id.face);
        insta = view.findViewById(R.id.instagram);
        whats = view.findViewById(R.id.whatsapp);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    btn.setClickable(false);
                    suggestion = new Suggestion();
                    suggestion.setUser_id(UtilShaPre.getDefaultsString(AppConstants.USER_ID,getContext()));
                    suggestion.setCompany_name(name.getText().toString());
                    suggestion.setDetail(detail.getText().toString());
                    suggestion.setLatitude(UtilShaPre.getDefaultsString(AppConstants.LATITUDE,getContext()));
                    suggestion.setLongitude(UtilShaPre.getDefaultsString(AppConstants.LONGITUDE,getContext()));
                    suggestion.setInstagram(insta.getText().toString());
                    suggestion.setFacebook(face.getText().toString());
                    suggestion.setWhatsapp(whats.getText().toString());
                    progress.setVisibility(View.VISIBLE);
                        loaderManager.restartLoader(1, null, FrSuggestion.this);
                    }
            }
        });

        return view;
    }

    private boolean validar() {
        boolean b = true;
        if (name.getText().toString().isEmpty()) {
            nameWrap.setErrorEnabled(true);
            nameWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            nameWrap.setErrorEnabled(false);
        }
        return b;
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
            return new LoaderPP(getActivity(),suggestion);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof Boolean && (boolean) data) {
                Toast.makeText(getContext(), "Enviado com sucesso. Agradecemos!", Toast.LENGTH_SHORT).show();
                Util.backFragmentFunction(this);
            } else {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class LoaderPP extends AsyncTaskLoader<Object> {

        Suggestion suggestion;
        Context ctx;

        LoaderPP(Context context, Suggestion suggestion) {
            super(context);
            ctx = context;
            this.suggestion = suggestion;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
               return gson.fromJson(ConnectApi.POST(suggestion, ConnectApi.SUGGESTION, getContext()), Boolean.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }

        }
    }
}