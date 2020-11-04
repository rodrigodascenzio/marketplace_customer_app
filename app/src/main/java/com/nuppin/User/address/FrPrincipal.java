package com.nuppin.User.address;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.model.Address;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.GpsUtils;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrPrincipal extends Fragment implements
        LoaderManager.LoaderCallbacks<Object>,
        RvEnderecos.RvEnderecosOnClickListener {


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;

    private LoaderManager loaderManager;
    private RvEnderecos adapter;
    private Address address, addressDelete;
    private RecyclerView mRecyclerView;
    private Button usarLoc, btnManual;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private CardView progress, cardContainer;

    public FrPrincipal() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_address, container, false);

        progress = view.findViewById(R.id.progress);
        cardContainer = view.findViewById(R.id.cardView);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                cardContainer.setVisibility(View.VISIBLE);
                loaderManager.restartLoader(0, null, FrPrincipal.this);
            }
        });

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    usarLoc.setClickable(true);
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), R.string.erro_local, Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        progress.setVisibility(View.GONE);
                        getAddressFromLocation(getContext(), location.getLatitude(), location.getLongitude());
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.location_error, Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.GONE);
                        usarLoc.setClickable(true);
                    }
                }
            }
        };


        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.endereco_toolbar, toolbar, getActivity(), false, 0);

        address = new Address();
        usarLoc = view.findViewById(R.id.btnUsarLoc);
        btnManual = view.findViewById(R.id.btnManual);

        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.addressManual();
                }
            }
        });

        usarLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usarLoc.setClickable(false);
                new GpsUtils(FrPrincipal.this, requireContext()).turnGPSOn(new GpsUtils.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        progress.setVisibility(View.VISIBLE);
                        isGPS = isGPSEnable;
                        getLocation();
                    }
                });
                if (!isGPS) {
                    Toast.makeText(getContext(), R.string.turn_gps_on, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRecyclerView = view.findViewById(R.id.recyclerview_address);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new RvEnderecos(this, getActivity());
        mRecyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == 0) {
            return new EnderecosLoader(getActivity());
        } else if (id == 1) {
            return new EnderecoPachLoader(getActivity(), address);
        } else {
            return new DeleteEndereco(getActivity(), addressDelete);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (data != null) {
            cardContainer.setVisibility(View.VISIBLE);
            switch (loader.getId()) {
                case 0:
                    if (((List) data).size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        linearEmpty.setVisibility(View.GONE);
                        adapter.setAddresses((List) data);
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        linearEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    Util.backFragmentFunction(this);
                    break;
                case 2:
                    dots.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(0, null, this);
                    loaderManager.destroyLoader(loader.getId());
                    address = null;
                    break;
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            cardContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    @Override
    public void onClick(Address address) {
        this.address = address;
        if (!(address == null)) {
            if (!(address.getStreet() == null)) {
                if (address.getIs_selected() == 0) {
                    progress.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(1, null, FrPrincipal.this);
                } else {
                    Util.backFragmentFunction(this);
                }
            }
        }
    }

    public void onClickEdit(Address address) {
        if (getActivity() instanceof ToActivity) {
            ToActivity listener = (ToActivity) getActivity();
            listener.FrPrincipal(address);
        }
    }

    public void onClickExclui(Address address) {
        if (address.getIs_selected() == 1) {
            Toast.makeText(getContext(), "Não pode excluir o endereço atual", Toast.LENGTH_SHORT).show();
            return;
        }
        progress.setVisibility(View.VISIBLE);
        addressDelete = address;
        loaderManager.restartLoader(2, null, this);
    }

    private static class EnderecosLoader extends AsyncTaskLoader<Object> {
        Context ctx;

        EnderecosLoader(Context context) {
            super(context);
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
                Address[] address = gson.fromJson(ConnectApi.GET(ConnectApi.ADDRESS + "/" + UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()), getContext()), Address[].class);
                return new ArrayList<>(Arrays.asList(address));
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class EnderecoPachLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        Address address;

        EnderecoPachLoader(Context context, Address address) {
            super(context);
            ctx = context;
            this.address = address;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                return gson.fromJson(ConnectApi.PATCH(address, ConnectApi.ADDRESS_PATCH_IS_SELECTED, getContext()), Address.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class DeleteEndereco extends AsyncTaskLoader<Object> {

        Address address;

        DeleteEndereco(Context context, Address address) {
            super(context);
            this.address = address;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.DELETE(address, ConnectApi.ADDRESS, getContext()), Address.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }


    private void getAddressFromLocation(Context context, double lat, double lon) {

        Geocoder coder = new Geocoder(context);
        List<android.location.Address> address;

        try {
            address = coder.getFromLocation(lat, lon, 1);
            if (address == null) {
                if (Util.retryAddressError(context)) {
                    getAddressFromLocation(context, lat, lon);
                } else {
                    usarLoc.setClickable(true);
                    progress.setVisibility(View.GONE);
                    Toast.makeText(context, R.string.endereco_nao_encontrado, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            android.location.Address location = address.get(0);
            this.address = new Address();

            try {
                this.address = Util.splitAddressLocation(location.getAddressLine(0));
                this.address.setCountry_code(location.getCountryCode());
                this.address.setLatitude(lat);
                this.address.setLongitude(lon);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                this.address.setCountry_code(location.getCountryCode());
                this.address.setFull_address(location.getAddressLine(0));
                this.address.setCity(location.getSubAdminArea());
                this.address.setDistrict(location.getSubLocality());
                this.address.setStreet(location.getThoroughfare());
                this.address.setLatitude(lat);
                this.address.setLongitude(lon);
            }

            onClickEdit(this.address);
            progress.setVisibility(View.GONE);
            usarLoc.setClickable(true);
            Util.cleanRetryAddress(context);

        } catch (Exception ex) {
            if (Util.retryAddressError(context)) {
                getAddressFromLocation(context, lat, lon);
            } else {
                progress.setVisibility(View.GONE);
                usarLoc.setClickable(true);
                Toast.makeText(getContext(), R.string.location_error, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        } else {
            progress.setVisibility(View.VISIBLE);
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If request is cancelled, the result arrays are empty.
        if (requestCode == AppConstants.LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                progress.setVisibility(View.GONE);
                usarLoc.setClickable(true);
                Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true;
                getLocation();
            }
        } else {
            if (requestCode == AppConstants.GPS_REQUEST) {
                progress.setVisibility(View.GONE);
                usarLoc.setClickable(true);
            }
        }
    }

    public interface ToActivity {
        void FrPrincipal(Address address);

        void addressManual();
    }

}
