package com.nuppin.coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nuppin.model.Coupon;
import com.nuppin.Util.Util;
import com.nuppin.R;

import java.util.List;

public class listaCuponsAdapter extends RecyclerView.Adapter<listaCuponsAdapter.ListaCuponsAdapterViewHoler> {

    private List<Coupon> cupons;
    private CupomOnClickListener listener;

    listaCuponsAdapter(CupomOnClickListener handler) {
        listener = handler;
    }

    public class ListaCuponsAdapterViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView desconto, nomeLoja, expira;
        SimpleDraweeView fotoLoja;

        ListaCuponsAdapterViewHoler(View view) {
            super(view);
            desconto = view.findViewById(R.id.desconto);
            fotoLoja = view.findViewById(R.id.fotoLoja);
            nomeLoja = view.findViewById(R.id.nomeLoja);
            expira = view.findViewById(R.id.expira);
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
        int layoutIdForListItem = R.layout.rv_list_coupon;
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
        holder.nomeLoja.setText(cupons.get(position).getCompany().getNome());
        Util.hasPhoto(cupons.get(position).getCompany(), holder.fotoLoja);


        if (cupons.get(position).getExpires_day() < 1 && cupons.get(position).getExpires_hour() < 1 && cupons.get(position).getExpires_minute() < 1) {
            holder.expira.setText(holder.expira.getResources().getString(R.string.expirado));
        } else {
            if (cupons.get(position).getExpires_day() < 1) {
                if (cupons.get(position).getExpires_hour() < 1) {
                    holder.expira.setText("Expira em " + cupons.get(position).getExpires_minute() + " minutos");
                } else if(cupons.get(position).getExpires_hour() < 2) {
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