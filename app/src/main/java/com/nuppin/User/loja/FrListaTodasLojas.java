package com.nuppin.User.loja;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.model.Address;
import com.nuppin.model.Cart;
import com.nuppin.model.Company;
import com.nuppin.model.CompanyCategory;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FrListaTodasLojas extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        RvLojaAdapter.RvLojaOnClickListener,
        SubCategoriaAdapter.SubCategoriaOnClickListener,
        CategoriaAdapter.CategoriaOnClickListener {

    private LoaderManager loaderManager;
    private RvLojaAdapter adapter;
    private CategoriaAdapter adapterCat;
    private SubCategoriaAdapter adapterSub;
    private Toolbar toolbar;
    private Cart carrinho;
    private Address address;
    private List<CompanyCategory[]> category;
    private List<CompanyCategory[]> subcategory;
    private List<Company> companies;
    private String categoria = "0"; // PADRAO - TODOS
    private Map<String, Object> data;
    private LottieAnimationView dots;
    private NestedScrollView linearEmptyNone;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private RecyclerView mRecyclerView, mRecyclerViewSub, mRecyclerViewCat;
    private MaterialButton feedback, share, indicate;
    private TextView txt, txtSub;
    private SwipeRefreshLayout swipe;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
        categoria = UtilShaPre.getDefaultsCategoria(AppConstants.CATEGORY, getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_list_company, container, false);
        dots = view.findViewById(R.id.dots);
        linearEmptyNone = view.findViewById(R.id.nestedNone);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mRecyclerViewCat.setVisibility(View.GONE);
                mRecyclerViewSub.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrListaTodasLojas.this);
            }
        });

        toolbar = view.findViewById(R.id.tb_main_top);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
        }

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = getActivity();
                if (activity instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) activity;
                    listener.endereco(0);
                }
            }
        });

        txt = view.findViewById(R.id.txt);
        txtSub = view.findViewById(R.id.txtSub);
        feedback = view.findViewById(R.id.feedback);
        share = view.findViewById(R.id.share);
        indicate = view.findViewById(R.id.indicate);
        swipe = view.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Load data to your RecyclerView
                loaderManager.restartLoader(0, null, FrListaTodasLojas.this);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareCompat.IntentBuilder.from(requireActivity())
                        .setType("text/plain")
                        .setChooserTitle("Compartilhar no..")
                        .setText(getString(R.string.share_nuppin_empresa))
                        .startChooser();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.notReady(R.id.feedback);
                }
            }
        });

        indicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.notReady(R.id.indicate);
                }
            }
        });


        LinearLayoutManager layoutManagerH = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mRecyclerViewCat = view.findViewById(R.id.recyclerview_company_category);
        mRecyclerViewCat.setLayoutManager(layoutManagerH);
        adapterCat = new CategoriaAdapter(this);
        mRecyclerViewCat.setAdapter(adapterCat);

        LinearLayoutManager layoutManagerHSub = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        mRecyclerViewSub = view.findViewById(R.id.recyclerview_company_subcategory);
        mRecyclerViewSub.setLayoutManager(layoutManagerHSub);
        adapterSub = new SubCategoriaAdapter(this);
        mRecyclerViewSub.setAdapter(adapterSub);
        mRecyclerView = view.findViewById(R.id.recyclerview_company);

        if (getResources().getBoolean(R.bool.isTablet)) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        }

        adapter = new RvLojaAdapter(this);
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
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        return new StoresLoader(getActivity(), categoria, data);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        swipe.setRefreshing(false);
        initialize(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {
    }

    private void initialize(Map<String, Object> data) {
        this.data = data;
        if (data != null) {
            if (data.get(AppConstants.ADDRESS) instanceof Address) {
                address = (Address) data.get(AppConstants.ADDRESS);
            }
            if (address == null) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.endereco(1);
                }
            } else {
                if (data.get(AppConstants.COMPANY) instanceof List) {
                    companies = (List) data.get(AppConstants.COMPANY);
                    if (companies.size() > 0) {
                        mRecyclerViewCat.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        linearEmptyNone.setVisibility(View.GONE);
                    } else {
                        if (categoria.equals("0")) {
                            mRecyclerView.setVisibility(View.GONE);
                            mRecyclerViewCat.setVisibility(View.GONE);
                            linearEmptyNone.setVisibility(View.VISIBLE);
                            txt.setText(getString(R.string.none_company_text));
                            txtSub.setText(getString(R.string.none_company_text_sub));
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            linearEmptyNone.setVisibility(View.VISIBLE);
                            txt.setText(getString(R.string.none_company_category_text));
                            txtSub.setText(getString(R.string.none_company_category_text_sub));
                        }
                    }
                }

                if (data.get("category") instanceof List) {
                    category = (List) data.get("category");
                }
                if (data.get("subcategory") instanceof List) {
                    if (((List) data.get("subcategory")).size() > 0) {
                        mRecyclerViewSub.setVisibility(View.VISIBLE);
                        subcategory = (List) data.get("subcategory");
                    } else {
                        mRecyclerViewSub.setVisibility(View.GONE);
                    }
                }

                if (data.get(AppConstants.CART) instanceof Cart) {
                    carrinho = (Cart) data.get(AppConstants.CART);
                    if (carrinho.getTemcarrinho() > 0) {
                        UtilShaPre.setDefaults(AppConstants.CART_COMPANY_ID, carrinho.getCartStoId(), getContext());
                    } else {
                        UtilShaPre.setDefaults(AppConstants.CART_COMPANY_ID, "", getContext());
                    }
                }
                setaCarrinho(carrinho);
                toolbar.setTitle(address.getStreet() + ", " + address.getStreet_number());
                adapterCat.setSCategorias(category);
                adapterSub.setSCategorias(subcategory);
                adapter.setCompanies(companies);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
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

    @Override
    public void onClickFromCategorias(String id) {
        if (!id.equals(categoria)) {
            categoria = id;
            dots.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            linearEmptyNone.setVisibility(View.GONE);
            data = null;
            mRecyclerViewSub.setVisibility(View.GONE);
            loaderManager.restartLoader(0, null, this);
        }
    }

    private void setaCarrinho(Cart carrinho) {
        Activity activity = getActivity();
        if (activity instanceof FrListaTodasLojas.FrtoActivity) {
            FrListaTodasLojas.FrtoActivity listener = (FrListaTodasLojas.FrtoActivity) activity;
            listener.carrinho(carrinho, 0);
        }
    }

    @Override
    public void onClickFromSubCategorias(CompanyCategory category) {
        Activity activity = getActivity();
        if (activity instanceof SubCategoriaAdapter.SubCategoriaOnClickListener) {
            SubCategoriaAdapter.SubCategoriaOnClickListener listener = (SubCategoriaAdapter.SubCategoriaOnClickListener) activity;
            listener.onClickFromSubCategorias(category);
        }
    }

    private static class StoresLoader extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        String categoria;
        Map<String, Object> data;


        StoresLoader(Context context, String categoria, Map<String, Object> data) {
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
            Address address;
            List<Company> companies;
            List<CompanyCategory> categoryList, subcategoryList;
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonObject jObj;
            Company[] company;
            CompanyCategory[] category;
            CompanyCategory[] subcategory;
            companies = new ArrayList<>();
            categoryList = new ArrayList<>();
            subcategoryList = new ArrayList<>();

            try {
                String stringJson = ConnectApi.GET(ConnectApi.STORES + "/" + UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()) + "," + categoria, getContext());
                jObj = parser.parse(stringJson).getAsJsonObject();
                company = gson.fromJson(jObj.getAsJsonArray(AppConstants.COMPANY), Company[].class);
                carrinho = gson.fromJson(jObj.getAsJsonObject(AppConstants.CART), Cart.class);
                category = gson.fromJson(jObj.getAsJsonArray("category"), CompanyCategory[].class);
                subcategory = gson.fromJson(jObj.getAsJsonArray("subcategory"), CompanyCategory[].class);
                subcategoryList.addAll(Arrays.asList(subcategory));


                try {
                    address = gson.fromJson(jObj.getAsJsonObject(AppConstants.ADDRESS), Address.class);
                    UtilShaPre.setDefaults(AppConstants.COUNTRY_CODE, address.getCountry_code(), getContext());
                    UtilShaPre.setDefaults(AppConstants.LATITUDE, String.valueOf(address.getLatitude()), getContext());
                    UtilShaPre.setDefaults(AppConstants.LONGITUDE, String.valueOf(address.getLongitude()), getContext());
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    address = null;
                }

                companies.addAll(Arrays.asList(company));
                categoryList.addAll(Arrays.asList(category));

                Map<String, Object> mapOrdPro;
                mapOrdPro = new HashMap<>();
                mapOrdPro.put(AppConstants.COMPANY, companies);
                mapOrdPro.put("cart", carrinho);
                mapOrdPro.put(AppConstants.ADDRESS, address);
                mapOrdPro.put("category", categoryList);
                mapOrdPro.put("subcategory", subcategoryList);
                return mapOrdPro;

            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface FrtoActivity {
        void carrinho(Cart carrinho, int index);

        void endereco(int index);

        void notReady(int index);
    }
}