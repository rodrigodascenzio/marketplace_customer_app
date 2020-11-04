package com.nuppin.User.pedido;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.model.Scheduling;
import com.nuppin.model.Order;
import com.nuppin.User.service.scheduling.RvAgendamentosAdapter;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FrPedidos extends Fragment implements LoaderManager.LoaderCallbacks,
        RvOrdersAdapter.RvOrdenOnClickListener,
        RvAgendamentosAdapter.RvAgendamentosOnClickListener {

    private LoaderManager loaderManager;
    private RvOrdersAdapter adapter;
    private RvAgendamentosAdapter adapterAgendamentos;
    private RecyclerView mRecyclerView;
    private static final String ID_USER = "id_user";
    private static final String POSITION = "position";
    private String idUser;
    private String position;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private List data;

    public FrPedidos() {
    }

    public static FrPedidos newInstance(String idUser, int position) {
        FrPedidos fragment = new FrPedidos();
        Bundle args = new Bundle();
        args.putString(ID_USER, idUser);
        args.putString(POSITION, String.valueOf(position));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ID_USER)) {
            idUser = getArguments().getString(ID_USER);
            position = getArguments().getString(POSITION);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fr_pedidos, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrPedidos.this);
            }
        });

        mRecyclerView = view.findViewById(R.id.recyclerview_order);
        if (getResources().getBoolean(R.bool.isTablet)) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        }
        if (position.equals("3")) {
            adapterAgendamentos = new RvAgendamentosAdapter(this);
            mRecyclerView.setAdapter(adapterAgendamentos);
        } else {
            adapter = new RvOrdersAdapter(this);
            mRecyclerView.setAdapter(adapter);
        }
        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        UtilShaPre.setDefaultsCategoriaOrders("order_categoria", (Integer.parseInt(position) - 1), getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new StoresLoader(getActivity(), idUser, position, data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof List) {
                this.data = (List) data;
                if (((List) data).size() > 0) {
                    linearEmpty.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (position.equals("3")) {
                        adapterAgendamentos.setOrder((List) data);
                    } else {
                        adapter.setOrder((List) data);
                    }
                } else {
                    linearEmpty.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }

    @Override
    public void onClick(Order order) {
        Activity activity = getActivity();
        if (activity instanceof RvOrdersAdapter.RvOrdenOnClickListener) {
            RvOrdersAdapter.RvOrdenOnClickListener listener = (RvOrdersAdapter.RvOrdenOnClickListener) activity;
            listener.onClick(order);
        }
    }

    @Override
    public void onClickAvaliar(Order order) {
        Activity activity = getActivity();
        if (activity instanceof RvOrdersAdapter.RvOrdenOnClickListener) {
            RvOrdersAdapter.RvOrdenOnClickListener listener = (RvOrdersAdapter.RvOrdenOnClickListener) activity;
            listener.onClickAvaliar(order);
        }
    }

    @Override
    public void onClick(Scheduling scheduling) {
        Activity activity = getActivity();
        if (activity instanceof RvAgendamentosAdapter.RvAgendamentosOnClickListener) {
            RvAgendamentosAdapter.RvAgendamentosOnClickListener listener = (RvAgendamentosAdapter.RvAgendamentosOnClickListener) activity;
            listener.onClick(scheduling);
        }
    }

    @Override
    public void onClickAvaliar(Scheduling scheduling) {
        Activity activity = getActivity();
        if (activity instanceof RvAgendamentosAdapter.RvAgendamentosOnClickListener) {
            RvAgendamentosAdapter.RvAgendamentosOnClickListener listener = (RvAgendamentosAdapter.RvAgendamentosOnClickListener) activity;
            listener.onClickAvaliar(scheduling);
        }
    }


    private static class StoresLoader extends AsyncTaskLoader<List> {

        Context ctx;
        String idUser;
        String position;
        List data;

        StoresLoader(Context context, String idUser, String position, List data) {
            super(context);
            ctx = context;
            this.idUser = idUser;
            this.position = position;
            this.data = data;
        }


        @Override
        protected void onStartLoading() {
            if (data != null) {
                deliverResult(data);
            }
            forceLoad();
        }

        @Override
        public List loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();

            if (position.equals("3")) {
                try {
                    String sJson = ConnectApi.GET(ConnectApi.ORDERS_USER + "/" + idUser + ",nuppin," + position + "," +
                                    Util.returnStringLatLonCountr(getContext())
                            , getContext());
                    JsonObject jObj = parser.parse(sJson).getAsJsonObject();
                    JsonArray jArray = jObj.getAsJsonArray(AppConstants.ORDERS);
                    Scheduling[] ord;
                    List ordList;
                    ord = gson.fromJson(jArray, Scheduling[].class);
                    ordList = new ArrayList<>(Arrays.asList(ord));
                    return ordList;
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    return null;
                }
            } else {
                try {
                    String sJson = ConnectApi.GET(ConnectApi.ORDERS_USER + "/" + idUser + ",nuppin," + position + "," +
                                    Util.returnStringLatLonCountr(getContext())
                            , getContext());
                    JsonObject jObj = parser.parse(sJson).getAsJsonObject();
                    JsonArray jArray = jObj.getAsJsonArray(AppConstants.ORDERS);
                    Order[] ord;
                    List ordList;
                    ord = gson.fromJson(jArray, Order[].class);
                    ordList = new ArrayList<>(Arrays.asList(ord));
                    return ordList;
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    return null;
                }
            }
        }
    }
}