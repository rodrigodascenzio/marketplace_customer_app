package com.nuppin.coupon;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.model.CartInfo;
import com.nuppin.model.ErrorCode;
import com.nuppin.model.Scheduling;
import com.nuppin.model.CartCompany;
import com.nuppin.model.Coupon;
import com.nuppin.model.Company;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaCupom extends Fragment implements
        LoaderManager.LoaderCallbacks<Object>,
        listaCuponsAdapter.CupomOnClickListener,
        listaCuponsInsideStoreAdapter.CupomOnClickListener {
    private CartInfo cartCompany;
    private static final String CARTSTORE = "CARTSTORE";
    private static final String AGENDAMENTO = "AGENDAMENTO";
    private static final String USER = "USER";
    private RecyclerView recyclerView;
    private listaCuponsInsideStoreAdapter adapterCart;
    private listaCuponsAdapter adapterUser;
    private LoaderManager loaderManager;
    private String userId;
    private String TAG;
    private Scheduling scheduling;
    private List<Coupon> data;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fab;


    public ListaCupom() {
    }

    public static ListaCupom newInstanceCart(CartInfo cartCompany) {
        ListaCupom fragment = new ListaCupom();
        Bundle args = new Bundle();
        args.putParcelable(CARTSTORE, cartCompany);
        fragment.setArguments(args);
        return fragment;
    }

    public static ListaCupom newInstanceScheduling(Scheduling scheduling) {
        ListaCupom fragment = new ListaCupom();
        Bundle args = new Bundle();
        args.putParcelable(AGENDAMENTO, scheduling);
        fragment.setArguments(args);
        return fragment;
    }

    public static ListaCupom newInstance(String userId) {
        ListaCupom fragment = new ListaCupom();
        Bundle args = new Bundle();
        args.putString(USER, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(CARTSTORE)) {
            cartCompany = getArguments().getParcelable(CARTSTORE);
            TAG = CARTSTORE;
        }
        if (getArguments() != null && getArguments().containsKey(USER)) {
            userId = getArguments().getString(USER);
            TAG = USER;
        }
        if (getArguments() != null && getArguments().containsKey(AGENDAMENTO)) {
            scheduling = getArguments().getParcelable(AGENDAMENTO);
            TAG = AGENDAMENTO;
        }

        loaderManager = LoaderManager.getInstance(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_cupons, container, false);

        Util.cancelNotifyOnOff(getContext(), 3);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                if (TAG.equals(CARTSTORE) || TAG.equals(AGENDAMENTO)) {
                    loaderManager.restartLoader(1, null, ListaCupom.this);
                } else if (TAG.equals(USER)) {
                    loaderManager.restartLoader(0, null, ListaCupom.this);
                }
            }
        });

        Toolbar toolbar;
        toolbar = view.findViewById(R.id.toolbar_top);
        if (TAG.equals(USER)) {
            Util.setaToolbar(this, R.string.coupon_toolbar, toolbar, getActivity(), false, 0);
        } else {
            Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);
        }

        recyclerView = view.findViewById(R.id.listaCupons);

        if (getResources().getBoolean(R.bool.isTablet)) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        }

        if (TAG.equals(CARTSTORE) || TAG.equals(AGENDAMENTO)) {
            adapterCart = new listaCuponsInsideStoreAdapter(this);
            recyclerView.setAdapter(adapterCart);
            loaderManager.restartLoader(1, null, ListaCupom.this);
        } else if (TAG.equals(USER)) {
            adapterUser = new listaCuponsAdapter(this);
            recyclerView.setAdapter(adapterUser);
            loaderManager.restartLoader(0, null, ListaCupom.this);
        }
        return view;
    }

    @Override
    public void coupon(Coupon coupon) {
        if (coupon != null) {
            if (coupon.getQuantity_used() >= coupon.getQuantity()) {
                Toast.makeText(getContext(), R.string.esgotado, Toast.LENGTH_SHORT).show();
                return;
            }
            switch (TAG) {
                case CARTSTORE:
                    cartCompany.setCoupon_id(coupon.getCoupon_id());
                    loaderManager.restartLoader(2, null, this);
                    break;
                case AGENDAMENTO:
                    if (scheduling.getTotal_amount() < coupon.getMin_purchase()) {
                        Toast.makeText(getContext(), getResources().getString(R.string.pedido_minimo_cupom_aviso, Util.formaterPrice(coupon.getMin_purchase())), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    scheduling.setCoupon_id(coupon.getCoupon_id());
                    scheduling.setTarget(coupon.getDiscount_type());
                    scheduling.setCoupon_value(coupon.getValue());
                    Util.backFragmentFunction(this);
                    break;
                case USER:
                    if (coupon.getCompany().getCategory_company_id().equals("3")) {
                        if (getActivity() instanceof FrtoActivity) {
                            FrtoActivity listener = (FrtoActivity) getActivity();
                            listener.ListaCuponsToServico(coupon.getCompany());
                        }
                    } else {
                        if (getActivity() instanceof FrtoActivity) {
                            FrtoActivity listener = (FrtoActivity) getActivity();
                            listener.ListaCuponsToStore(coupon.getCompany());
                        }
                    }
                    break;
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new CuponsLoader(getActivity(), userId, data);
        } else if (id == 1) {
            if (TAG.equals(CARTSTORE)) {
                return new CuponsLoaderCart(getActivity(), cartCompany.getUser_id(), cartCompany.getCompany_id());
            } else {
                return new CuponsLoaderCart(getActivity(), scheduling.getUser_id(), scheduling.getCompany_id());
            }
        } else {
            return new UpdateCoupon(requireContext(), cartCompany);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 0) {
                if (data instanceof List) {
                    this.data = (List) data;
                    if (((List)data).size() > 0) {
                        adapterUser.setCupons((List) data);
                        recyclerView.setVisibility(View.VISIBLE);
                        linearEmpty.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        linearEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }else if (loader.getId() == 1) {
                if (data instanceof List) {
                    adapterCart.setCupons((List) data);
                }
            } else if (loader.getId() == 2) {
                if (data instanceof Boolean && (Boolean) data) {
                    Toast.makeText(getContext(), "Atualizado", Toast.LENGTH_SHORT).show();
                    Util.backFragmentFunction(this);
                } else if (data instanceof ErrorCode) {
                    Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Erro ao atualizar. Tente novamente!", Toast.LENGTH_SHORT).show();
                }
                loaderManager.destroyLoader(loader.getId());
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class CuponsLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        String userId;
        List<Coupon> data;


        CuponsLoader(Context context, String userId, List<Coupon> data) {
            super(context);
            ctx = context;
            this.userId = userId;
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
        public List<Coupon> loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            try {
                String json = ConnectApi.GET(ConnectApi.CUPOM_USERS + "/" + userId + "," +
                                UtilShaPre.getDefaultsString(AppConstants.LATITUDE, getContext()) + "," +
                                UtilShaPre.getDefaultsString(AppConstants.LONGITUDE, getContext())
                        , getContext());
                JsonObject jObj = parser.parse(json).getAsJsonObject();
                Coupon[] cupons = gson.fromJson(jObj.getAsJsonArray(AppConstants.COUPON), Coupon[].class);
                return new ArrayList<>(Arrays.asList(cupons));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class CuponsLoaderCart extends AsyncTaskLoader<Object> {

        Context ctx;
        String userId, stoId;


        CuponsLoaderCart(Context context, String userId, String stoId) {
            super(context);
            ctx = context;
            this.userId = userId;
            this.stoId = stoId;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public List<Coupon> loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            try {
                String json = ConnectApi.GET(ConnectApi.CUPOM_USERS_FROM_CART + "/" + userId + "," + stoId, getContext());
                JsonObject jObj = parser.parse(json).getAsJsonObject();
                Coupon[] cupons = gson.fromJson(jObj.getAsJsonArray(AppConstants.COUPON), Coupon[].class);
                return new ArrayList<>(Arrays.asList(cupons));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class UpdateCoupon extends AsyncTaskLoader<Object> {

        CartInfo info;

        UpdateCoupon(Context context, CartInfo info) {
            super(context);
            this.info = info;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String json = ConnectApi.PATCH(info, ConnectApi.CART_INFO_COUPON, getContext());
            try {
                return gson.fromJson(json, Boolean.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    JsonParser parser = new JsonParser();
                    return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
        }
    }


    public interface FrtoActivity {
        void ListaCuponsToStore(Company company);

        void ListaCuponsToServico(Company company);
    }
}
