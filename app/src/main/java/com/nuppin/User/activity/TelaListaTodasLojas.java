package com.nuppin.User.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.nuppin.User.loja.FrCompanyDescription;
import com.nuppin.User.perfil.FrFeedback;
import com.nuppin.User.perfil.FrSuggestion;
import com.nuppin.model.CartInfo;
import com.nuppin.model.Scheduling;
import com.nuppin.model.Cart;
import com.nuppin.model.CartProduct;
import com.nuppin.model.CartCompany;
import com.nuppin.model.Chat;
import com.nuppin.model.Company;
import com.nuppin.model.Address;
import com.nuppin.model.Order;
import com.nuppin.model.Product;
import com.nuppin.model.Service;
import com.nuppin.model.CompanyCategory;
import com.nuppin.model.User;
import com.nuppin.User.carrinho.FrCarrinho;
import com.nuppin.User.carrinho.meiosPagamento;
import com.nuppin.User.address.FrManualEndereco;
import com.nuppin.User.address.FrPrincipal;
import com.nuppin.User.loja.FrListaTodasLojas;
import com.nuppin.User.loja.RvLojaAdapter;
import com.nuppin.User.loja.SubCategoriaAdapter;
import com.nuppin.User.loja.porCategoria.FrListaTodasLojasPorCategoria;
import com.nuppin.User.pedido.FrPedidoDetalhe;
import com.nuppin.User.pedido.FragmentAvaliarStore;
import com.nuppin.User.pedido.PedidosViewPager;
import com.nuppin.User.pedido.RvOrdersAdapter;
import com.nuppin.User.perfil.FrPerfilUsuario;
import com.nuppin.User.perfil.FrPerfilUsuarioNav;
import com.nuppin.User.perfil.FrPerfilUsuarioUpdateCelular;
import com.nuppin.User.perfil.FrPerfilUsuarioUpdateEmail;
import com.nuppin.User.produto.FrDetalheProduto;
import com.nuppin.User.produto.FrListaProdutos;
import com.nuppin.User.service.ListaServicos;
import com.nuppin.User.service.RvServicosAdapter;
import com.nuppin.User.service.scheduling.AgendamentosFr;
import com.nuppin.User.service.scheduling.HorariosFr;
import com.nuppin.User.service.scheduling.HorariosHeaderAdapter;
import com.nuppin.User.service.scheduling.PedidoAgendamentoDetalhe;
import com.nuppin.User.service.scheduling.RvAgendamentosAdapter;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.chat.FrChat;
import com.nuppin.coupon.ListaCupom;
import com.nuppin.R;

public class TelaListaTodasLojas extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        RvLojaAdapter.RvLojaOnClickListener,
        RvOrdersAdapter.RvOrdenOnClickListener,
        RvAgendamentosAdapter.RvAgendamentosOnClickListener,
        FrListaTodasLojas.FrtoActivity,
        FrListaProdutos.FrtoActivity,
        HorariosHeaderAdapter.HorariosAdapterListener,
        FrCarrinho.FrtoActivity,
        RvServicosAdapter.RvServicosOnClickListener,
        SubCategoriaAdapter.SubCategoriaOnClickListener,
        FrManualEndereco.ToActivity,
        FrPrincipal.ToActivity,
        AgendamentosFr.FrtoActivity,
        ListaCupom.FrtoActivity,
        FrPedidoDetalhe.ToActivity,
        PedidoAgendamentoDetalhe.ToActivity,
        FragmentManager.OnBackStackChangedListener,
        FrPerfilUsuarioNav.ToActivity,
        FrPerfilUsuario.ToActivity{

    FragmentTransaction transaction;
    ConstraintLayout cartButton;
    View bottom, btshadow, cartshadow;
    TextView cartTotal, cartQuantidade;
    BottomNavigationView navigationView;
    boolean isFinalCart = false;
    FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btshadow = findViewById(R.id.shadowbt);
        cartshadow = findViewById(R.id.shadowcart);
        cartButton = findViewById(R.id.carrinho);
        cartTotal = findViewById(R.id.cartTotal);
        cartQuantidade = findViewById(R.id.cartQuantidade);
        bottom = findViewById(R.id.tb_main_bt);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setOnNavigationItemSelectedListener(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseInstanceId.getInstance().getInstanceId();

        openFragment(new FrListaTodasLojas(), "FrListaLojas", false);
        openFrag(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        openFrag(intent);
    }


    public void openFrag(Intent it) {
        String json = it.getStringExtra(AppConstants.CHAT);
        Gson gson = new Gson();
        if (json != null) {
            try {
                Chat chat = gson.fromJson(json, Chat.class);
                if (getSupportFragmentManager().findFragmentByTag("FrChat") == null) {
                    openFragment(FrChat.newInstance(chat.getChat_to(), chat.getOrder_id(), chat.getChat_from()), "FrChat", true);
                }
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, this));
                    FirebaseCrashlytics.getInstance().recordException(e);
                Toast.makeText(this, "Erro ao abrir o chat", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        transaction = null;
    }

    @Override
    public void endereco(int index) {
        switch (index) {
            case 0:
                openFragment(new FrPrincipal(), "FrPrincipal", true);
                break;
            case 1:
                openFragment(new FrPrincipal(), "FrPrincipal", false);
                break;
        }
    }

    @Override
    public void notReady(int index) {
        switch (index) {
            case R.id.feedback:
                openFragment(new FrFeedback(), "FrFeedback", true);
                break;
            case R.id.indicate:
                openFragment(new FrSuggestion(), "FrSuggestion", true);
                break;
        }
    }

    @Override
    public void meioPagamento(CartInfo cartInfo) {
        openFragment(meiosPagamento.newInstance(cartInfo), "meiosPagamentos", true);
    }

    @Override
    public void cupons(CartInfo cartInfo) {
        openFragment(ListaCupom.newInstanceCart(cartInfo), "ListaCupom", true);
    }

    @Override
    public void ListaCuponsToStore(Company company) {
        openFragment(FrListaProdutos.newInstanceFromCart(company), "FrLojaCupom", true);
    }

    @Override
    public void ListaCuponsToServico(Company company) {
        openFragment(ListaServicos.novaInstancia2(company), "FrServicosCupom", true);
    }

    @Override
    public void companyCart(CartCompany company) {
        Company s = new Company();
        s.setId(company.getCompany_id());
        s.setNome(company.getName());
        s.setCategory_company_id(company.getCategory_company_id());
        s.setphoto(company.getphoto());
        openFragment(FrListaProdutos.newInstanceFromCart(s), "FrLojaCart", true);
    }

    @Override
    public void productCart(CartProduct product, String catId) {
        openFragment(FrDetalheProduto.novaInstanciaEdit(product.getProduct_id(), product.getCompany_id(), catId, product.getCart_id()), "FrDetalheProdutoEdit", true);
    }

    @Override
    public void concluiCompra(Order order) {
        isFinalCart = true;
        openFragment(FrPedidoDetalhe.novaInstancia2(order.getUser_id(), order.getOrder_id()), "FrDetalhePedidoPosCompra", true);
        navigationView.getMenu().findItem(R.id.navigation_pedidos).setChecked(true);
    }

    @Override
    public void meioPagamento(Scheduling scheduling) {
        openFragment(meiosPagamento.newInstance(scheduling), "meiosPagamentos", true);
    }

    @Override
    public void cupons(Scheduling scheduling) {
        openFragment(ListaCupom.newInstanceScheduling(scheduling), "ListaCupom", true);
    }

    @Override
    public void concluiAgendamento(Scheduling scheduling) {
        isFinalCart = true;
        openFragment(PedidoAgendamentoDetalhe.newInstance(scheduling), "FrDetalhePedidoPosAgendamento", true);
        navigationView.getMenu().findItem(R.id.navigation_pedidos).setChecked(true);
    }

    @Override
    public void onClick(Company company) {
        if (company.getCategory_company_id().equals("3")) {
            openFragment(ListaServicos.novaInstancia2(company), "FrServicos", true);
        } else {
            openFragment(FrListaProdutos.newInstance(company), "FrLoja", true);
        }
    }

    @Override
    public void onClickFromSubCategorias(CompanyCategory category) {
        openFragment(FrListaTodasLojasPorCategoria.newInstance(category, category.getSubcategory_name()), "FrListaTodasLojasPorCategoria", true);
    }

    @Override
    public void selecionado(Product product, String catId) {
        openFragment(FrDetalheProduto.novaInstancia(product.getProduct_id(), product.getCompany_id(), catId), "FrDetalheProduto", true);
    }

    @Override
    public void description(Company company) {
        openFragment(FrCompanyDescription.newInstance(company), "FrCompanyDescription", true);
    }

    public void clicouCart(View view) {
        openFragment(new FrCarrinho(), "FrCarrinho", true);
    }

    @Override
    public void onClick(Order order) {
        openFragment(FrPedidoDetalhe.novaInstancia2(UtilShaPre.getDefaultsString(AppConstants.USER_ID, this), order.getOrder_id()), "FrDetalhePedidoDireto", true);
    }

    @Override
    public void onClickAvaliar(Order order) {
        openFragment(FragmentAvaliarStore.newInstance(order), "FragmentAvaliarStore", true);
    }

    @Override
    public void onClick(Scheduling scheduling) {
        openFragment(PedidoAgendamentoDetalhe.newInstance(scheduling), "FrDetalhePedidoDireto", true);
    }

    @Override
    public void onClickAvaliar(Scheduling scheduling) {
        openFragment(FragmentAvaliarStore.newInstance(scheduling), "FragmentAvaliarStore", true);
    }

    @Override
    public void carrinho(Cart carrinho, int index) {
        if (!isFinalCart) {
            if (carrinho.getTemcarrinho() == 0) {
                cartButton.setVisibility(View.GONE);
                cartshadow.setVisibility(View.GONE);
                if (index == 0) {
                    bottom.setVisibility(View.VISIBLE);
                    btshadow.setVisibility(View.VISIBLE);
                }
            } else {
                if (index == 0) {
                    bottom.setVisibility(View.VISIBLE);
                    btshadow.setVisibility(View.GONE);
                }
                cartButton.setVisibility(View.VISIBLE);
                cartshadow.setVisibility(View.VISIBLE);
                cartTotal.setText(Util.formaterPrice(carrinho.getCartProValor()));
                cartQuantidade.setText(String.valueOf(carrinho.getCartQuantidade()));
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_lojas: {
                if (navigationView.getMenu().findItem(R.id.navigation_lojas).isChecked()) {
                    break;
                }
                clearBackStack();
                openFragment(new FrListaTodasLojas(), "FrListaLojas", false);
                break;
            }
            case R.id.navigation_pedidos: {
                if (navigationView.getMenu().findItem(R.id.navigation_pedidos).isChecked()) {
                    break;
                }
                clearBackStack();
                openFragment(PedidosViewPager.newInstance(UtilShaPre.getDefaultsString(AppConstants.USER_ID, TelaListaTodasLojas.this)), "FrPedidos", true);
                break;
            }
            case R.id.navigation_coupon: {
                if (navigationView.getMenu().findItem(R.id.navigation_coupon).isChecked()) {
                    break;
                }
                clearBackStack();
                openFragment(ListaCupom.newInstance(UtilShaPre.getDefaultsString(AppConstants.USER_ID, TelaListaTodasLojas.this)), "ListaCupons", true);
                break;
            }
            case R.id.navigation_perfil: {
                if (navigationView.getMenu().findItem(R.id.navigation_perfil).isChecked()) {
                    break;
                }
                clearBackStack();
                openFragment(FrPerfilUsuarioNav.newInstance(UtilShaPre.getDefaultsString(AppConstants.USER_ID, TelaListaTodasLojas.this)), "Perfil", true);
                break;
            }
        }
        return true;
    }

    @Override
    public void onClickHorario(Scheduling horario, Service service, Company company) {
        openFragment(AgendamentosFr.newInstance(horario, service, company), "FrAgendamentos", true);
    }

    @Override
    public void onClick(Service service, Company company) {
        openFragment(HorariosFr.newInstance(service, company), "FrHorarios", true);
    }

    @Override
    public void FrManualEndereco() {
        clearBackStack();
        openFragment(new FrListaTodasLojas(), "FrListaLojas", false);
    }

    @Override
    public void FrPrincipal(Address address) {
        openFragment(FrManualEndereco.instance(address), "FrEnderecoDetalhe", true);
    }

    @Override
    public void addressManual() {
        openFragment(new FrManualEndereco(), "FrEnderecoDetalhe", true);
    }

    @Override
    public void avaliarFromDetalhePedidos(Order order) {
        openFragment(FragmentAvaliarStore.newInstance(order), "AvaliarPedido", true);
    }

    @Override
    public void openChat(Order order) {
        openFragment(FrChat.newInstance(order.getUser_id(), order.getOrder_id(), order.getCompany_id()), "FrChat", true);
    }

    @Override
    public void AvaliarFromPedidoAgendamentoDetalhe(Scheduling scheduling) {
        openFragment(FragmentAvaliarStore.newInstance(scheduling), "AvaliarPedido", true);
    }

    @Override
    public void openChat(Scheduling scheduling) {
        openFragment(FrChat.newInstance(scheduling.getUser_id(), scheduling.getScheduling_id(), scheduling.getCompany_id()), "FrChat", true);
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void openFragment(Fragment fragment, String tagBackStack, boolean tag) {
        transaction = getSupportFragmentManager().beginTransaction();

        if (tagBackStack.equals("FrDetalhePedidoPosCompra") || tagBackStack.equals("FrDetalhePedidoPosAgendamento")) {
            cartButton.setVisibility(View.GONE);
            cartshadow.setVisibility(View.GONE);
            clearBackStack();
        } else {
            isFinalCart = false;
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        transaction.replace(R.id.container, fragment, tagBackStack);
        if (tag) {
            transaction.addToBackStack(tagBackStack);
        }
        transaction.commit();

        if (!tagBackStack.equals("FrLoja") && !tagBackStack.equals("FrLojaCart") && !tagBackStack.equals("FrLojaCupom") && !tagBackStack.equals("FrListaLojas")) {
            cartButton.setVisibility(View.GONE);
            cartshadow.setVisibility(View.GONE);
        }

        firebaseAnalytics.setCurrentScreen(this, tagBackStack, tagBackStack);
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("FrLojaCart") != null) {
            cartButton.setVisibility(View.GONE);
            cartshadow.setVisibility(View.GONE);        }

        if (getSupportFragmentManager().findFragmentByTag("FrDetalhePedidoPosCompra") != null ||
                getSupportFragmentManager().findFragmentByTag("FrDetalhePedidoPosAgendamento") != null) {
            navigationView.getMenu().findItem(R.id.navigation_lojas).setChecked(true);
        }

        final int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count <= 1 && !navigationView.getMenu().findItem(R.id.navigation_lojas).isChecked()) {
            navigationView.setSelectedItemId(R.id.navigation_lojas);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onBackStackChanged() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment != null && fragment.getTag() != null) {
            if (!fragment.getTag().equals("FrListaLojas") && !fragment.getTag().equals("FrPedidos") && !fragment.getTag().equals("ListaCupons") && !fragment.getTag().equals("Perfil")) {
                bottom.setVisibility(View.GONE);
                btshadow.setVisibility(View.GONE);
            } else {
                bottom.setVisibility(View.VISIBLE);
                btshadow.setVisibility(View.VISIBLE);
                if (!fragment.getTag().equals("FrListaLojas")) {
                    cartButton.setVisibility(View.GONE);
                    cartshadow.setVisibility(View.GONE);
                } else {
                    btshadow.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void editarUsuario(User user) {
        openFragment(FrPerfilUsuario.newInstance(user), "EditarPerfil", true);
    }

    @Override
    public void feedback() {
        openFragment(new FrFeedback(), "FrFeedback", true);
    }

    @Override
    public void indicar() {
        openFragment(new FrSuggestion(), "FrSuggestion", true);
    }

    @Override
    public void updateEmail(User user) {
        openFragment(FrPerfilUsuarioUpdateEmail.newInstance(user), "EditarEmail", true);
    }

    @Override
    public void updateCelular(User user) {
        openFragment(FrPerfilUsuarioUpdateCelular.newInstance(user), "EditarCelular", true);
    }
}