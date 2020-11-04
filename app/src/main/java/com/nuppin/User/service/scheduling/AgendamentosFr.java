package com.nuppin.User.service.scheduling;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.model.Coupon;
import com.nuppin.model.ErrorCode;
import com.nuppin.model.Scheduling;
import com.nuppin.model.Company;
import com.nuppin.model.Service;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgendamentosFr extends Fragment implements
        LoaderManager.LoaderCallbacks<Object> {

    private static final String HORARIOS = "HORARIOS";
    private static final String SERVICOS = "SERVICOS";
    private static final String STORE = "STORE";
    private LoaderManager loaderManager;
    private Scheduling scheduling;
    private Service service;
    private Company company;
    private ArrayList cupons;
    private CardView progress;
    private MaterialButton button;
    private TextView tSubtotal, tTotal, txtCupom, pagamentoEscolhido, desconto, descontotxt;
    private CardView cardCupom;
    private MaterialButton adicionarCupom, alterarMeioPagamento;
    private Object data;
    private LottieAnimationView dots;

    public AgendamentosFr() {
    }

    public static AgendamentosFr newInstance(Scheduling horario, Service service, Company company) {
        AgendamentosFr fragment = new AgendamentosFr();
        Bundle args = new Bundle();
        args.putParcelable(HORARIOS, horario);
        args.putParcelable(SERVICOS, service);
        args.putParcelable(STORE, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(HORARIOS)) {
            scheduling = getArguments().getParcelable(HORARIOS);
        }
        if (getArguments() != null && getArguments().containsKey(SERVICOS)) {
            service = getArguments().getParcelable(SERVICOS);
        }
        if (getArguments() != null && getArguments().containsKey(STORE)) {
            company = getArguments().getParcelable(STORE);
            if (company != null) {
                scheduling.setCompany_id(company.getId());
                scheduling.setUser_id(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getActivity()));
            }
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_scheduling, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.scheduling_toolbar, toolbar, getActivity(), false, 0);

        progress = view.findViewById(R.id.progress);
        button = view.findViewById(R.id.btnAvaliar);
        SimpleDraweeView foto = view.findViewById(R.id.fotoLoja);
        TextView dataAge = view.findViewById(R.id.dataAge);
        TextView profissional = view.findViewById(R.id.ageFuncionario);
        TextView servico = view.findViewById(R.id.servico);
        final TextView obs = view.findViewById(R.id.obs);
        TextView nome = view.findViewById(R.id.nomeLoja);
        TextView endereco = view.findViewById(R.id.txtEndereco);
        TextView preco = view.findViewById(R.id.preco);
        TextView horaInicio = view.findViewById(R.id.ageHoraInicio);
        TextView stoRating = view.findViewById(R.id.stoRating);
        cardCupom = view.findViewById(R.id.cardCupom);
        pagamentoEscolhido = view.findViewById(R.id.meioPagamentoEscolhido);
        desconto = view.findViewById(R.id.resulDesconto);
        descontotxt = view.findViewById(R.id.desconto);
        txtCupom = view.findViewById(R.id.txtCupom);
        tSubtotal = view.findViewById(R.id.resultSubtotal);
        tTotal = view.findViewById(R.id.resultTotal);
        alterarMeioPagamento = view.findViewById(R.id.alterar);
        adicionarCupom = view.findViewById(R.id.adicionar);
        dots = view.findViewById(R.id.dots);


        adicionarCupom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.cupons(scheduling);
                }
            }
        });

        alterarMeioPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.meioPagamento(scheduling);
                }
            }
        });

        tSubtotal.setText(Util.formaterPrice(service.getPrice()));


        nome.setText(company.getNome());
        if (company.getNum_rating() == 0) {
            stoRating.setText(R.string.novo);
            stoRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            stoRating.setText(getResources().getString(R.string.rating_and_num_rating, Util.formaterRating(company.getRating() / company.getNum_rating()), company.getNum_rating()));
            stoRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_black_12dp, 0, 0, 0);
        }

        endereco.setText(company.getFull_address());
        Util.hasPhoto(company, foto);
        dataAge.setText(Util.timestampFormatDayMonthYear(scheduling.getStart_date()));
        horaInicio.setText(getResources().getString(R.string.time_with_dots, scheduling.getStart_time(), scheduling.getEnd_time()));
        profissional.setText(scheduling.getEmployee_name());
        servico.setText(service.getName());
        preco.setText(Util.formaterPrice(service.getPrice()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (scheduling.getPayment_method() == null) {
                    Toast.makeText(getContext(), R.string.selecione_metodo_pagamento, Toast.LENGTH_SHORT).show();
                    return;
                }

                scheduling.setType("local");
                scheduling.setSource("nuppin");
                scheduling.setLatitude(company.getLatitude());
                scheduling.setLongitude(company.getLongitude());
                scheduling.setCompany_id(service.getCompany_id());
                scheduling.setService_id(service.getService_id());
                scheduling.setService_name(service.getName());
                if (!scheduling.getStart_time().contains(" ")) {
                    scheduling.setStart_time(scheduling.getStart_date() + " " + scheduling.getStart_time());
                    scheduling.setEnd_time(scheduling.getStart_date() + " " + scheduling.getEnd_time());
                }
                scheduling.setCompany_name(company.getNome());
                scheduling.setUser_name(UtilShaPre.getDefaultsString(AppConstants.USER_NAME, getActivity()));
                scheduling.setSubtotal_amount(service.getPrice());
                scheduling.setService_duration(service.getDuration());
                scheduling.setAddress(company.getFull_address());
                scheduling.setNote(obs.getText().toString());
                progress.setVisibility(View.VISIBLE);
                button.setClickable(false);
                loaderManager.restartLoader(1, null, AgendamentosFr.this);
            }
        });

        loaderManager.restartLoader(0, null, this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (scheduling.getPayment_method() != null && !scheduling.getPayment_method().equals("")) {
            pagamentoEscolhido.setText(scheduling.getPayment_method());
        }

        if (scheduling.getCoupon_id() != null && scheduling.getTarget() != null && scheduling.getCoupon_value() != 0) {
            if (!(scheduling.getCoupon_id().equals("") && scheduling.getCoupon_id().equals(""))) {
                desconto.setVisibility(View.VISIBLE);
                descontotxt.setVisibility(View.VISIBLE);

                adicionarCupom.setText(R.string.btnAlterar);
                if (scheduling.getTarget().equals("1")) {
                    txtCupom.setText(getResources().getString(R.string.money_off, Util.formaterPrice(scheduling.getCoupon_value())));
                    scheduling.setDiscount_amount(scheduling.getCoupon_value());
                    desconto.setText(Util.formaterPrice(scheduling.getDiscount_amount()));
                    if (service.getPrice() - scheduling.getDiscount_amount() < 0) {
                        tTotal.setText(Util.formaterPrice(0));
                        scheduling.setTotal_amount(0);
                    } else {
                        double total = service.getPrice() - scheduling.getDiscount_amount();
                        tTotal.setText(Util.formaterPrice(total));
                        scheduling.setTotal_amount(total);
                    }
                } else {
                    txtCupom.setText(getResources().getString(R.string.percent_off, (int) scheduling.getCoupon_value()));
                    scheduling.setDiscount_amount((scheduling.getCoupon_value() / 100) * (service.getPrice()));
                    desconto.setText(Util.formaterPrice(scheduling.getDiscount_amount()));
                    if (service.getPrice() - scheduling.getDiscount_amount() < 0) {
                        tTotal.setText(Util.formaterPrice(0));
                        scheduling.setTotal_amount(0);
                    } else {
                        double total = service.getPrice() - scheduling.getDiscount_amount();
                        tTotal.setText(Util.formaterPrice(total));
                        scheduling.setTotal_amount(total);
                    }
                }
            }
        } else {
            desconto.setVisibility(View.GONE);
            descontotxt.setVisibility(View.GONE);

            adicionarCupom.setText(R.string.btnAdicionar);
            double total = service.getPrice();
            tTotal.setText(Util.formaterPrice(total));
            scheduling.setTotal_amount(total);
        }

    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new CuponsLoaderCart(getActivity(), scheduling.getUser_id(), scheduling.getCompany_id(), data);
        } else {
            return new AgendarLoader(getActivity(), scheduling);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        dots.setVisibility(View.GONE);

        if (data != null) {
            if (loader.getId() == 0) {
                this.data = data;
                if (data instanceof List) {
                    cupons = ((ArrayList) data);
                    if (cupons.size() > 0) {
                        cardCupom.setVisibility(View.VISIBLE);
                        if (scheduling.getCoupon_id() == null) {
                            if (cupons.size() < 2) {
                                txtCupom.setText(getResources().getString(R.string.qtd_coupon_disponivel, cupons.size()));
                            } else {
                                txtCupom.setText(getResources().getString(R.string.qtd_cupons_disponiveis, cupons.size()));
                            }
                        }
                    }
                }
            } else {
                if (data instanceof Scheduling) {
                    if (getActivity() != null) {
                        if (getActivity() instanceof FrtoActivity) {
                            FrtoActivity listener = (FrtoActivity) getActivity();
                            listener.concluiAgendamento((Scheduling) data);
                        }
                    }
                } else if (data instanceof ErrorCode) {
                    Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        loaderManager.destroyLoader(loader.getId());
        button.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {
    }

    private static class AgendarLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        Scheduling scheduling;


        AgendarLoader(Context context, Scheduling scheduling) {
            super(context);
            ctx = context;
            this.scheduling = scheduling;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            String json = "";
            try {
                json = ConnectApi.POST(scheduling, ConnectApi.AGENDAR, getContext());
                return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.SCHEDULING), Scheduling.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
        }
    }

    private static class CuponsLoaderCart extends AsyncTaskLoader<Object> {

        Context ctx;
        String userId, stoId;
        Object data;

        CuponsLoaderCart(Context context, String userId, String stoId, Object data) {
            super(context);
            ctx = context;
            this.userId = userId;
            this.stoId = stoId;
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
        public Object loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            try {
                String json = ConnectApi.GET(ConnectApi.CUPOM_USERS_FROM_CART + "/" + userId + "," + stoId, getContext());
                Coupon[] cupons = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonArray(AppConstants.COUPON), Coupon[].class);
                return new ArrayList<>(Arrays.asList(cupons));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface FrtoActivity {
        void meioPagamento(Scheduling scheduling);

        void cupons(Scheduling scheduling);

        void concluiAgendamento(Scheduling scheduling);
    }
}