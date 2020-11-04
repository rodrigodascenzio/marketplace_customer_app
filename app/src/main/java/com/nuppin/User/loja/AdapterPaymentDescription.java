package com.nuppin.User.loja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.R;
import com.nuppin.model.CompanyPayment;

import java.util.List;

public class AdapterPaymentDescription extends RecyclerView.Adapter<AdapterPaymentDescription.MeiosPagamentosAdapterViewHolder> {

    private List<CompanyPayment> companyPayments;

    AdapterPaymentDescription() { }

    class MeiosPagamentosAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView meiopag;

        MeiosPagamentosAdapterViewHolder(View view) {
            super(view);
            meiopag = view.findViewById(R.id.name);
        }
    }

    @NonNull
    public MeiosPagamentosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_payment_company_description;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MeiosPagamentosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeiosPagamentosAdapterViewHolder holder, int position) {
            holder.meiopag.setText(companyPayments.get(position).getName());
            holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        if (companyPayments == null)
            return 0;
        return companyPayments.size();
    }

    void setMeiosPagamento(List meiosPagamentos) {
        this.companyPayments = meiosPagamentos;
        notifyDataSetChanged();
    }

}