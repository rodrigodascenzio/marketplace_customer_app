package com.nuppin.User.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.model.Address;
import com.nuppin.R;

import java.util.List;

public class RvEnderecos extends RecyclerView.Adapter<RvEnderecos.RvEnderecosAdapterViewHolder> {

    private List<Address> Addresses;
    private RvEnderecos.RvEnderecosOnClickListener listener;
    private View view;
    private Context ctx;

    RvEnderecos(RvEnderecos.RvEnderecosOnClickListener handler, Context ctx) {
        this.ctx = ctx;
        listener = handler;
    }

    public class RvEnderecosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView nomeEndereco, dots;
        final TextView edit, exclui;
        final ImageView img;
        View viewBottom;
        ConstraintLayout content;

        RvEnderecosAdapterViewHolder(View view) {
            super(view);
            nomeEndereco = view.findViewById(R.id.nomeEndereco);
            edit = view.findViewById(R.id.editEndereco);
            exclui = view.findViewById(R.id.excluiEndereco);
            img = view.findViewById(R.id.img);
            content = view.findViewById(R.id.content);
            dots = view.findViewById(R.id.dots);
            viewBottom = view.findViewById(R.id.view);
            view.setOnClickListener(this);
            edit.setOnClickListener(this);
            exclui.setOnClickListener(this);
            content.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            final int adapterPosition = getAdapterPosition();

            switch (v.getId()) {
                case R.id.editEndereco:
                    listener.onClickEdit(Addresses.get(adapterPosition));
                    break;
                case R.id.excluiEndereco:
                    listener.onClickExclui(Addresses.get(adapterPosition));
                    break;
                case R.id.content:
                    listener.onClick(Addresses.get(adapterPosition));
                    break;
                default:
                    if (view != null) {

                        if (view == v) {
                            if (edit.getVisibility() == View.VISIBLE){
                                edit.setVisibility(View.GONE);
                                exclui.setVisibility(View.GONE);
                                dots.setText(R.string.v_ellipsis);
                                return;
                            }else {
                                edit.setVisibility(View.VISIBLE);
                                exclui.setVisibility(View.VISIBLE);
                                dots.setText(R.string.h_ellipsis);
                                return;
                            }
                        }

                        view.findViewById(R.id.editEndereco).setVisibility(View.GONE);
                        view.findViewById(R.id.excluiEndereco).setVisibility(View.GONE);
                        ((TextView)view.findViewById(R.id.dots)).setText(R.string.v_ellipsis);

                    }

                    edit.setVisibility(View.VISIBLE);
                    exclui.setVisibility(View.VISIBLE);
                    dots.setText(R.string.h_ellipsis);

                    view = v;
                    break;
            }
        }
    }

    @NonNull
    public RvEnderecos.RvEnderecosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_address;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvEnderecos.RvEnderecosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvEnderecos.RvEnderecosAdapterViewHolder holder, int position) {
        holder.dots.setText(R.string.v_ellipsis);
        if (position == 0) {
            holder.content.setPadding(
                    ctx.getResources().getDimensionPixelOffset(R.dimen.dez_dp),
                    ctx.getResources().getDimensionPixelOffset(R.dimen.vinte_dp),
                    ctx.getResources().getDimensionPixelOffset(R.dimen.zero_dp),
                    ctx.getResources().getDimensionPixelOffset(R.dimen.vinte_dp));
            holder.viewBottom.getLayoutParams().height = ctx.getResources().getDimensionPixelOffset(R.dimen.dois_dp);
        }

        holder.edit.setVisibility(View.GONE);
        holder.exclui.setVisibility(View.GONE);
        if (Addresses.get(position).getIs_selected() == 1) {
            holder.img.setImageResource(R.drawable.ic_my_location_accent_24dp);
            holder.viewBottom.setBackgroundResource(R.color.colorAccent);
        } else {
            holder.img.setImageResource(R.drawable.ic_history_black_24dp);
            holder.viewBottom.setBackgroundResource(R.color.shadow_color);
        }
        holder.nomeEndereco.setText((Addresses.get(position).getFull_address()));
    }

    @Override
    public int getItemCount() {
        if (null == Addresses)
            return 0;
        return Addresses.size();
    }

    void setAddresses(List<Address> Addresses) {
        this.Addresses = Addresses;
        notifyDataSetChanged();
    }


    public interface RvEnderecosOnClickListener {
        void onClick(Address address);
        void onClickEdit(Address address);
        void onClickExclui(Address address);
    }

}

