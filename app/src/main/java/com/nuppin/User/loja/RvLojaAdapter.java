package com.nuppin.User.loja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nuppin.model.Company;
import com.nuppin.Util.Util;
import com.nuppin.R;

import java.util.List;

public class RvLojaAdapter extends RecyclerView.Adapter<RvLojaAdapter.RvLojaAdapterViewHolder> {

    private List<Company> companies;
    private RvLojaOnClickListener listener;

    public RvLojaAdapter(RvLojaOnClickListener handler) {
        listener = handler;
    }

    public class RvLojaAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final SimpleDraweeView fotoLoja;
        final TextView companyDistancia, nome, subCategoria, rating, entrega, retirada;
        ImageView iconStar;
        Context ctx;
        ConstraintLayout fechada;

       RvLojaAdapterViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            entrega = view.findViewById(R.id.entrega);
            retirada = view.findViewById(R.id.retirada);
            fotoLoja = view.findViewById(R.id.fotoLoja);
            companyDistancia = view.findViewById(R.id.distancia);
            nome = view.findViewById(R.id.nome);
            subCategoria = view.findViewById(R.id.subCategoria);
            rating = view.findViewById(R.id.rating);
            iconStar = view.findViewById(R.id.icon_star);
            fechada = view.findViewById(R.id.fechada);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(companies.get(adapterPosition));
        }
    }

    @NonNull
    public RvLojaAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForListItem = R.layout.rv_list_company;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;
            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            return new RvLojaAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvLojaAdapterViewHolder holder, int position) {
        holder.entrega.setVisibility(View.GONE);
        holder.retirada.setVisibility(View.GONE);
        holder.fechada.setVisibility(View.GONE);
        ConstraintLayout.LayoutParams lparams = (ConstraintLayout.LayoutParams) holder.retirada.getLayoutParams();
        lparams.horizontalBias = 1;
        holder.retirada.setLayoutParams(lparams);

        holder.entrega.setTextColor(holder.ctx.getResources().getColor(android.R.color.tab_indicator_text));
        Util.hasPhoto(companies.get(position),holder.fotoLoja);

        if (companies.get(position).getIs_online() != 1) {
            holder.fechada.setVisibility(View.VISIBLE);
        }

        if (companies.get(position).getIs_local() == 1) {
            holder.retirada.setVisibility(View.VISIBLE);
            holder.retirada.setText(R.string.retirada);
            holder.retirada.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stores_12px, 0, 0, 0);
            if (companies.get(position).getCategory_company_id().equals("3")) {
                holder.retirada.setText(R.string.scheduling);
                holder.retirada.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_schedule_black_12dp, 0, 0, 0);
            }
            holder.companyDistancia.setText(Util.formaterAll(companies.get(position).getDistance()));
        }else {
            holder.companyDistancia.setText(Util.formater(companies.get(position).getDistance()));
        }

        if (companies.get(position).getIs_delivery() == 1) {
            holder.entrega.setVisibility(View.VISIBLE);

            if (!companies.get(position).getCategory_company_id().equals("3")){
                if (companies.get(position).getMax_radius_free() == 0 || companies.get(position).getMax_radius_free() < companies.get(position).getDistance()) {
                    switch (companies.get(position).getDelivery_type_value()) {
                        case 1:
                            holder.entrega.setText(holder.ctx.getResources().getString(R.string.loja_adapter_time_and_delivery_value, companies.get(position).getDelivery_max_time(), Util.formaterPrice(companies.get(position).getDelivery_fixed_fee())));
                            break;
                        case 2:
                            holder.entrega.setText(holder.ctx.getResources().getString(R.string.loja_adapter_time_and_delivery_value, companies.get(position).getDelivery_max_time(), Util.formaterPrice(Util.roundHalf(companies.get(position).getDelivery_fixed_fee() + (companies.get(position)).getDelivery_variable_fee() * companies.get(position).getDistance()))));
                            break;
                        case 3:
                            holder.entrega.setText(holder.ctx.getResources().getString(R.string.loja_adapter_time_and_delivery_value, companies.get(position).getDelivery_max_time(), Util.formaterPrice(Util.roundHalf(companies.get(position).getDelivery_variable_fee() * companies.get(position).getDistance()))));
                            break;
                        case 4:
                            holder.entrega.setText(holder.ctx.getResources().getString(R.string.loja_adapter_time_and_delivery_value_gratis, companies.get(position).getDelivery_max_time()));
                            break;
                    }
                } else {
                    holder.entrega.setText(holder.ctx.getResources().getString(R.string.loja_adapter_time_and_delivery_value_gratis, companies.get(position).getDelivery_max_time()));
                }
            }else {
                holder.entrega.setText(R.string.scheduling_delivery);
            }
        }else {
            lparams.horizontalBias = 0;
            holder.retirada.setLayoutParams(lparams);
        }

            holder.nome.setText(companies.get(position).getNome());
            if (companies.get(position).getNum_rating() == 0) {
                holder.rating.setText(R.string.novo);
                holder.iconStar.setVisibility(View.GONE);
            } else {
                holder.iconStar.setVisibility(View.VISIBLE);
                holder.iconStar.setImageResource(R.drawable.ic_star_black_12dp);
                holder.rating.setText(Util.formaterRating(companies.get(position).getRating() / companies.get(position).getNum_rating()));
            }
        holder.subCategoria.setText(holder.ctx.getResources().getString(R.string.loja_adapter_dot_string, companies.get(position).getSubcategory_name()));

    }

    @Override
    public int getItemCount() {
        if (companies != null) {
            return companies.size();
        }else {
            return 0;
        }
    }


    public void setCompanies(List<Company> companies) {
        this.companies = companies;
        notifyDataSetChanged();
    }


    public interface RvLojaOnClickListener {
        void onClick(Company company);
    }

}

