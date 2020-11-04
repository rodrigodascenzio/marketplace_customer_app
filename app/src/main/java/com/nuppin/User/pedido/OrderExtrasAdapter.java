package com.nuppin.User.pedido;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.R;
import com.nuppin.model.OrderItemExtra;

import java.util.List;

public class OrderExtrasAdapter extends RecyclerView.Adapter<OrderExtrasAdapter.ConjuctExtrasAdapterViewHolder> {

    private List<OrderItemExtra> extraItems;

    OrderExtrasAdapter() {}

    class ConjuctExtrasAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView eiName, eiQuantity;
        Context ctx;
        View view;

        ConjuctExtrasAdapterViewHolder(View view) {
            super(view);
            this.view = view;
            ctx = view.getContext();
            eiQuantity = view.findViewById(R.id.quantidade);
            eiName = view.findViewById(R.id.nome);
        }
    }

    @NonNull
    @Override
    public ConjuctExtrasAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_cart_extras;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctExtrasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctExtrasAdapterViewHolder holder, int position) {
        holder.eiName.setText(extraItems.get(position).getName());
        holder.eiQuantity.setText(String.valueOf(extraItems.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        if (extraItems == null) {
            return 0;
        } else {
            return extraItems.size();
        }
    }

    void setExtras(List<OrderItemExtra> extraItems) {
        this.extraItems = extraItems;
        notifyDataSetChanged();
    }
}
