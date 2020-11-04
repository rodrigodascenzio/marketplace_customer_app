package com.nuppin.User.produto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.model.Cart;
import com.nuppin.model.Company;
import com.nuppin.model.Product;
import com.nuppin.User.produto.Alimentos.RvAlimentosAdapter;
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

public class FrListaProdutos extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        RvProdutosAdapter.RvProdutosOnClickListener,
        RvAlimentosAdapter.RvProdutosOnClickListener {
    private LoaderManager loaderManager;
    private RvAlimentosAdapter alimentosAdapter;
    private RvProdutosAdapter produtosAdapter;
    private RecyclerView mRecyclerView;
    private SimpleDraweeView banner;
    private Toolbar toolbar;
    private Company company;
    private static final String STORE = "STORE";
    private static final String STORE_FROM_CART = "STORE_FROM_CART";
    private Map<String, Object> data;
    private LottieAnimationView dots;
    private NestedScrollView linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private ImageView site, face, insta;
    private TextView rating, description;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;


    public static FrListaProdutos newInstance(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(STORE, company);
        FrListaProdutos fragment = new FrListaProdutos();
        fragment.setArguments(parametros);
        return fragment;
    }

    public static FrListaProdutos newInstanceFromCart(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(STORE_FROM_CART, company);
        FrListaProdutos fragment = new FrListaProdutos();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(STORE_FROM_CART)) {
            company = getArguments().getParcelable(STORE_FROM_CART);
        }
        if (getArguments() != null && getArguments().containsKey(STORE)) {
            company = getArguments().getParcelable(STORE);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_list_product, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.nested);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrListaProdutos.this);
            }
        });

        toolbar = view.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this,0,toolbar,getActivity(),false,0);


        site = view.findViewById(R.id.site);
        face = view.findViewById(R.id.face);
        insta = view.findViewById(R.id.insta);
        description = view.findViewById(R.id.description);
        rating = view.findViewById(R.id.rating);

        if (company.getNum_rating() == 0) {
            rating.setText(R.string.novo);
            rating.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            rating.setText(getResources().getString(R.string.rating_and_num_rating, Util.formaterRating(company.getRating() / company.getNum_rating()), company.getNum_rating()));
            rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_black_12dp, 0, 0, 0);
        }

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrListaProdutos.FrtoActivity) {
                    FrListaProdutos.FrtoActivity listener = (FrListaProdutos.FrtoActivity) getActivity();
                    listener.description(company);
                }
            }
        });

        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www."+company.getSite()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.facebook.com/"+ company.getFacebook()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.instagram.com/"+ company.getInstagram()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        mRecyclerView = view.findViewById(R.id.recyclerview_products);

        banner = view.findViewById(R.id.banner);
        Util.hasPhoto(company,banner);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(company.getNome());
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsingToolbarLayoutTitleColor);

        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        alimentosAdapter = new RvAlimentosAdapter(this);

        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        produtosAdapter = new RvProdutosAdapter(this);


        if (company != null) {
            if (company.getCategory_company_id().equals("1")) {
                if (getResources().getBoolean(R.bool.isTablet)) {
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                }else {
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                }
                mRecyclerView.setAdapter(alimentosAdapter);
            } else {
                if (getResources().getBoolean(R.bool.isTablet)) {
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                }else {
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                }
                mRecyclerView.setAdapter(produtosAdapter);
            }
        }

        mRecyclerView.setHasFixedSize(true);

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
            return new ProductsLoader(getActivity(), company.getId(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        face.setVisibility(View.GONE);
        insta.setVisibility(View.GONE);
        site.setVisibility(View.GONE);

        this.data = data;
        if (data != null) {
            if (data.get(AppConstants.PRODUCT) instanceof List) {
                if (((List)data.get(AppConstants.PRODUCT)).size()>0){

                    if (company.getSite() != null && !company.getSite().equals("")) {
                        site.setVisibility(View.VISIBLE);
                    }
                    if (company.getFacebook() != null && !company.getFacebook().equals("")) {
                        face.setVisibility(View.VISIBLE);
                    }
                    if (company.getInstagram() != null && !company.getInstagram().equals("")) {
                        insta.setVisibility(View.VISIBLE);
                    }

                    if (company.getCategory_company_id().equals("1")) {
                        alimentosAdapter.setProducts((List) data.get(AppConstants.PRODUCT));
                    } else {
                        produtosAdapter.setProducts((List) data.get(AppConstants.PRODUCT));
                    }

                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearEmpty.setVisibility(View.GONE);
                }else {
                    mRecyclerView.setVisibility(View.GONE);
                    linearEmpty.setVisibility(View.VISIBLE);
                }
            }
            if (data.get(AppConstants.CART) instanceof Cart) {
                setaCarrinho((Cart) data.get(AppConstants.CART));
            }
            errorLayout.setVisibility(View.GONE);
        }else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {

    }

    private void setaCarrinho(Cart carrinho) {
        Activity activity = getActivity();
        if (activity instanceof FrListaProdutos.FrtoActivity) {
            FrListaProdutos.FrtoActivity listener = (FrListaProdutos.FrtoActivity) activity;
            listener.carrinho(carrinho, 1);
        }
    }

    @Override
    public void onClick(Product product) {
        Activity activity = getActivity();
        if (activity instanceof FrtoActivity) {
            FrtoActivity listener = (FrtoActivity) activity;
            listener.selecionado(product, company.getCategory_company_id());
        }
    }

    private static class ProductsLoader extends AsyncTaskLoader<Map<String, Object>> {

        String idStore;
        Map<String, Object> data;

        ProductsLoader(Context context, String idStore, Map<String, Object> data) {
            super(context);
            this.idStore = idStore;
            this.data = data;
        }

        @Override
        protected void onStartLoading() {
            if ( data != null) {
                deliverResult(data);
            }
            forceLoad();
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Cart carrinho;
            List<Product> p;
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            JsonParser parser = new JsonParser();

            try {
                String stringJson = ConnectApi.GET(ConnectApi.PRODUCTS + "/" + idStore +"/"+ UtilShaPre.getDefaultsString(AppConstants.USER_ID,getContext()),getContext());
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Product[] products = gson.fromJson(jObj.getAsJsonArray(AppConstants.PRODUCT), Product[].class);
                carrinho = gson.fromJson(jObj.getAsJsonObject(AppConstants.CART), Cart.class);
                p = new ArrayList<>(Arrays.asList(products));
                mapOrdPro.put(AppConstants.PRODUCT, p);
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
        void carrinho(Cart carrinho, int index);
        void selecionado(Product product, String catId);
        void description(Company company);
    }
}
