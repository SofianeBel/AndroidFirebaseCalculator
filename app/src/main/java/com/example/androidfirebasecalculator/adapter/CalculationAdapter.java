package com.example.androidfirebasecalculator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfirebasecalculator.R;
import com.example.androidfirebasecalculator.model.Calculation;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur pour afficher la liste des calculs dans un RecyclerView
 */
public class CalculationAdapter extends RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder> {

    private List<Calculation> calculations = new ArrayList<>();

    @NonNull
    @Override
    public CalculationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calculation, parent, false);
        return new CalculationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalculationViewHolder holder, int position) {
        Calculation calculation = calculations.get(position);
        holder.tvCalculation.setText(calculation.getFullCalculation());
        holder.tvTimestamp.setText(calculation.getFormattedDate());
    }

    @Override
    public int getItemCount() {
        return calculations.size();
    }

    /**
     * Met à jour la liste des calculs
     */
    public void setCalculations(List<Calculation> calculations) {
        this.calculations = calculations;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder pour les éléments de la liste
     */
    static class CalculationViewHolder extends RecyclerView.ViewHolder {
        TextView tvCalculation;
        TextView tvTimestamp;

        public CalculationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCalculation = itemView.findViewById(R.id.tvCalculation);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}