package com.nuppin.User.loja.porCategoria;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.model.Cart;
import com.nuppin.model.Company;
import com.nuppin.model.CompanyCategory;
import com.nuppin.User.loja.RvLojaAdapter;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrListaTodasLojasPorCategoria extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        RvLojaAdapter.RvLojaOnClickListener {

    private static final String EXTRA_SUBCATEGORIA = "subcategoria";
    private static final String EXTRA_SUBNOME = "subnome";
    private LoaderManager loaderManager;
    private RvLojaAdapter adapter;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private Cart carrinho;
    private List<Company> companies;
    private CompanyCategory categoria;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private String subNome;
    private Map<String, Object> data;

    public static FrListaTodasLojasPorCategoria newInstance(CompanyCategory sub, String subNome) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_SUBCATEGORIA, sub);
        parametros.putString(EXTRA_SUBNOME, subNome);
        FrListaTodasLojasPorCategoria fragment = new FrListaTodasLojasPorCategoria();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(EXTRA_SUBCATEGORIA)) {
            categoria = getArguments().getParcelable(EXTRA_SUBCATEGORIA);
            subNome = getArguments().getString(EXTRA_SUBNOME);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fr_companies_category, container, false);

        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrListaTodasLojasPorCategoria.this);
            }
        });


        toolbar = view.findViewById(R.id.toolbar_top);

        Util.setaToolbar(this,0,toolbar,getActivity(),false,0);
        toolbar.setTitle(subNome);

        mRecyclerView = view.findViewById(R.id.recyclerview_company);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new RvLojaAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setNestedScrollingEnabled(false);
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
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        return new StoresLoader(getActivity(), categoria, data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        vish(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {
    }

    private void vish(Map<String, Object> data) {
        if (data != null) {
            this.data = data;
            if (data.get(AppConstants.COMPANY) instanceof List) {
                companies = (List) data.get(AppConstants.COMPANY);
                    mRecyclerView.setVisibility(View.VISIBLE);
            }
            if (data.get(AppConstants.CART) instanceof Cart) {
                carrinho = (Cart) data.get(AppConstants.CART);
            }
            setaCarrinho(carrinho);
            adapter.setCompanies(companies);
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(Company company) {
        Activity activity = getActivity();
        if (activity instanceof RvLojaAdapter.RvLojaOnClickListener) {
            RvLojaAdapter.RvLojaOnClickListener listener = (RvLojaAdapter.RvLojaOnClickListener) activity;
            listener.onClick(company);
        }
    }

    private void setaCarrinho(Cart carrinho) {
        Activity activity = getActivity();
        if (activity instanceof FrtoActivity) {
            FrtoActivity listener = (FrtoActivity) activity;
            listener.carrinho(carrinho);
        }
    }

    private static class StoresLoader extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        CompanyCategory categoria;
        Map<String, Object> data;

        StoresLoader(Context context, CompanyCategory categoria, Map<String, Object> data) {
            super(context);
            ctx = context;
            this.categoria = categoria;
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
        public Map<String, Object> loadInBackground() {
            Cart carrinho;
            List<Company> companies;
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonObject jObj;
            Company[] company;
            companies = new ArrayList<>();

            try {
                String stringJson = ConnectApi.GET(ConnectApi.STORES_POR_CATEGORIA + "/" + UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()) + "," + categoria.getSubcategory_company_id() + "," + categoria.getCategory_company_id(), getContext());
                jObj = parser.parse(stringJson).getAsJsonObject();
                company = gson.fromJson(jObj.getAsJsonArray(AppConstants.COMPANY), Company[].class);
                carrinho = gson.fromJson(jObj.getAsJsonObject(AppConstants.CART), Cart.class);
                companies.addAll(Arrays.asList(company));
                Map<String, Object> mapOrdPro;
                mapOrdPro = new HashMap<>();
                mapOrdPro.put(AppConstants.COMPANY, companies);
                mapOrdPro.put("cart", carrinho);
                return mapOrdPro;
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface FrtoActivity {
        void carrinho(Cart carrinho);
    }
}