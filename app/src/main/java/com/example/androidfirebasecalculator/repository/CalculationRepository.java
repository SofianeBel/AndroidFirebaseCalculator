package com.example.androidfirebasecalculator.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidfirebasecalculator.model.Calculation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository pour gérer les calculs avec Firestore
 */
public class CalculationRepository {
    private static final String TAG = "CalculationRepository";
    private static final String CALCULATIONS_PATH = "calculations";
    private static final String COLLECTION_CALCULATIONS = "calculations";
    
    private final DatabaseReference databaseRef;
    private final MutableLiveData<List<Calculation>> calculationsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    private final FirebaseFirestore db;
    private final UserRepository userRepository;
    
    public CalculationRepository() {
        databaseRef = FirebaseDatabase.getInstance().getReference(CALCULATIONS_PATH);
        db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository();
    }
    
    /**
     * Vérifie si l'utilisateur est connecté
     * @return true si l'utilisateur est connecté, false sinon
     */
    public boolean isUserLoggedIn() {
        return userRepository.isUserLoggedIn();
    }
    
    /**
     * Récupère l'utilisateur actuellement connecté
     * @return l'utilisateur actuel ou null s'il n'est pas connecté
     */
    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }
    
    /**
     * Déconnecte l'utilisateur actuel
     */
    public void logout() {
        Log.d(TAG, "Déconnexion de l'utilisateur");
        userRepository.logout();
    }
    
    /**
     * Enregistre un calcul dans Firestore
     * @param calculation le calcul à enregistrer
     * @return Task indiquant si l'opération a réussi
     */
    public Task<Void> saveCalculation(Calculation calculation) {
        if (!userRepository.isUserLoggedIn()) {
            return Tasks.forException(new Exception("Utilisateur non connecté"));
        }
        
        CollectionReference calculationsRef = db.collection(COLLECTION_CALCULATIONS);
        return calculationsRef.add(calculation)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Calcul enregistré avec succès");
                    } else {
                        Log.e(TAG, "Erreur lors de l'enregistrement du calcul", task.getException());
                    }
                    return null;
                });
    }
    
    /**
     * Récupère les calculs d'un utilisateur
     * @param userId l'identifiant de l'utilisateur
     * @return une LiveData contenant la liste des calculs
     */
    public LiveData<List<Calculation>> getCalculationsForUser(String userId) {
        MutableLiveData<List<Calculation>> calculationsLiveData = new MutableLiveData<>();
        
        if (!userRepository.isUserLoggedIn()) {
            calculationsLiveData.setValue(new ArrayList<>());
            return calculationsLiveData;
        }
        
        db.collection(COLLECTION_CALCULATIONS)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Calculation> calculations = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Calculation calculation = document.toObject(Calculation.class);
                        calculation.setId(document.getId());
                        calculations.add(calculation);
                    }
                    calculationsLiveData.setValue(calculations);
                })
                .addOnFailureListener(e -> {
                    // Erreur lors de la récupération
                    calculationsLiveData.setValue(new ArrayList<>());
                });
        
        return calculationsLiveData;
    }
    
    /**
     * Supprime un calcul
     * @param calculationId l'identifiant du calcul à supprimer
     */
    public void deleteCalculation(String calculationId) {
        if (!userRepository.isUserLoggedIn()) {
            return;
        }
        
        db.collection(COLLECTION_CALCULATIONS).document(calculationId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Calcul supprimé avec succès
                })
                .addOnFailureListener(e -> {
                    // Erreur lors de la suppression
                });
    }
    
    /**
     * Charge les calculs de l'utilisateur actuel
     * @return LiveData contenant la liste des calculs
     */
    public LiveData<List<Calculation>> loadCalculations() {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Impossible de charger les calculs : utilisateur non connecté");
            errorMessage.setValue("Utilisateur non connecté");
            calculationsLiveData.setValue(new ArrayList<>());
            return calculationsLiveData;
        }
        
        isLoading.setValue(true);
        
        // Récupérer les calculs de l'utilisateur triés par timestamp
        com.google.firebase.database.Query query = databaseRef.child(user.getUid()).orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                
                calculationsLiveData.setValue(calculations);
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Erreur lors de la récupération des calculs: " + databaseError.getMessage(), databaseError.toException());
                errorMessage.setValue("Erreur lors du chargement : " + databaseError.getMessage());
                calculationsLiveData.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
        
        return calculationsLiveData;
    }
    
    /**
     * Retourne l'état de chargement
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * Retourne les messages d'erreur
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
} 