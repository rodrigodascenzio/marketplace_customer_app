package com.nuppin.User.loja;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nuppin.model.CompanyCategory;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaAdapterViewHolder> {

    private List<CompanyCategory> stoCategorias;
    private CategoriaOnClickListener listener;
    private View view;
    private int viewPosition;

    CategoriaAdapter(CategoriaOnClickListener handler) {
        listener = handler;
    }

    public class CategoriaAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView categoria;
        SimpleDraweeView fotoCategoria;
        ConstraintLayout cardView;
        Context ctx;
        View viewInner;

        CategoriaAdapterViewHolder(View view) {
            super(view);
            viewInner = view;
            ctx = view.getContext();
            categoria = view.findViewById(R.id.categoria_loja);
            fotoCategoria = view.findViewById(R.id.fotoCategoria);
            cardView = view.findViewById(R.id.card);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickFromCategorias(stoCategorias.get(adapterPosition).getCategory_company_id());
            UtilShaPre.setDefaultsCategoria(AppConstants.CATEGORY,stoCategorias.get(adapterPosition).getCategory_company_id(),ctx);
            fotoCategoria.setBackground(ctx.getResources().getDrawable(R.drawable.categoria_selecionada));

            if (view != null) {
                SimpleDraweeView viewCategoria = view.findViewById(R.id.fotoCategoria);
                viewCategoria.getHierarchy().setPlaceholderImage(setaIconSecond(stoCategorias.get(viewPosition).getCategory_company_id(), ctx));
                viewCategoria.setBackground(ctx.getResources().getDrawable(R.drawable.categoria_sem_sected));
            }
            fotoCategoria.getHierarchy().setPlaceholderImage(setaIcon(stoCategorias.get(adapterPosition).getCategory_company_id(), ctx));
            fotoCategoria.setBackground(ctx.getResources().getDrawable(R.drawable.categoria_selecionada));

            viewPosition = adapterPosition;
            view = v;
        }
    }

    @NonNull
    public CategoriaAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_companies_category;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CategoriaAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaAdapterViewHolder holder, int position) {

        if (!stoCategorias.get(position).getCategory_company_id().equals(UtilShaPre.getDefaultsCategoria(AppConstants.CATEGORY, holder.ctx))) {
            holder.fotoCategoria.getHierarchy().setPlaceholderImage(setaIconSecond(stoCategorias.get(position).getCategory_company_id(),holder.ctx));
            holder.fotoCategoria.setBackground(holder.ctx.getResources().getDrawable(R.drawable.categoria_sem_sected));
        }else {
            view = holder.viewInner;
            viewPosition = position;
            UtilShaPre.setDefaultsCategoria(AppConstants.CATEGORY,stoCategorias.get(position).getCategory_company_id(),holder.ctx);
            holder.fotoCategoria.getHierarchy().setPlaceholderImage(setaIcon(stoCategorias.get(position).getCategory_company_id(),holder.ctx));
            holder.fotoCategoria.setBackgroundColor(holder.ctx.getResources().getColor(R.color.background_light));
            holder.fotoCategoria.setBackground(holder.ctx.getResources().getDrawable(R.drawable.categoria_selecionada));
        }

        if (position == stoCategorias.size() - 1) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            layoutParams.setMargins(holder.ctx.getResources().getDimensionPixelOffset(R.dimen.bottom_last_item_margin), 0, holder.ctx.getResources().getDimensionPixelOffset(R.dimen.bottom_last_item_margin), 0);
        }
        holder.categoria.setText(stoCategorias.get(position).getCategory_name());
    }


    private Drawable setaIcon(String nomeIcon, Context ctx){
        if (nomeIcon != null) {
            switch (nomeIcon) {
                case "0":
                    return ctx.getResources().getDrawable(R.drawable.card_bulleted_outline_white);
                case "1":
                    return ctx.getResources().getDrawable(R.drawable.food_white);
                case "2":
                    return ctx.getResources().getDrawable(R.drawable.tshirt_crew_outline_white);
                case "3":
                    return ctx.getResources().getDrawable(R.drawable.hair_dryer_outline_white);
            }
            return ctx.getResources().getDrawable(R.drawable.information_outline);
        }
        return null;
    }

    private Drawable setaIconSecond(String nomeIcon, Context ctx){
        if (nomeIcon != null) {
            switch (nomeIcon) {
                case "0":
                    return ctx.getResources().getDrawable(R.drawable.card_bulleted_outline);
                case "1":
                    return ctx.getResources().getDrawable(R.drawable.food);
                case "2":
                    return ctx.getResources().getDrawable(R.drawable.tshirt_crew_outline);
                case "3":
                    return ctx.getResources().getDrawable(R.drawable.hair_dryer_outline);
            }
            return ctx.getResources().getDrawable(R.drawable.information_outline);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (stoCategorias == null)
            return 0;
        return stoCategorias.size();
    }

    void setSCategorias(List mstoCategorias) {
        stoCategorias = mstoCategorias;
        notifyDataSetChanged();
    }


    public interface CategoriaOnClickListener {
        void onClickFromCategorias(String id);
    }

}