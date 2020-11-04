package com.nuppin.User.loja;

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
import androidx.cardview.widget.CardView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.R;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.model.Company;
import com.nuppin.model.CompanyPayment;
import com.nuppin.model.CompanySchedule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrCompanyDescription extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>> {
    private LoaderManager loaderManager;
    private Company company;
    private static final String STORE = "STORE";
    private LottieAnimationView dots;
    private NestedScrollView linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private SimpleDraweeView fotoloja;
    private TextView nomeLoja, rating, descricao, address, emptySchedule, opcao1, opcao2;
    private RecyclerView recyclerViewSchedule, recyclerViewPayment;
    private ImageView site, insta, face;
    private AdapterPaymentDescription adapterPayment;
    private CardView cardAddress;
    private AdapterScheduleDescription adapterSchedule;


    public static FrCompanyDescription newInstance(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(STORE, company);
        FrCompanyDescription fragment = new FrCompanyDescription();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(STORE)) {
            company = getArguments().getParcelable(STORE);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_company_description, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.nested);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrCompanyDescription.this);
            }
        });


        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.descricao, toolbar, getActivity(), false, 0);



        fotoloja = view.findViewById(R.id.fotoLoja);
        nomeLoja = view.findViewById(R.id.nomeLoja);
        rating = view.findViewById(R.id.rating);
        descricao = view.findViewById(R.id.descricao);
        address = view.findViewById(R.id.address);
        recyclerViewSchedule = view.findViewById(R.id.recyclerview_schedule);
        recyclerViewPayment = view.findViewById(R.id.recyclerview_payment);
        site = view.findViewById(R.id.site);
        insta = view.findViewById(R.id.insta);
        face = view.findViewById(R.id.face);
        cardAddress = view.findViewById(R.id.cardEndereco);
        emptySchedule = view.findViewById(R.id.emptySchedule);
        opcao1 = view.findViewById(R.id.opcao1);
        opcao2 = view.findViewById(R.id.opcao2);


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


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerViewPayment.setLayoutManager(linearLayoutManager);
        adapterPayment = new AdapterPaymentDescription();
        recyclerViewPayment.setAdapter(adapterPayment);

        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(), 1);
        recyclerViewSchedule.setLayoutManager(gridLayoutManager2);
        adapterSchedule = new AdapterScheduleDescription();
        recyclerViewSchedule.setAdapter(adapterSchedule);
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
        return new DescriptionLoader(getActivity(), company.getId());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (data.get(AppConstants.COMPANY) instanceof Company) {
                Company company = (Company) data.get(AppConstants.COMPANY);
                Util.hasPhoto(company,fotoloja);
                nomeLoja.setText(company.getNome());


                if (company.getDescription() != null && !company.getDescription().equals("")) {
                    descricao.setText(company.getDescription());
                }else {
                    descricao.setText(getResources().getString(R.string.estabelecimento_sem_descricao));
                }


                if (company.getIs_local() == 1){
                    address.setText(company.getFull_address());
                } else {
                    cardAddress.setVisibility(View.GONE);
                }


                if (company.getIs_delivery() == 1) {
                    opcao1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.racing_helmet_12do, 0, 0, 0);
                    if (company.getCategory_company_id().equals("3")) {
                        opcao1.setText(getString(R.string.a_domicilio));
                    } else {
                        opcao1.setText(getString(R.string.delivery));
                    }
                    if (company.getIs_local() == 1) {
                        opcao2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stores_12px, 0, 0, 0);
                        if (company.getCategory_company_id().equals("3")) {
                            opcao2.setText(getString(R.string.no_local));
                        } else {
                            opcao2.setText(getString(R.string.retirada));
                        }
                    } else {
                        opcao2.setVisibility(View.GONE);
                    }
                }else {
                    opcao1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stores_12px, 0, 0, 0);
                    opcao2.setVisibility(View.GONE);
                    if (company.getIs_local() == 1) {
                        if (company.getCategory_company_id().equals("3")) {
                            opcao1.setText(getString(R.string.no_local));
                        } else {
                            opcao1.setText(getString(R.string.retirada));
                        }
                    }
                }


                if (company.getNum_rating() == 0) {
                    rating.setText(R.string.novo);
                    rating.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    rating.setText(getResources().getString(R.string.rating_and_num_rating, Util.formaterRating(company.getRating() / company.getNum_rating()), company.getNum_rating()));
                    rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_black_12dp, 0, 0, 0);
                }

                if (company.getSite() != null && !company.getSite().equals("")) {
                    site.setVisibility(View.VISIBLE);
                }

                if (company.getFacebook() != null && !company.getFacebook().equals("")) {
                    face.setVisibility(View.VISIBLE);
                }

                if (company.getInstagram() != null && !company.getInstagram().equals("")) {
                    insta.setVisibility(View.VISIBLE);
                }


            }if (data.get("schedule") instanceof List) {
                if (((List) data.get("schedule")).size() > 0) {
                    recyclerViewSchedule.setVisibility(View.VISIBLE);
                    emptySchedule.setVisibility(View.GONE);
                    adapterSchedule.setHorarios((List) data.get("schedule"));
                }else {
                    recyclerViewSchedule.setVisibility(View.GONE);
                    emptySchedule.setVisibility(View.VISIBLE);
                    emptySchedule.setText(getResources().getString(R.string.estabelecimento_sem_horario));
                }
            }
            if (data.get("payment") instanceof List) {
                adapterPayment.setMeiosPagamento((List) data.get("payment"));
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {

    }

    private static class DescriptionLoader extends AsyncTaskLoader<Map<String, Object>> {

        String idStore;

        DescriptionLoader(Context context, String idStore) {
            super(context);
            this.idStore = idStore;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Company company;
            List<CompanySchedule> schedules;
            List<CompanyPayment> payments;
            JsonParser parser = new JsonParser();
            JsonObject jObj;
            CompanySchedule[] schedule;
            CompanyPayment[] payment;
            schedules = new ArrayList<>();
            payments = new ArrayList<>();

            try {
                String stringJson = ConnectApi.GET(ConnectApi.STORES_DESCRIPTION + "/" + idStore, getContext());
                jObj = parser.parse(stringJson).getAsJsonObject();
                company = gson.fromJson(jObj.getAsJsonObject(AppConstants.COMPANY), Company.class);
                schedule = gson.fromJson(jObj.getAsJsonArray("schedule"), CompanySchedule[].class);
                payment = gson.fromJson(jObj.getAsJsonArray("payment"), CompanyPayment[].class);

                schedules.addAll(Arrays.asList(schedule));
                payments.addAll(Arrays.asList(payment));

                Map<String, Object> mapOrdPro;
                mapOrdPro = new HashMap<>();
                mapOrdPro.put(AppConstants.COMPANY, company);
                mapOrdPro.put("schedule", schedules);
                mapOrdPro.put("payment", payments);
                return mapOrdPro;

            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
