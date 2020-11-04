package com.nuppin.User.service.scheduling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.nuppin.Util.AppConstants;
import com.nuppin.model.Scheduling;
import com.nuppin.Util.Util;
import com.nuppin.R;

import java.util.List;

public class RvAgendamentosAdapter extends RecyclerView.Adapter<RvAgendamentosAdapter.RvAgendamentosAdapterViewHolder>{

    private RvAgendamentosOnClickListener listener;
    private List<Scheduling> ordList;

    public RvAgendamentosAdapter(RvAgendamentosOnClickListener handler) {
        listener = handler;
    }

    public class RvAgendamentosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView ordId, ordStatus, stoNome, ageData, ageDataInicio, ageHoraInicio;
        SimpleDraweeView img;
        MaterialButton avaliar;


        RvAgendamentosAdapterViewHolder(View view) {
            super(view);
            ordId = view.findViewById(R.id.ordId);
            ordStatus = view.findViewById(R.id.ordStatus);
            stoNome = view.findViewById(R.id.nomeLoja);
            img = view.findViewById(R.id.fotoLoja);
            avaliar = view.findViewById(R.id.btnAvaliar);
            ageData = view.findViewById(R.id.ordData);
            ageDataInicio = view.findViewById(R.id.dataAge);
            ageHoraInicio = view.findViewById(R.id.ageHoraInicio);


            avaliar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    listener.onClickAvaliar(ordList.get(adapterPosition));
                }
            });
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(ordList.get(adapterPosition));
        }
    }

    @NonNull
    public RvAgendamentosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_scheduling;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvAgendamentosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvAgendamentosAdapterViewHolder holder, int position) {
        holder.avaliar.setVisibility(View.GONE);

        switch (ordList.get(position).getStatus()) {
            case AppConstants.STATUS_PENDING:
                holder.ordStatus.setText(R.string.pedido_aguardando_aprovacao);
                //todo - liberar cancelar com alguns parametros, como tempo excedido por exemplo
                break;
            case AppConstants.STATUS_ACCEPTED:
                holder.ordStatus.setText(R.string.scheduling_reservado);
                //todo - liberar cancelar com alguns parametros, como tempo excedido por exemplo
                break;
            case AppConstants.STATUS_CONCLUDED_NOT_RATED:
            case AppConstants.STATUS_CONCLUDED:
                holder.ordStatus.setText(R.string.concluido);
                if (ordList.get(position).getRating() == 0) {
                    holder.avaliar.setVisibility(View.VISIBLE);
                }
                break;
            case AppConstants.STATUS_CANCELED_BY_ROBOT:
            case AppConstants.STATUS_CANCELED_REFUSED:
            case AppConstants.STATUS_CANCELED_BY_USER:
            case AppConstants.STATUS_CANCELED_BY_COMPANY:
                holder.ordStatus.setText(R.string.scheduling_cancelado);
                break;
            //todo - liberar para avaliar caso de cancelamento com alguns parametros, como tempo para cancelar por exemplo
            case AppConstants.STATUS_NOSHOW:
                holder.ordStatus.setText(R.string.nao_compareceu);
                break;
        }
        holder.ordId.setText(ordList.get(position).getScheduling_id());
        holder.stoNome.setText(ordList.get(position).getCompany_name());
        Util.hasPhoto(ordList.get(position),holder.img);
        holder.ageData.setText(Util.timestampFormatDayMonthYear(ordList.get(position).getCreated_date()));
        holder.ageDataInicio.setText(Util.timestampFormatDayMonthYear(ordList.get(position).getStart_date()));
        holder.ageHoraInicio.setText(holder.ageHoraInicio.getContext().getResources().getString(R.string.time_with_dots, ordList.get(position).getStart_time(), ordList.get(position).getEnd_time()));
    }

    @Override
    public int getItemCount() {
        if (null == ordList)
            return 0;
        return ordList.size();
    }

    public void setOrder(List<Scheduling> order) {
        ordList = order;
        notifyDataSetChanged();
    }


    public interface RvAgendamentosOnClickListener {
        void onClick(Scheduling order);
        void onClickAvaliar(Scheduling order);
    }

}

