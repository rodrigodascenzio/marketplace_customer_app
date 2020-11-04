package com.nuppin.coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.model.Coupon;
import com.nuppin.Util.Util;
import com.nuppin.R;

import java.util.List;

public class listaCuponsInsideStoreAdapter extends RecyclerView.Adapter<listaCuponsInsideStoreAdapter.ListaCuponsAdapterViewHoler> {

    private List<Coupon> cupons;
    private CupomOnClickListener listener;

    public listaCuponsInsideStoreAdapter() {
    }

    listaCuponsInsideStoreAdapter(CupomOnClickListener handler) {
        listener = handler;
    }

    public class ListaCuponsAdapterViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView desconto, expira, pedidoMinimo;
        View view;

        ListaCuponsAdapterViewHoler(View view) {
            super(view);
            desconto = view.findViewById(R.id.desconto);
            expira = view.findViewById(R.id.expira);
            pedidoMinimo = view.findViewById(R.id.pedidoMinimo);
            this.view = view;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.coupon(cupons.get(adapterPosition));
        }
    }

    public ListaCuponsAdapterViewHoler onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_list_coupon_inside_company;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ListaCuponsAdapterViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaCuponsAdapterViewHoler holder, int position) {
        if (cupons.get(position).getDiscount_type() != null) {
            if (cupons.get(position).getDiscount_type().equals("1")) {
                holder.desconto.setText(holder.desconto.getResources().getString(R.string.money_off, Util.formaterPrice(cupons.get(position).getValue())));
            } else {
                holder.desconto.setText(holder.desconto.getResources().getString(R.string.percent_off, (int) cupons.get(position).getValue()));
            }
        }

        if (cupons.get(position).getExpires_day() < 1 && cupons.get(position).getExpires_hour() < 1 && cupons.get(position).getExpires_minute() < 1) {
            holder.expira.setText(holder.expira.getResources().getString(R.string.expirado));
            holder.view.setClickable(false);
            holder.view.setEnabled(false);
        } else {
            if (cupons.get(position).getExpires_day() < 1) {
                if (cupons.get(position).getExpires_hour() < 1) {
                    holder.expira.setText("Expira em " + cupons.get(position).getExpires_minute() + " minutos");
                }else if(cupons.get(position).getExpires_hour() < 2) {
                    holder.expira.setText("Expira em " + cupons.get(position).getExpires_hour() + " hora e " + cupons.get(position).getExpires_minute() + " minutos");
                }else {
                    holder.expira.setText("Expira em " + cupons.get(position).getExpires_hour() + " horas e " + cupons.get(position).getExpires_minute() + " minutos");
                }
            } else if (cupons.get(position).getExpires_day() == 1) {
                holder.expira.setText("Expira amanhã");
            } else {
                holder.expira.setText("Expira em " + cupons.get(position).getExpires_day() + " dias");
            }
        }

        if (cupons.get(position).getQuantity_used() >= cupons.get(position).getQuantity()) {
            holder.pedidoMinimo.setText(holder.pedidoMinimo.getResources().getString(R.string.esgotado));
        }else if(cupons.get(position).getMin_purchase() > 0) {
            holder.pedidoMinimo.setText(holder.pedidoMinimo.getResources().getString(R.string.pedido_minimo_cupom, Util.formaterPrice(cupons.get(position).getMin_purchase())));
        }else {
            holder.pedidoMinimo.setText(holder.pedidoMinimo.getResources().getString(R.string.sem_pedido_minimo));
        }
    }

    @Override
    public int getItemCount() {
        if (cupons == null)
            return 0;
        return cupons.size();
    }

    void setCupons(List cupons) {
        this.cupons = cupons;
        notifyDataSetChanged();
    }


    public interface CupomOnClickListener {
        void coupon(Coupon coupon);
    }

}