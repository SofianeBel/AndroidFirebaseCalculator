package com.example.androidfirebasecalculator.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Classe utilitaire pour gérer les opérations Firebase
 */
public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static final String CALCULATIONS_PATH = "calculations";
    
    private DatabaseReference databaseRef;
    
    public FirebaseHelper() {
        databaseRef = FirebaseDatabase.getInstance().getReference(CALCULATIONS_PATH);
    }
    
    /**
     * Vérifie si l'utilisateur est connecté
     * @return true si l'utilisateur est connecté, false sinon
     */
    public boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
    
    /**
     * Récupère l'utilisateur actuel
     * @return l'utilisateur actuel ou null s'il n'est pas connecté
     */
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    
    /**
     * Déconnecte l'utilisateur actuel
     */
    public void logout() {
        Log.d(TAG, "Déconnexion de l'utilisateur");
        FirebaseAuth.getInstance().signOut();
    }
    
    /**
     * Enregistre un calcul dans la base de données
     * @param calculation le calcul à enregistrer
     * @param successListener le listener à appeler en cas de succès
     * @param failureListener le listener à appeler en cas d'échec
     * @return true si l'opération a été initiée, false sinon
     */
    public boolean saveCalculation(Calculation calculation, 
                                  OnSuccessListener<Void> successListener,
                                  OnFailureListener failureListener) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Impossible d'enregistrer le calcul : utilisateur non connecté");
            return false;
        }
        
        String userId = user.getUid();
        calculation.setUserId(userId);
        
        // Générer une clé unique pour ce calcul
        String key = databaseRef.child(userId).push().getKey();
        if (key == null) {
            Log.e(TAG, "Impossible de générer une clé pour le calcul");
            return false;
        }
        
        calculation.setId(key);
        Log.d(TAG, "Enregistrement du calcul avec l'ID : " + key + " pour l'utilisateur : " + userId);
        
        // Enregistrer le calcul sous l'ID de l'utilisateur
        databaseRef.child(userId).child(key).setValue(calculation)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
        
        return true;
    }
    
    /**
     * Récupère la référence à la base de données pour les calculs d'un utilisateur
     * @return la référence à la base de données ou null si l'utilisateur n'est pas connecté
     */
    public DatabaseReference getUserCalculationsRef() {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Impossible de récupérer la référence : utilisateur non connecté");
            return null;
        }
        
        return databaseRef.child(user.getUid());
    }
} 