package com.example.androidfirebasecalculator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfirebasecalculator.R;
import com.example.androidfirebasecalculator.model.Calculation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptateur pour afficher les calculs dans un RecyclerView
 */
public class CalculationAdapter extends RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder> {
    private List<Calculation> calculations = new ArrayList<>();
    private OnCalculationClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    
    /**
     * Interface pour gérer les clics sur les calculs
     */
    public interface OnCalculationClickListener {
        void onCalculationClick(Calculation calculation);
        void onCalculationLongClick(Calculation calculation);
    }
    
    /**
     * Définit la liste des calculs à afficher
     * @param calculations la liste des calculs
     */
    public void setCalculations(List<Calculation> calculations) {
        this.calculations = calculations;
        notifyDataSetChanged();
    }
    
    /**
     * Définit le listener pour les clics sur les calculs
     * @param listener le listener
     */
    public void setOnCalculationClickListener(OnCalculationClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CalculationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calculation, parent, false);
        return new CalculationViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CalculationViewHolder holder, int position) {
        Calculation calculation = calculations.get(position);
        holder.bind(calculation);
    }
    
    @Override
    public int getItemCount() {
        return calculations.size();
    }
    
    /**
     * ViewHolder pour les calculs
     */
    class CalculationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvExpression;
        private final TextView tvResult;
        private final TextView tvDate;
        
        public CalculationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpression = itemView.findViewById(R.id.tvExpression);
            tvResult = itemView.findViewById(R.id.tvResult);
            tvDate = itemView.findViewById(R.id.tvDate);
            
            // Configurer les listeners de clic
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCalculationClick(calculations.get(position));
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCalculationLongClick(calculations.get(position));
                    return true;
                }
                return false;
            });
        }
        
        /**
         * Lie les données du calcul aux vues
         * @param calculation le calcul à afficher
         */
        public void bind(Calculation calculation) {
            tvExpression.setText(calculation.getExpression());
            tvResult.setText(calculation.getResult());
            tvDate.setText(dateFormat.format(calculation.getTimestamp()));
        }
    }
}