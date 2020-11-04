package com.nuppin.User.carrinho;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nuppin.User.dialogs.ClosedCompanyDialogFragment;
import com.nuppin.User.dialogs.TrocoDialogFragment;
import com.nuppin.model.Cart;
import com.nuppin.model.CartCompany;
import com.nuppin.model.CartInfo;
import com.nuppin.model.CartProduct;
import com.nuppin.model.Coupon;
import com.nuppin.model.ErrorCode;
import com.nuppin.model.Order;
import com.nuppin.User.dialogs.InfoDialogFragment;
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

public class FrCarrinho extends Fragment implements
        LoaderManager.LoaderCallbacks<Object>,
        RvCarrinhoAdapter.RvCartOnClickListener,
        InfoDialogFragment.InfoDialogListener,
        TrocoDialogFragment.TrocoDialogListener,
        ClosedCompanyDialogFragment.ClosedCompanyDialogListener {

    private LoaderManager loaderManager;
    private RvCarrinhoAdapter adapter;
    private RecyclerView mRecyclerView;
    private Button btn;
    private TextView nomeLoja, rating, tSubtotal, entrega, tEntrega, tTotal, tEnderecoEntrega, trocarEndereco, txtCupom, pagamentoEscolhido, desconto, descontotxt;
    private CardView cardCupom, cardStore;
    private SimpleDraweeView fotoLoja;
    private CartCompany cartCompany;
    private ArrayList cupons;
    private ConstraintLayout lCarrinho;
    private CartProduct cartProduct;
    private CartInfo cartInfo;
    private Toolbar toolbar;
    private Map mapOrdPro;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private CardView progress;
    private TextView infoEntrega;
    private MaterialButton btnEntrega, btnRetirada, alterarMeioPagamento, btnAdicionarCoupon;

    public FrCarrinho() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fr_cart, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                lCarrinho.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrCarrinho.this);
            }
        });

        progress = view.findViewById(R.id.progress);

        toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.carrinho_toolbar, toolbar, getActivity(), false, 0);

        final EditText obs = view.findViewById(R.id.obs);

        obs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cartCompany.setCart_note(obs.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        btnAdicionarCoupon = view.findViewById(R.id.btnAdicionarCoupon);
        infoEntrega = view.findViewById(R.id.infoEntrega);
        alterarMeioPagamento = view.findViewById(R.id.alterar);
        fotoLoja = view.findViewById(R.id.fotoLoja);
        nomeLoja = view.findViewById(R.id.nomeLoja);
        rating = view.findViewById(R.id.stars);
        mRecyclerView = view.findViewById(R.id.recyclerview_cart);
        tSubtotal = view.findViewById(R.id.resultSubtotal);
        tEntrega = view.findViewById(R.id.resultEntrega);
        entrega = view.findViewById(R.id.Entrega);
        tTotal = view.findViewById(R.id.resultTotal);
        btnEntrega = view.findViewById(R.id.btnEntregar);
        btnRetirada = view.findViewById(R.id.btnRetirada);
        tEnderecoEntrega = view.findViewById(R.id.txtEndereco);
        lCarrinho = view.findViewById(R.id.layoutCarrinho);
        cardCupom = view.findViewById(R.id.cardCupom);
        txtCupom = view.findViewById(R.id.txtCupom);
        btn = view.findViewById(R.id.btnComprar);
        trocarEndereco = view.findViewById(R.id.trocarEndereco);
        pagamentoEscolhido = view.findViewById(R.id.meioPagamentoEscolhido);
        desconto = view.findViewById(R.id.resulDesconto);
        descontotxt = view.findViewById(R.id.desconto);
        cardStore = view.findViewById(R.id.cardViewLoja);


        cardStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.companyCart(cartCompany);
                }
            }
        });

        btnEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDelivery();
                cartInfo.setType("delivery");
                loaderManager.restartLoader(4, null, FrCarrinho.this);
                progress.setVisibility(View.VISIBLE);
            }
        });

        btnRetirada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocal();
                cartInfo.setType("local");
                loaderManager.restartLoader(4, null, FrCarrinho.this);
                progress.setVisibility(View.VISIBLE);
            }
        });

        if (cartCompany != null) {
            obs.setText(cartCompany.getCart_note());
        }

        btnAdicionarCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartCompany.getDiscount_amount() == 0) {
                    if (getActivity() instanceof FrtoActivity) {
                        FrtoActivity listener = (FrtoActivity) getActivity();
                        listener.cupons(cartInfo);
                    }
                }else {
                    cartInfo.setCoupon_id(null);
                    loaderManager.restartLoader(4, null, FrCarrinho.this);
                }
            }
        });

        alterarMeioPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.meioPagamento(cartInfo);
                }
            }
        });

        //todo
        trocarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrCarrinho.FrtoActivity) {
                    FrCarrinho.FrtoActivity listener = (FrCarrinho.FrtoActivity) getActivity();
                    listener.endereco(0);//0 - coloca no backstack; 1 - sem backstack
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cartCompany.getIs_available() != 1) {
                    DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.cart_warning_location), getResources().getString(R.string.limpar_carrinho), getResources().getString(R.string.cancelar));
                    dialogFrag.show(FrCarrinho.this.getChildFragmentManager(), "info");
                    return;
                }

                if (cartCompany.getCategory_company_id().equals("1") && cartCompany.getIs_online() != 1) {
                    DialogFragment dialogFrag = ClosedCompanyDialogFragment.newInstance(getResources().getString(R.string.alimentos_apena_loja_aberta), getResources().getString(R.string.limpar_carrinho), getResources().getString(R.string.voltar));
                    dialogFrag.show(FrCarrinho.this.getChildFragmentManager(), "info");
                    return;
                }

                if (cartCompany.getMin_purchase() > (cartCompany.getSubtotal_amount() - cartCompany.getDiscount_amount()) && (cartCompany.getDelivery_type_value() == 4 && cartCompany.getInfo().getType().equals("delivery"))) {
                    DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.aviso_min_pedido_gratis), getResources().getString(R.string.ir_a_loja), getResources().getString(R.string.voltar));
                    dialogFrag.show(FrCarrinho.this.getChildFragmentManager(), "pedido_minimo");
                    return;
                }

                if (cartCompany.getInfo().getPayment_method() == null) {
                    Toast.makeText(getContext(), R.string.selecione_metodo_pagamento, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cartCompany.getIs_online() == 1) {
                    DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar pedido?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                    dialogFrag.show(FrCarrinho.this.getChildFragmentManager(), "confirm");
                } else {
                    DialogFragment dialogFrag = ClosedCompanyDialogFragment.newInstance("Seu pedido só será liberado amanhã. Deseja continuar?", getResources().getString(R.string.continuar), getResources().getString(R.string.revisar));
                    dialogFrag.show(FrCarrinho.this.getChildFragmentManager(), "confirm_closed");
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new RvCarrinhoAdapter(this, getContext());
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
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new CartLoader(getActivity(), cartCompany, mapOrdPro);
        } else if (id == 1) {
            return new Cart2Order(getActivity(), cartCompany);
        } else if (id == 2) {
            return new DeleteItem(getActivity(), cartProduct);
        } else if (id == 3) {
            return new LimpaCarrinho(getActivity());
        }else if (id == 4) {
            return new UpdateType(getActivity(), cartInfo);
        }
        return null;
    }

    private void setLocal() {
        btnRetirada.setTextColor(requireContext().getResources().getColor(R.color.colorPrimary));
        btnEntrega.setTextColor(requireContext().getResources().getColor(R.color.shadow_color));
        trocarEndereco.setVisibility(View.GONE);
        btnRetirada.setClickable(false);
        btnEntrega.setClickable(true);
        tEnderecoEntrega.setText(cartCompany.getUser_address());
        entrega.setVisibility(View.GONE);
        tEntrega.setVisibility(View.GONE);
    }

    private void setDelivery() {
        btnEntrega.setTextColor(requireContext().getResources().getColor(R.color.colorPrimary));
        btnRetirada.setTextColor(requireContext().getResources().getColor(R.color.shadow_color));
        trocarEndereco.setVisibility(View.VISIBLE);
        entrega.setVisibility(View.VISIBLE);
        tEntrega.setVisibility(View.VISIBLE);
        tEnderecoEntrega.setText(cartCompany.getFull_address());
        tEntrega.setText(Util.formaterPrice(cartCompany.getDelivery_amount()));
        btnRetirada.setClickable(true);
        btnEntrega.setClickable(false);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        infoEntrega.setVisibility(View.GONE);
        btn.setClickable(true);
        if (data != null) {
            switch (loader.getId()) {
                case 0:
                    mapOrdPro = (Map) data;
                    if (((Map) data).get(AppConstants.CART_COMPANY) instanceof CartCompany) {

                        cartCompany = (CartCompany) ((Map) data).get(AppConstants.CART_COMPANY);
                        cartInfo = cartCompany.getInfo();

                        if (cartCompany.getCart_sto_numRating() == 0) {
                            rating.setText(R.string.novo);
                            rating.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            rating.setText(getResources().getString(R.string.rating_and_num_rating, Util.formaterRating(cartCompany.getRating() / cartCompany.getCart_sto_numRating()), cartCompany.getCart_sto_numRating()));
                            rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_black_12dp, 0, 0, 0);
                        }

                        if (cartCompany.getIs_available() != 1) {
                            DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.cart_warning_location), getResources().getString(R.string.limpar_carrinho), getResources().getString(R.string.cancelar));
                            dialogFrag.show(FrCarrinho.this.getChildFragmentManager(), "info");
                            lCarrinho.setVisibility(View.GONE);
                            linearEmpty.setVisibility(View.VISIBLE);
                            return;
                        }

                        if (cartCompany.getIs_delivery() == 1) {
                            btnEntrega.setVisibility(View.VISIBLE);
                        }else {
                            btnRetirada.setVisibility(View.INVISIBLE);
                        }
                        if (cartCompany.getIs_local() == 1) {
                            btnRetirada.setVisibility(View.VISIBLE);
                        }else {
                            btnRetirada.setVisibility(View.INVISIBLE);
                        }

                        adapter.setCart(cartCompany.getProduct());
                        nomeLoja.setText(cartCompany.getName());
                        Util.hasPhoto(cartCompany, fotoLoja);
                        switch (cartCompany.getInfo().getType()) {
                            case "delivery":
                                setDelivery();
                                break;
                            case "local":
                                setLocal();
                                break;
                        }

                        if (cartCompany.getNot_enough_amount_for_free_delivery() != null) {
                            infoEntrega.setVisibility(View.VISIBLE);
                            infoEntrega.setText("Pedido minimo: " + Util.formaterPrice(cartCompany.getMin_purchase()));
                        } else if ((cartCompany.getFree_delivery_over_available() != null)) {
                            infoEntrega.setVisibility(View.VISIBLE);
                            infoEntrega.setText("Entrega grátis acima de " + Util.formaterPrice(cartCompany.getMin_purchase()));
                        }

                        if (cartCompany.getInfo().getPayment_method() != null) {
                            pagamentoEscolhido.setText(cartCompany.getInfo().getPayment_method());
                        }

                        tSubtotal.setText(Util.formaterPrice(cartCompany.getSubtotal_amount()));
                        tTotal.setText(Util.formaterPrice(cartCompany.getTotal_amount()));


                        if (((Map) data).get(AppConstants.COUPON) instanceof List) {
                            cupons = ((ArrayList) ((Map) data).get(AppConstants.COUPON));
                            if (cupons.size() > 0) {
                                cardCupom.setVisibility(View.VISIBLE);
                                if (cartCompany.getDiscount_amount() == 0) {
                                    btnAdicionarCoupon.setText("Adicionar");
                                    desconto.setVisibility(View.GONE);
                                    descontotxt.setVisibility(View.GONE);
                                    if (cupons.size() < 2) {
                                        txtCupom.setText(getResources().getString(R.string.qtd_coupon_disponivel, cupons.size()));
                                    } else {
                                        txtCupom.setText(getResources().getString(R.string.qtd_cupons_disponiveis, cupons.size()));
                                    }
                                }else {
                                    desconto.setVisibility(View.VISIBLE);
                                    desconto.setText(Util.formaterPrice(cartCompany.getDiscount_amount()));
                                    descontotxt.setVisibility(View.VISIBLE);
                                    btnAdicionarCoupon.setText("Remover");
                                    txtCupom.setText(getResources().getString(R.string.text_coupon_discount, Util.formaterPrice(cartCompany.getDiscount_amount())));
                                }
                            }
                        }
                    }

                    if (((Map) data).get(AppConstants.CART_COMPANY_EMPTY) != null) {
                        lCarrinho.setVisibility(View.GONE);
                        linearEmpty.setVisibility(View.VISIBLE);
                        try {
                            Thread.sleep(1000);
                            Util.backFragmentFunction(this);
                        }catch (Exception e){
                            //nothing
                        }
                    }
                    lCarrinho.setVisibility(View.VISIBLE);
                    linearEmpty.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    break;
                case 1:
                    if (data instanceof ErrorCode) {
                        Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
                        loaderManager.restartLoader(0, null, FrCarrinho.this);
                    } else {
                        if (getActivity() instanceof FrtoActivity) {
                            FrtoActivity listener = (FrtoActivity) getActivity();
                            listener.concluiCompra((Order) data);
                        }
                    }
                    break;
                case 2:
                    loaderManager.restartLoader(0, null, FrCarrinho.this);
                    break;
                case 3:
                    Util.backFragmentFunction(this);
                    break;
                case 4:
                    if (data instanceof Boolean && (Boolean)data){
                        Toast.makeText(getContext(), "Atualizado", Toast.LENGTH_SHORT).show();
                        loaderManager.restartLoader(0, null, FrCarrinho.this);
                    }else {
                        Toast.makeText(getContext(), "Erro ao atualizar. Tente novamente!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        } else {
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
    public void onClick(CartProduct cart) {
        if (getActivity() instanceof FrtoActivity) {
            FrtoActivity listener = (FrtoActivity) getActivity();
            listener.productCart(cart, cartCompany.getCategory_company_id());
        }
    }

    @Override
    public void onClickExclui(CartProduct product) {
        cartProduct = product;
        progress.setVisibility(View.VISIBLE);
        loaderManager.restartLoader(2, null, FrCarrinho.this);
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        if (info.getTag() != null) {
            switch (info.getTag()) {
                case "confirm":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            if (cartCompany.getInfo().getPayment_method().toLowerCase().equals("dinheiro")) {
                                DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.troco), getResources().getString(R.string.sim), getResources().getString(R.string.nao));
                                dialogFrag.show(FrCarrinho.this.getChildFragmentManager(), "troco");
                            } else {
                                progress.setVisibility(View.VISIBLE);
                                btn.setClickable(false);
                                loaderManager.restartLoader(1, null, FrCarrinho.this);
                                info.dismiss();
                                break;
                            }
                        case R.id.btnNegative:
                            info.dismiss();
                            break;
                    }
                    break;
                case "info":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            loaderManager.restartLoader(3, null, FrCarrinho.this);
                            info.dismiss();
                            break;
                        case R.id.btnNegative:
                            info.dismiss();
                            Util.backFragmentFunction(this);
                            break;
                    }
                    break;
                case "troco":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            DialogFragment dialogFrag = TrocoDialogFragment.newInstance(getResources().getString(R.string.troco_detail), getResources().getString(R.string.confirmar), "");
                            dialogFrag.show(FrCarrinho.this.getChildFragmentManager(), "troco_detail");
                            info.dismiss();
                            break;
                        case R.id.btnNegative:
                            progress.setVisibility(View.VISIBLE);
                            btn.setClickable(false);
                            loaderManager.restartLoader(1, null, FrCarrinho.this);
                            info.dismiss();
                            break;
                    }
                    break;
                case "pedido_minimo":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            if (getActivity() instanceof FrtoActivity) {
                                FrtoActivity listener = (FrtoActivity) getActivity();
                                listener.companyCart(cartCompany);
                            }
                            info.dismiss();
                            break;
                        case R.id.btnNegative:
                            info.dismiss();
                            break;
                    }
            }
        }
    }

    @Override
    public void onDialogOKClick(View view, String value, TrocoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                cartCompany.setCart_note((cartCompany.getCart_note() + "\nTROCO PARA " + value).trim());
                progress.setVisibility(View.VISIBLE);
                btn.setClickable(false);
                loaderManager.restartLoader(1, null, FrCarrinho.this);
                info.dismiss();
                break;
        }
    }

    @Override
    public void onDialogOKClick(View view, ClosedCompanyDialogFragment closed) {
        if (closed.getTag() != null) {
            switch (closed.getTag()) {
                case "info":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            loaderManager.restartLoader(3, null, FrCarrinho.this);
                            closed.dismiss();
                            break;
                        case R.id.btnNegative:
                            closed.dismiss();
                            break;
                    }
                    break;
                case "confirm_closed":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar pedido?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                            dialogFrag.show(FrCarrinho.this.getChildFragmentManager(), "confirm");
                            closed.dismiss();
                            break;
                        case R.id.btnNegative:
                            closed.dismiss();
                            break;
                    }
            }
        }
    }


    private static class CartLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        CartCompany cartCompany;
        Map map;

        CartLoader(Context context, CartCompany cartCompany, Map map) {
            super(context);
            ctx = context;
            this.cartCompany = cartCompany;
            this.map = map;
        }


        @Override
        protected void onStartLoading() {
            if (map != null) {
                deliverResult(map);
            }
            forceLoad();

        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String sJson = ConnectApi.GET(ConnectApi.CART + "/" +
                    UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx) +",nuppin,"
                    + Util.returnStringLatLonCountr(getContext()), getContext());
            JsonParser parser = new JsonParser();
            Coupon[] coupon;
            CartCompany cartCompany;
            List<Coupon> couponList;

            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                cartCompany = gson.fromJson(parser.parse(sJson).getAsJsonObject().getAsJsonObject(AppConstants.CART_COMPANY), CartCompany.class);

                if (this.cartCompany != null) {
                    cartCompany.setCart_note(this.cartCompany.getCart_note());
                }

                try {
                    JsonArray cupons = parser.parse(sJson).getAsJsonObject().getAsJsonArray(AppConstants.COUPON);
                    coupon = gson.fromJson(cupons, Coupon[].class);
                    couponList = new ArrayList<>(Arrays.asList(coupon));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    couponList = null;
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    Boolean b = gson.fromJson(parser.parse(sJson).getAsJsonObject().getAsJsonPrimitive(AppConstants.CART_COMPANY_EMPTY), Boolean.class);
                    mapOrdPro.put(AppConstants.CART_COMPANY_EMPTY, b);
                    return mapOrdPro;
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
            mapOrdPro.put(AppConstants.CART_COMPANY, cartCompany);
            mapOrdPro.put(AppConstants.COUPON, couponList);
            return mapOrdPro;
        }
    }

    private static class Cart2Order extends AsyncTaskLoader<Object> {

        CartCompany cartCompany;

        private Cart2Order(Context context, CartCompany cartCompany) {
            super(context);
            this.cartCompany = cartCompany;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            String jsonString = ConnectApi.POST(cartCompany, ConnectApi.ORDERS, getContext());
            try {
                return gson.fromJson(parser.parse(jsonString).getAsJsonObject().getAsJsonObject(AppConstants.ORDERS), Order.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    return gson.fromJson(parser.parse(jsonString).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
        }
    }

    private static class DeleteItem extends AsyncTaskLoader<Object> {

        CartProduct product;

        DeleteItem(Context context, CartProduct product) {
            super(context);
            this.product = product;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.DELETE(product, ConnectApi.CART_ITEM, getContext()), CartProduct.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class LimpaCarrinho extends AsyncTaskLoader<Object> {

        LimpaCarrinho(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                Cart cart = new Cart(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.DELETE(cart, ConnectApi.CART_LIMPA_TUDO, getContext()), Cart.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class UpdateType extends AsyncTaskLoader<Object> {

        CartInfo info;

        UpdateType(Context context, CartInfo info) {
            super(context);
            this.info = info;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.PATCH(info, ConnectApi.CART_INFO, getContext()), Boolean.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface FrtoActivity {
        void endereco(int index);

        void meioPagamento(CartInfo cartInfo);

        void cupons(CartInfo cartInfo);

        void companyCart(CartCompany company);

        void productCart(CartProduct product, String catId);

        void concluiCompra(Order order);
    }
}