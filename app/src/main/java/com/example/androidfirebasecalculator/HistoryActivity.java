package com.example.androidfirebasecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfirebasecalculator.adapter.CalculationAdapter;
import com.example.androidfirebasecalculator.model.Calculation;
import com.example.androidfirebasecalculator.model.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private static final String TAG = "HistoryActivity";

    private RecyclerView rvHistory;
    private TextView tvNoHistory;
    private CalculationAdapter adapter;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialiser FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Vérifier si l'utilisateur est connecté
        if (!firebaseHelper.isUserLoggedIn()) {
            Toast.makeText(this, "Vous devez être connecté pour voir l'historique", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Utilisateur non connecté, fermeture de l'activité");
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

        // Charger l'historique des calculs
        loadCalculationHistory();
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

    private void loadCalculationHistory() {
        // Récupérer la référence aux calculs de l'utilisateur
        DatabaseReference userCalculationsRef = firebaseHelper.getUserCalculationsRef();
        if (userCalculationsRef == null) {
            tvNoHistory.setText("Erreur: Impossible d'accéder à la base de données");
            tvNoHistory.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
            Log.e(TAG, "Référence aux calculs de l'utilisateur null");
            return;
        }

        // Récupérer les calculs de l'utilisateur triés par timestamp
        Query query = userCalculationsRef.orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Calculation> calculations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Calculation calculation = snapshot.getValue(Calculation.class);
                    if (calculation != null) {
                        calculations.add(calculation);
                        Log.d(TAG, "Calcul récupéré: " + calculation.getFullCalculation());
                    }
                }

                // Trier par ordre décroissant (plus récent en premier)
                Collections.reverse(calculations);
                Log.d(TAG, "Nombre de calculs récupérés: " + calculations.size());

                // Mettre à jour l'interface utilisateur
                if (calculations.isEmpty()) {
                    tvNoHistory.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                    Log.d(TAG, "Aucun calcul trouvé");
                } else {
                    tvNoHistory.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                    adapter.setCalculations(calculations);
                    Log.d(TAG, "Affichage des calculs");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer l'erreur
                tvNoHistory.setText("Erreur: " + databaseError.getMessage());
                tvNoHistory.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
                Log.e(TAG, "Erreur lors de la récupération des calculs: " + databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    private void logout() {
        try {
            // Déconnecter l'utilisateur
            firebaseHelper.logout();
            
            // Afficher un message de confirmation
            Toast.makeText(this, "Vous avez été déconnecté", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Utilisateur déconnecté avec succès");
            
            // Rediriger vers l'écran de connexion
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la déconnexion : " + e.getMessage(), e);
            Toast.makeText(this, "Erreur lors de la déconnexion : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}