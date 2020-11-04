package com.nuppin.User.pedido;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.User.dialogs.InfoDialogFragment;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.model.Chat;
import com.nuppin.model.ErrorCode;
import com.nuppin.model.Order;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.Util;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrPedidoDetalhe extends Fragment
        implements LoaderManager.LoaderCallbacks<Object>, InfoDialogFragment.InfoDialogListener {
    private LoaderManager loaderManager;
    private RvPedidoDetalheAdapter adapter;
    private RecyclerView mRecyclerView;
    private MaterialButton btnAvaliar, cancelar, chat;
    private Order order;
    private Toolbar toolbar;
    private TextView tSubtotal, entrega, badgeChat, tEntrega, tDesconto, resultDesconto, tTotal, ordId, ordData, nomeLoja, ordStatus, endereco, maps, pagamento, entregaOuRetirada, obs;
    private static final String ID_USER = "id_user";
    private static final String ID_ORDER = "id_order";
    private String idUser;
    private String idOrder;
    private SimpleDraweeView fotoLoja;
    private ProgressBar progressBar, progressBarInderteminate;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private String status;
    private Object data;

    public static FrPedidoDetalhe novaInstancia2(String idUser, String idOrder) {
        Bundle parametros = new Bundle();
        parametros.putString(ID_USER, idUser);
        parametros.putString(ID_ORDER, idOrder);
        FrPedidoDetalhe fragment = new FrPedidoDetalhe();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().containsKey(ID_USER))
            idUser = getArguments().getString(ID_USER);

        if (getArguments() != null && getArguments().containsKey(ID_ORDER))
            idOrder = getArguments().getString(ID_ORDER);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fr_request_detail, container, false);

        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrPedidoDetalhe.this);
            }
        });

        toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.detalhe_toolbar, toolbar, getActivity(), false, 0);


        cancelar = view.findViewById(R.id.ordCancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar cancelamento?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(FrPedidoDetalhe.this.getChildFragmentManager(), "confirm");
            }
        });

        mRecyclerView = view.findViewById(R.id.recyclerview_order);
        tSubtotal = view.findViewById(R.id.resultSubtotal);
        tEntrega = view.findViewById(R.id.resultEntrega);
        entrega = view.findViewById(R.id.Entrega);
        tDesconto = view.findViewById(R.id.desconto);
        resultDesconto = view.findViewById(R.id.resulDesconto);
        tTotal = view.findViewById(R.id.resultTotal);
        ordData = view.findViewById(R.id.ordData);
        ordId = view.findViewById(R.id.ordId);
        btnAvaliar = view.findViewById(R.id.btnAvaliar);
        nomeLoja = view.findViewById(R.id.nomeLoja);
        endereco = view.findViewById(R.id.txtEndereco);
        fotoLoja = view.findViewById(R.id.fotoLoja);
        progressBar = view.findViewById(R.id.progressBarStatus);
        progressBarInderteminate = view.findViewById(R.id.progressBarStatusInderterminate);
        pagamento = view.findViewById(R.id.meioPagamentoEscolhido);
        entregaOuRetirada = view.findViewById(R.id.entregaOuRetirada);
        badgeChat = view.findViewById(R.id.chatBadge);
        maps = view.findViewById(R.id.txtMaps);

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(getContext(), "Abrindo o maps..", Toast.LENGTH_SHORT).show();
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + order.getLatitude() + "," + order.getLongitude());
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

        ordStatus = view.findViewById(R.id.ordStatus);
        obs = view.findViewById(R.id.obs);
        btnAvaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.avaliarFromDetalhePedidos(order);
                }
            }
        });

        chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.openChat(order);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new RvPedidoDetalheAdapter();
        mRecyclerView.setAdapter(adapter);

        loaderManager = LoaderManager.getInstance(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loaderManager.restartLoader(0, null, FrPedidoDetalhe.this);
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
            return new PedidoLoader(getActivity(), idUser, idOrder, data);
        }else {
            return new AtualizaStatus(getActivity(), order, status);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        cancelar.setVisibility(View.GONE);

        if (data != null) {
            if (loader.getId() == 0) {
                this.data = data;
                if (data instanceof Map && ((Map) data).get(AppConstants.ORDERS) instanceof Order) {
                    order = (Order) ((Map) data).get(AppConstants.ORDERS);
                    adapter.setOrder(order.getItem());

                    if (order.getIs_chat_available() != 1) {
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

                    btnAvaliar.setVisibility(View.GONE);
                    switch (order.getStatus()) {
                        case AppConstants.STATUS_PENDING:
                            ordStatus.setText(R.string.pedido_aguardando_aprovacao);
                            progressBar.setProgress(1);
                            cancelar.setVisibility(View.VISIBLE);
                            break;
                        case AppConstants.STATUS_ACCEPTED:
                            if (order.getCategory_company_id().equals("1")) {
                                ordStatus.setText(R.string.pedido_em_preparo);
                            } else {
                                ordStatus.setText(R.string.pedido_em_separacao);
                            }
                            progressBar.setProgress(2);
                            break;
                        case AppConstants.STATUS_DELIVERY:
                            ordStatus.setText(R.string.pedido_saiu_entrega);
                            progressBar.setProgress(3);
                            break;
                        case AppConstants.STATUS_RELEASED:
                            ordStatus.setText(R.string.liberado_retirada);
                            progressBar.setProgress(3);
                            break;
                        case AppConstants.STATUS_CONCLUDED_NOT_RATED:
                        case AppConstants.STATUS_CONCLUDED:
                            progressBar.setProgress(4);
                            if (order.getRating() == 0) {
                                btnAvaliar.setVisibility(View.VISIBLE);
                            }
                            progressBarInderteminate.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            if (order.getType().equals("delivery")) {
                                ordStatus.setText(R.string.pedido_entregue);
                            } else {
                                ordStatus.setText(R.string.pedido_retirado);
                            }
                            break;
                        case AppConstants.STATUS_CANCELED_BY_ROBOT:
                        case AppConstants.STATUS_CANCELED_BY_USER:
                        case AppConstants.STATUS_CANCELED_REFUSED:
                            ordStatus.setText(R.string.pedido_cancelado);
                            progressBarInderteminate.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            break;
                    }


                    if (order.getType().equals("delivery")) {
                        entregaOuRetirada.setText(R.string.endereco_entrega);
                        endereco.setText(order.getAddress());
                        maps.setVisibility(View.GONE);
                    } else {
                        entregaOuRetirada.setText(R.string.endereco_retirada);
                        endereco.setText(order.getAddress());
                        entrega.setVisibility(View.GONE);
                        tEntrega.setVisibility(View.GONE);
                        maps.setVisibility(View.VISIBLE);
                    }

                    if (order.getDiscount_amount() > 0) {
                        resultDesconto.setText(Util.formaterPrice(order.getDiscount_amount()));
                        resultDesconto.setVisibility(View.VISIBLE);
                        tDesconto.setVisibility(View.VISIBLE);
                    }
                    if (order.getNote() != null && !order.getNote().equals("")) {
                        obs.setText(order.getNote());
                        obs.setVisibility(View.VISIBLE);
                    }
                    pagamento.setText(order.getPayment_method());
                    tSubtotal.setText(Util.formaterPrice(order.getSubtotal_amount()));
                    tEntrega.setText(Util.formaterPrice(order.getDelivery_amount()));
                    tTotal.setText(Util.formaterPrice((order.getTotal_amount())));
                    ordId.setText(String.valueOf(order.getOrder_id()));
                    ordData.setText(Util.timestampFormatDayMonthYear(order.getCreated_date()));
                    Util.hasPhoto(order, fotoLoja);
                    nomeLoja.setText(order.getCompany_name());
                    endereco.setText(order.getAddress());

                    errorLayout.setVisibility(View.GONE);
                }
            } else {
                loaderManager.destroyLoader(loader.getId());
                if (data instanceof Boolean && ((Boolean)data)) {
                    Toast.makeText(getContext(), "Cancelado com sucesso", Toast.LENGTH_SHORT).show();
                    loaderManager.restartLoader(0, null, this);
                } else if (data instanceof ErrorCode) {
                    Toast.makeText(getContext(), ((ErrorCode)data).getError_message(), Toast.LENGTH_SHORT).show();
                    loaderManager.restartLoader(0, null, this);
                }else {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            errorLayout.setVisibility(View.VISIBLE);
        }
        if (loader.getId() != 0) {
            loaderManager.destroyLoader(loader.getId());
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
                loaderManager.restartLoader(1, null, FrPedidoDetalhe.this);
                info.dismiss();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    private static class PedidoLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        String idUser;
        String idOrder;
        Object data;

        PedidoLoader(Context context, String idUser, String idOrder, Object data) {
            super(context);
            ctx = context;
            this.idUser = idUser;
            this.idOrder = idOrder;
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
            Order order;
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            Gson gson = new Gson();

            try {
                String sJson = ConnectApi.GET(ConnectApi.PEDIDO_DETALHE+"/"+idUser+","+idOrder+","+
                        Util.returnStringLatLonCountr(getContext()),getContext());
                JsonObject jObj = parser.parse(sJson).getAsJsonObject();
                order = gson.fromJson(jObj.getAsJsonObject(AppConstants.ORDERS), Order.class);
                mapOrdPro.put(AppConstants.ORDERS, order);
                try {
                    chats = gson.fromJson(jObj.getAsJsonArray(AppConstants.CHAT), Chat[].class);
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

        Order order;
        String status;
        Context ctx;

        private AtualizaStatus(Context context, Order order, String status) {
            super(context);
            ctx = context;
            this.order = order;
            this.status = status;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String sJson = "";
            try {
                sJson = ConnectApi.PATCH(order,ConnectApi.ATUALIZA_ORDER+"/"+status,getContext());
                JsonParser parser = new JsonParser();
                return gson.fromJson(parser.parse(sJson).getAsJsonPrimitive(),Boolean.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    JsonParser parser = new JsonParser();
                    return gson.fromJson(parser.parse(sJson).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
        }
    }

    public interface ToActivity{
        void avaliarFromDetalhePedidos(Order order);
        void openChat(Order order);
    }
}
