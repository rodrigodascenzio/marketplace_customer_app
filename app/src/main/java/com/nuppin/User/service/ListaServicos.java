package com.nuppin.User.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nuppin.User.produto.FrListaProdutos;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.model.Company;
import com.nuppin.model.Service;
import com.nuppin.Util.Util;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaServicos extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Service>>,
        com.nuppin.User.service.RvServicosAdapter.RvServicosOnClickListener{

    private LoaderManager loaderManager;
    private com.nuppin.User.service.RvServicosAdapter serviceAdapter;
    private RecyclerView mRecyclerView;
    private SimpleDraweeView banner;
    private Company company;
    private Toolbar toolbar;
    private static final String EXTRA_STORE = "company";
    private LottieAnimationView dots;
    private NestedScrollView linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private List<Service> data;
    private TextView rating, description;

    public ListaServicos() {}


    public static ListaServicos novaInstancia2(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_STORE, company);
        ListaServicos fragment = new ListaServicos();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(EXTRA_STORE)) {
            company = getArguments().getParcelable(EXTRA_STORE);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_list_service, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.nested);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, ListaServicos.this);
            }
        });

        SimpleDraweeView site = view.findViewById(R.id.site);
        SimpleDraweeView face = view.findViewById(R.id.face);
        SimpleDraweeView insta = view.findViewById(R.id.insta);

        if (company.getSite() != null && !company.getSite().equals("")) {
            site.setVisibility(View.VISIBLE);
        }

        if (company.getFacebook() != null && !company.getFacebook().equals("")) {
            face.setVisibility(View.VISIBLE);
        }

        if (company.getInstagram() != null && !company.getInstagram().equals("")) {
            insta.setVisibility(View.VISIBLE);
        }

        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(company.getSite()); // missing 'http://' will cause crashed
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


        mRecyclerView =  view.findViewById(R.id.recyclerview_service);

        toolbar = view.findViewById(R.id.tb_main_top);
        banner = view.findViewById(R.id.banner);
        Util.hasPhoto(company,banner);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(company.getNome());
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsingToolbarLayoutTitleColor);


        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);

        if (getResources().getBoolean(R.bool.isTablet)) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        }
        serviceAdapter = new com.nuppin.User.service.RvServicosAdapter(this);
        mRecyclerView.setAdapter(serviceAdapter);
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
    public Loader<List<Service>> onCreateLoader(int id, Bundle args) {
        return new ServicesLoader(getActivity(), company.getId(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Service>> loader, List<Service> data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (data.size() > 0) {
                this.data = data;
                serviceAdapter.setServicos(data);
                linearEmpty.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }else {
                linearEmpty.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
            errorLayout.setVisibility(View.GONE);
        }else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Service>> loader) {

    }

    @Override
    public void onClick(Service service, Company companyNull) {
        Activity activity = getActivity();
        if (activity instanceof com.nuppin.User.service.RvServicosAdapter.RvServicosOnClickListener) {
            com.nuppin.User.service.RvServicosAdapter.RvServicosOnClickListener listener = (com.nuppin.User.service.RvServicosAdapter.RvServicosOnClickListener) activity;
            listener.onClick(service, company);
        }
    }

    private static class ServicesLoader extends AsyncTaskLoader<List<Service>> {

        String idStore;
        List<Service> data;

        ServicesLoader(Context context, String idStore, List<Service> data) {
            super(context);
            this.idStore = idStore;
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
        public List<Service> loadInBackground() {
            Gson gson = new Gson();
            String stringJson = ConnectApi.GET(ConnectApi.SERVICES_STORE + "/" + idStore, getContext());
            JsonParser parser = new JsonParser();
            try {
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Service[] service = gson.fromJson(jObj.getAsJsonArray("service"), Service[].class);
                return new ArrayList<>(Arrays.asList(service));
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
