package com.nuppin.User.pedido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nuppin.Util.Util;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.R;

public class PedidosViewPager extends Fragment {

    private String idUser;
    private static final String ID_USER = "id_user";


    public PedidosViewPager() {
        }

    public static PedidosViewPager newInstance(String idUser) {
        PedidosViewPager fragment = new PedidosViewPager();
        Bundle args = new Bundle();
        args.putString(ID_USER, idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ID_USER)) {
            idUser = getArguments().getString(ID_USER);
        }
    }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.vp_request_scheduling, container, false);

            Util.cancelNotifyOnOff(getContext(), 2);

            ViewPager viewPager;
            Toolbar toolbar;
            toolbar = view.findViewById(R.id.toolbar_top);
            Util.setaToolbar(this, R.string.pedidos, toolbar, getActivity(), false, 0);

            viewPager = view.findViewById(R.id.viewpager);
            TabLayout tabLayout = view.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setAdapter(new CustomPagerAdapter(getChildFragmentManager(), idUser));
            viewPager.setCurrentItem(UtilShaPre.getDefaultsCategoriaOrders("order_categoria",getContext()));
            return view;
        }


        private class CustomPagerAdapter extends FragmentStatePagerAdapter {

            String userId;

            private CustomPagerAdapter(FragmentManager fragmentManager, String userId) {
                super(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                this.userId = userId;
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                int positionPlus = position + 1;
                switch (position) {
                    case 0:
                        return FrPedidos.newInstance(userId,positionPlus);
                    case 1:
                        return FrPedidos.newInstance(userId,positionPlus);
                    case 2:
                        return FrPedidos.newInstance(userId,positionPlus);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getResources().getString(R.string.alimentos);
                    case 1:
                        return getResources().getString(R.string.produtos);
                    case 2:
                        return getResources().getString(R.string.service);
                    default:
                        return "";

                }
            }

        }
}
