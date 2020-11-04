package com.nuppin.User.loja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.R;
import com.nuppin.Util.Util;
import com.nuppin.model.CompanySchedule;

import java.util.List;

public class AdapterScheduleDescription extends RecyclerView.Adapter<AdapterScheduleDescription.AdapterScheduleDescriptionViewHolder> {

    private List<CompanySchedule> horarios;

    AdapterScheduleDescription() {}

    class AdapterScheduleDescriptionViewHolder extends RecyclerView.ViewHolder {

        final TextView dia, horario;

        AdapterScheduleDescriptionViewHolder(View view) {
            super(view);
            dia = view.findViewById(R.id.diaSemana);
            horario = view.findViewById(R.id.horario);
        }
    }

    @NonNull
    public AdapterScheduleDescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_company_schedule;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new AdapterScheduleDescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterScheduleDescriptionViewHolder holder, int position) {
        holder.dia.setText(Util.nomeDiaSemana(horarios.get(position).getWeekday_id()));
        holder.horario.setText(holder.horario.getContext().getResources().getString(R.string.time_with_dots, horarios.get(position).getStart_time(), horarios.get(position).getEnd_time()));
    }



    @Override
    public int getItemCount() {
        if (horarios == null)
            return 0;
        return horarios.size();
    }

    void setHorarios(List horarios) {
        this.horarios = horarios;
        notifyDataSetChanged();
    }
}