package com.example.androidfirebasecalculator.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfirebasecalculator.R;
import com.example.androidfirebasecalculator.adapter.CalculationAdapter;
import com.example.androidfirebasecalculator.viewmodel.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";

    private RecyclerView rvHistory;
    private TextView tvNoHistory;
    private CalculationAdapter adapter;
    private HistoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialiser le ViewModel
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        // Vérifier si l'utilisateur est connecté
        if (!viewModel.isUserLoggedIn()) {
            Toast.makeText(this, "Vous devez être connecté pour voir l'historique", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialiser les vues
        rvHistory = findViewById(R.id.rvHistory);
        tvNoHistory = findViewById(R.id.tvNoHistory);

        // Configurer le RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CalculationAdapter();
        rvHistory.setAdapter(adapter);

        // Observer les changements dans le ViewModel
        observeViewModel();
    }

    private void observeViewModel() {
        // Observer les calculs
        viewModel.getCalculations().observe(this, calculations -> {
            if (calculations.isEmpty()) {
                tvNoHistory.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
            } else {
                tvNoHistory.setVisibility(View.GONE);
                rvHistory.setVisibility(View.VISIBLE);
                adapter.setCalculations(calculations);
            }
        });
        
        // Observer l'état de chargement
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Vous pouvez ajouter un indicateur de chargement ici si nécessaire
        });
        
        // Observer les messages d'erreur
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                tvNoHistory.setText("Erreur: " + errorMessage);
                tvNoHistory.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        try {
            // Déconnecter l'utilisateur
            viewModel.logout();
            
            // Afficher un message de confirmation
            Toast.makeText(this, "Vous avez été déconnecté", Toast.LENGTH_SHORT).show();
            
            // Rediriger vers l'écran de connexion
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de la déconnexion : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
} 