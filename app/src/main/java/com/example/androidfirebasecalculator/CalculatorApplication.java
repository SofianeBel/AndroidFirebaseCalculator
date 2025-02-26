package com.example.androidfirebasecalculator;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CalculatorApplication extends Application {
    private static final String TAG = "CalculatorApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // Initialiser Firebase
            FirebaseApp.initializeApp(this);
            
            // Activer la persistance des données hors ligne
            try {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                Log.d(TAG, "Persistance hors ligne activée");
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de l'activation de la persistance : " + e.getMessage(), e);
            }
            
            // Vérifier la connexion à la base de données
            DatabaseReference testRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            testRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        Log.d(TAG, "Connecté à Firebase Realtime Database");
                    } else {
                        Log.w(TAG, "Déconnecté de Firebase Realtime Database");
                    }
                }

                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError error) {
                    Log.e(TAG, "Erreur de connexion à Firebase : " + error.getMessage(), error.toException());
                }
            });
            
            Log.d(TAG, "Firebase initialisé avec succès");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation de Firebase : " + e.getMessage(), e);
        }
    }
} 