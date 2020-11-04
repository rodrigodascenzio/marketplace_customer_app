package com.nuppin.User.address;

import android.content.Context;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.Util.AppConstants;
import com.nuppin.model.Address;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.List;


public class FrManualEndereco extends Fragment implements LoaderManager.LoaderCallbacks<Address> {
    private Address address;
    private static String ENDERECO = "ENDERECO";
    private EditText eCidade, eBairro, eNumero, eComplemento, eRua, eEstado;
    private TextInputLayout cidadeWrap, bairroWrap, ruaWrap, numeroWrap, estadoWrap;
    private Button btn;
    private LoaderManager loaderManager;
    private CardView progress;

    public FrManualEndereco() {
    }

    public static FrManualEndereco instance(Address address) {
        FrManualEndereco fragment = new FrManualEndereco();
        Bundle args = new Bundle();
        args.putParcelable(ENDERECO, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ENDERECO)) {
            address = getArguments().getParcelable(ENDERECO);
            if (address != null) {
                address.setIs_selected(1);
            }
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_address_register, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        eEstado = v.findViewById(R.id.edtEstado);
        eCidade = v.findViewById(R.id.edtCidade);
        eBairro = v.findViewById(R.id.edtBairro);
        eNumero = v.findViewById(R.id.edtNumero);
        eRua = v.findViewById(R.id.edtRua);
        estadoWrap = v.findViewById(R.id.estadoWrap);
        cidadeWrap = v.findViewById(R.id.cidadeWrap);
        bairroWrap = v.findViewById(R.id.bairroWrap);
        numeroWrap = v.findViewById(R.id.numeroWrap);
        ruaWrap = v.findViewById(R.id.ruaWrap);
        eComplemento = v.findViewById(R.id.edtComplemento);
        progress = v.findViewById(R.id.progress);
        btn = v.findViewById(R.id.btnCadastrar);

        if (address != null) {
            if ((address.getAddress_id() != null && !address.getAddress_id().equals(""))) {
                eNumero.setText(address.getStreet_number());
            }
            eEstado.setText(address.getState_code());
            eCidade.setText(address.getCity());
            eBairro.setText(address.getDistrict());
            eRua.setText(address.getStreet());
            eComplemento.setText(address.getComplement());
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    progress.setVisibility(View.VISIBLE);
                    String stringAddress;
                    if (eComplemento.getText().toString().isEmpty()) {
                        stringAddress = Util.joinAddress(eRua.getText().toString(), eNumero.getText().toString(), eBairro.getText().toString(), eCidade.getText().toString(), eEstado.getText().toString(), requireContext());
                    } else {
                        stringAddress = Util.joinAddress(eRua.getText().toString(), eNumero.getText().toString(), eBairro.getText().toString(), eCidade.getText().toString(), eEstado.getText().toString(), eComplemento.getText().toString(), requireContext());
                    }
                    btn.setClickable(false);
                    getLocationFromAddress(getContext(), stringAddress);
                }
            }
        });
        return v;
    }


    private boolean validar() {
        boolean b = true;
        if (eRua.getText().toString().trim().isEmpty()) {
            ruaWrap.setErrorEnabled(true);
            ruaWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            ruaWrap.setErrorEnabled(false);
        }
        if (eNumero.getText().toString().trim().isEmpty()) {
            numeroWrap.setErrorEnabled(true);
            numeroWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            numeroWrap.setErrorEnabled(false);
        }
        if (eBairro.getText().toString().trim().isEmpty()) {
            bairroWrap.setErrorEnabled(true);
            bairroWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            bairroWrap.setErrorEnabled(false);
        }
        if (eCidade.getText().toString().trim().isEmpty()) {
            cidadeWrap.setErrorEnabled(true);
            cidadeWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            cidadeWrap.setErrorEnabled(false);
        }
        if (eEstado.getText().toString().trim().isEmpty()) {
            estadoWrap.setErrorEnabled(true);
            estadoWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            estadoWrap.setErrorEnabled(false);
        }
        return b;
    }

    private void getLocationFromAddress(Context context, String endereco) {

        Geocoder coder = new Geocoder(context);
        List<android.location.Address> addressGet;

        try {
            addressGet = coder.getFromLocationName(endereco, 1);
            if (addressGet == null) {
                if (Util.retryAddressError(context)) {
                    getLocationFromAddress(context, endereco);
                } else {
                    btn.setClickable(true);
                    progress.setVisibility(View.GONE);
                    Toast.makeText(context, R.string.endereco_nao_encontrado, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            android.location.Address location = addressGet.get(0);

            Util.cleanRetryAddress(context);

            address = Util.splitAddressLocation(endereco);
            address.setCountry_code(location.getCountryCode());
            address.setLatitude(location.getLatitude());
            address.setLongitude(location.getLongitude());
            address.setFull_address(endereco);
            address.setUser_id(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
            address.setIs_selected(1);
            address.setComplement(eComplemento.getText().toString());

            loaderManager.restartLoader(0, null, FrManualEndereco.this);

        } catch (IndexOutOfBoundsException e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
            FirebaseCrashlytics.getInstance().recordException(e);
            if (Util.retryAddressError(context)) {
                getLocationFromAddress(context, endereco);
            } else {
                btn.setClickable(true);
                progress.setVisibility(View.GONE);
                Toast.makeText(context, R.string.endereco_nao_encontrado, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
            FirebaseCrashlytics.getInstance().recordException(e);
            if (Util.retryAddressError(context)) {
                getLocationFromAddress(context, endereco);
            } else {
                btn.setClickable(true);
                progress.setVisibility(View.GONE);
                Toast.makeText(context, R.string.erro_layout_sub, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Address> onCreateLoader(int id, @Nullable Bundle args) {
        return new EnderecoLoader(getActivity(), address);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Address> loader, Address data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (getActivity() instanceof ToActivity) {
                ToActivity listener = (ToActivity) getActivity();
                listener.FrManualEndereco();
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        loaderManager.destroyLoader(loader.getId());
        btn.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Address> loader) {

    }

    private static class EnderecoLoader extends AsyncTaskLoader<Address> {

        Context ctx;
        Address address;

        EnderecoLoader(Context context, Address address) {
            super(context);
            ctx = context;
            this.address = address;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Address loadInBackground() {
            Gson gson = new Gson();
            try {
                if (!address.getAddress_id().isEmpty()) {
                    return gson.fromJson(ConnectApi.PATCH(address, ConnectApi.ADDRESS, getContext()), Address.class);
                }
                return gson.fromJson(ConnectApi.POST(address, ConnectApi.ADDRESS, getContext()), Address.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void FrManualEndereco();
    }
}
