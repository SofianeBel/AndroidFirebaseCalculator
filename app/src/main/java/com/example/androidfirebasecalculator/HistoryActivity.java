package com.example.androidfirebasecalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfirebasecalculator.adapter.CalculationAdapter;
import com.example.androidfirebasecalculator.model.Calculation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvNoHistory;
    private CalculationAdapter adapter;
    private DatabaseReference calculationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialiser Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        calculationsRef = database.getReference("calculations");

        // Initialiser les vues
        rvHistory = findViewById(R.id.rvHistory);
        tvNoHistory = findViewById(R.id.tvNoHistory);

        // Configurer le RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CalculationAdapter();
        rvHistory.setAdapter(adapter);

        // Charger l'historique des calculs
        loadCalculationHistory();
    }

    private void loadCalculationHistory() {
        // Récupérer les calculs triés par timestamp (du plus récent au plus ancien)
        Query query = calculationsRef.orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Calculation> calculations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Calculation calculation = snapshot.getValue(Calculation.class);
                    if (calculation != null) {
                        calculations.add(calculation);
                    }
                }

                // Trier par ordre décroissant (plus récent en premier)
                Collections.reverse(calculations);

                // Mettre à jour l'interface utilisateur
                if (calculations.isEmpty()) {
                    tvNoHistory.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                } else {
                    tvNoHistory.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                    adapter.setCalculations(calculations);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer l'erreur
                tvNoHistory.setText(databaseError.getMessage());
                tvNoHistory.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
            }
        });
    }
}