package com.nuppin.User.loja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.model.CompanyCategory;
import com.nuppin.R;

import java.util.List;

public class SubCategoriaAdapter extends RecyclerView.Adapter<SubCategoriaAdapter.SubCategoriaAdapterViewHolder> {

    private List<CompanyCategory> stoCategorias;
    private SubCategoriaOnClickListener listener;

    SubCategoriaAdapter(SubCategoriaOnClickListener handler) {
        listener = handler;
    }

    public class SubCategoriaAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subcategoria, dot;
        Context ctx;

        SubCategoriaAdapterViewHolder(View view) {
            super(view);
            subcategoria = view.findViewById(R.id.categoria_loja);
            dot = view.findViewById(R.id.dot);
            ctx = view.getContext();
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickFromSubCategorias(stoCategorias.get(adapterPosition));
        }
    }

    @NonNull
    public SubCategoriaAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_company_subcategory;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new SubCategoriaAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoriaAdapterViewHolder holder, int position) {
        if (position == 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.subcategoria.getLayoutParams();
            layoutParams.setMargins(holder.ctx.getResources().getDimensionPixelOffset(R.dimen.subcategory_first_item_margin), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.subcategory_first_item_right_margin), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.subcategory_first_item_right_margin), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.subcategory_first_item_right_margin));
            if (position == stoCategorias.size() - 1) {
                holder.dot.setVisibility(View.GONE);
            }
        }else if (position == stoCategorias.size() - 1) {
            holder.dot.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.subcategoria.getLayoutParams();
            layoutParams.setMargins(holder.ctx.getResources().getDimensionPixelOffset(R.dimen.subcategory_first_item_right_margin), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.subcategory_first_item_right_margin), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.subcategory_first_item_margin), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.subcategory_first_item_right_margin));
        }

        holder.subcategoria.setText(stoCategorias.get(position).getSubcategory_name());
        holder.setIsRecyclable(false);
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


    public interface SubCategoriaOnClickListener {
        void onClickFromSubCategorias(CompanyCategory category);
    }

}