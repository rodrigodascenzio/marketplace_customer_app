package com.nuppin.User.service.scheduling;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.User.dialogs.InfoDialogFragment;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.model.Chat;
import com.nuppin.model.Scheduling;
import com.nuppin.Util.Util;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoAgendamentoDetalhe extends Fragment
        implements LoaderManager.LoaderCallbacks<Object>, InfoDialogFragment.InfoDialogListener {

    private static final String AGENDAMENTO = "AGENDAMENTO";
    private LoaderManager loaderManager;
    private Scheduling scheduling;
    private TextView dataAge, horaInicio, badgeChat, profissional, preco, servico, obs, endereco, nomeLoja, ordId, ordData, ordStatus, tDesconto, resultDesconto, tTotal, tSubtotal, pagamento;
    private ProgressBar progressBar, progressBarInderterminate;
    private SimpleDraweeView fotoLoja;
    private MaterialButton btnAvaliar, cancelar, chat;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private String status;
    private Object data;


    public PedidoAgendamentoDetalhe() {
    }

    public static PedidoAgendamentoDetalhe newInstance(Scheduling scheduling) {
        PedidoAgendamentoDetalhe fragment = new PedidoAgendamentoDetalhe();
        Bundle args = new Bundle();
        args.putParcelable(AGENDAMENTO, scheduling);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(AGENDAMENTO)) {
            scheduling = getArguments().getParcelable(AGENDAMENTO);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_scheduling_detail, container, false);

        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, PedidoAgendamentoDetalhe.this);
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.detalhe_toolbar, toolbar, getActivity(), false, 0);

        btnAvaliar = view.findViewById(R.id.ageAvaliar);
        fotoLoja = view.findViewById(R.id.fotoLoja);
        nomeLoja = view.findViewById(R.id.nomeLoja);
        endereco = view.findViewById(R.id.txtEndereco);
        dataAge = view.findViewById(R.id.dataAge);
        profissional = view.findViewById(R.id.ageFuncionario);
        servico = view.findViewById(R.id.servico);
        preco = view.findViewById(R.id.preco);
        obs = view.findViewById(R.id.obs);
        horaInicio = view.findViewById(R.id.ageHoraInicio);
        ordId = view.findViewById(R.id.ordId);
        ordData = view.findViewById(R.id.ordData);
        ordStatus = view.findViewById(R.id.ordStatus);
        progressBar = view.findViewById(R.id.progressBarStatus);
        progressBarInderterminate = view.findViewById(R.id.progressBarStatusInderterminate);
        tDesconto = view.findViewById(R.id.desconto);
        resultDesconto = view.findViewById(R.id.resulDesconto);
        tTotal = view.findViewById(R.id.resultTotal);
        tSubtotal = view.findViewById(R.id.resultSubtotal);
        pagamento = view.findViewById(R.id.meioPagamentoEscolhido);
        cancelar = view.findViewById(R.id.ageCancelar);
        badgeChat = view.findViewById(R.id.chatBadge);
        TextView maps = view.findViewById(R.id.txtMaps);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(getContext(), "Abrindo o maps..", Toast.LENGTH_SHORT).show();
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + scheduling.getLatitude() + "," + scheduling.getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Erro ao abrir no maps", Toast.LENGTH_SHORT).show();
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        });


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar cancelamento?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(PedidoAgendamentoDetalhe.this.getChildFragmentManager(), "confirm");
            }
        });


        chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.openChat(scheduling);
                }
            }
        });


        btnAvaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.AvaliarFromPedidoAgendamentoDetalhe(scheduling);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loaderManager.restartLoader(0, null, PedidoAgendamentoDetalhe.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new AgendamentoLoader(getActivity(), scheduling, data);
        } else {
            return new AtualizaStatus(getActivity(), scheduling, status);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        cancelar.setVisibility(View.GONE);
        btnAvaliar.setVisibility(View.GONE);

        if (data != null) {
            if (loader.getId() == 0) {
                this.data = data;
                if (data instanceof Map && ((Map) data).get(AppConstants.SCHEDULING) instanceof Scheduling) {
                    scheduling = (Scheduling) ((Map) data).get(AppConstants.SCHEDULING);
                    Util.hasPhoto(scheduling, fotoLoja);
                    nomeLoja.setText(scheduling.getCompany_name());
                    preco.setText(Util.formaterPrice(scheduling.getSubtotal_amount()));
                    profissional.setText(scheduling.getEmployee_name());
                    servico.setText(scheduling.getService_name());
                    endereco.setText(scheduling.getAddress());
                    horaInicio.setText(getResources().getString(R.string.time_with_dots, scheduling.getStart_time(), scheduling.getEnd_time()));
                    dataAge.setText(Util.timestampFormatDayMonthYear(scheduling.getStart_date()));
                    ordData.setText(Util.timestampFormatDayMonthYear(scheduling.getCreated_date()));
                    ordId.setText(scheduling.getScheduling_id());

                    if (scheduling.getIs_chat_available() != 1) {
                        chat.setVisibility(View.GONE);
                    }

                    if (((Map) data).get(AppConstants.CHAT) instanceof List) {
                        int fat_qtd = ((List) ((Map) data).get(AppConstants.CHAT)).size();
                        if (fat_qtd > 0) {
                            badgeChat.setVisibility(View.VISIBLE);
                            badgeChat.setText(String.valueOf(fat_qtd));
                        } else {
                            badgeChat.setVisibility(View.GONE);
                        }
                    }

                    switch (scheduling.getStatus()) {
                        case AppConstants.STATUS_PENDING:
                            ordStatus.setText(R.string.pedido_aguardando_aprovacao);
                            progressBar.setProgress(1);
                            cancelar.setVisibility(View.VISIBLE);
                            break;
                        case AppConstants.STATUS_ACCEPTED:
                            ordStatus.setText(R.string.scheduling_reservado);
                            progressBar.setProgress(2);
                            cancelar.setVisibility(View.VISIBLE);
                            break;
                        case AppConstants.STATUS_CONCLUDED_NOT_RATED:
                        case AppConstants.STATUS_CONCLUDED:
                            ordStatus.setText(R.string.concluido);
                            progressBar.setProgress(3);
                            progressBarInderterminate.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            if (scheduling.getRating() == 0) {
                                btnAvaliar.setVisibility(View.VISIBLE);
                            }
                            break;
                        case AppConstants.STATUS_CANCELED_BY_ROBOT:
                        case AppConstants.STATUS_CANCELED_REFUSED:
                        case AppConstants.STATUS_CANCELED_BY_USER:
                        case AppConstants.STATUS_CANCELED_BY_COMPANY:
                            ordStatus.setText(R.string.scheduling_cancelado);
                            progressBar.setProgress(0);
                            progressBarInderterminate.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            break;
                        case AppConstants.STATUS_NOSHOW:
                            ordStatus.setText(R.string.nao_compareceu);
                            progressBar.setProgress(0);
                            progressBarInderterminate.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            break;
                    }


                    if (scheduling.getNote() != null && !scheduling.getNote().equals("")) {
                        obs.setVisibility(View.VISIBLE);
                        obs.setText(scheduling.getNote());
                    }

                    if (scheduling.getDiscount_amount() > 0) {
                        resultDesconto.setText(Util.formaterPrice(scheduling.getDiscount_amount()));
                        resultDesconto.setVisibility(View.VISIBLE);
                        tDesconto.setVisibility(View.VISIBLE);
                    }


                    pagamento.setText(scheduling.getPayment_method());
                    tSubtotal.setText(Util.formaterPrice(scheduling.getSubtotal_amount()));
                    tTotal.setText(Util.formaterPrice((scheduling.getTotal_amount())));
                }
                errorLayout.setVisibility(View.GONE);
            } else if (loader.getId() == 1) {
                loaderManager.destroyLoader(loader.getId());
                if (data instanceof Boolean && ((Boolean) data)) {
                    Toast.makeText(getContext(), "Cancelado com sucesso", Toast.LENGTH_SHORT).show();
                    loaderManager.restartLoader(0, null, this);
                } else {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (loader.getId() != 0) {
                loaderManager.destroyLoader(loader.getId());
            }
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                status = AppConstants.STATUS_CANCELED_BY_USER;
                loaderManager.restartLoader(1, null, PedidoAgendamentoDetalhe.this);
                info.dismiss();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    private static class AgendamentoLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        Scheduling scheduling;
        Object data;

        AgendamentoLoader(Context context, Scheduling scheduling, Object data) {
            super(context);
            ctx = context;
            this.scheduling = scheduling;
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
            JsonParser parser = new JsonParser();
            List<Chat> listChats;
            listChats = new ArrayList<>();
            Chat[] chats;
            Scheduling schedulingRe;
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            Gson gson = new Gson();

            try {
                String sJson = ConnectApi.GET(ConnectApi.AGENDAMENTO + "/" + scheduling.getUser_id() + "," + scheduling.getScheduling_id() + "," +
                        Util.returnStringLatLonCountr(getContext()), getContext());

                schedulingRe = gson.fromJson(parser.parse(sJson).getAsJsonObject().getAsJsonObject(AppConstants.SCHEDULING), Scheduling.class);
                mapOrdPro.put(AppConstants.SCHEDULING, schedulingRe);

                try {
                    chats = gson.fromJson(parser.parse(sJson).getAsJsonObject().getAsJsonArray(AppConstants.CHAT), Chat[].class);
                    listChats.addAll(Arrays.asList(chats));
                    mapOrdPro.put(AppConstants.CHAT, listChats);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    listChats = null;
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
            return mapOrdPro;
        }
    }


    private static class AtualizaStatus extends AsyncTaskLoader<Object> {

        Scheduling scheduling;
        String status;
        Context ctx;

        private AtualizaStatus(Context context, Scheduling scheduling, String status) {
            super(context);
            ctx = context;
            this.scheduling = scheduling;
            this.status = status;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Boolean loadInBackground() {
            try {
                Gson gson = new Gson();
                String json;
                json = ConnectApi.PATCH(scheduling, ConnectApi.ATUALIZA_STATUS_AGENDAMENTO + "/" + status, getContext());
                JsonParser parser = new JsonParser();
                return gson.fromJson(parser.parse(json).getAsJsonPrimitive(), Boolean.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void AvaliarFromPedidoAgendamentoDetalhe(Scheduling scheduling);

        void openChat(Scheduling scheduling);
    }

}