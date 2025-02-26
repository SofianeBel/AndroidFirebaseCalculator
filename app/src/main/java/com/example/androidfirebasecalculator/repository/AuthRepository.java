package com.example.androidfirebasecalculator.repository;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

/**
 * Repository pour gérer l'authentification des utilisateurs
 */
public class AuthRepository {
    private static final String TAG = "AuthRepository";
    
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        
        // Observer les changements d'état de l'authentification
        firebaseAuth.addAuthStateListener(auth -> {
            userLiveData.setValue(auth.getCurrentUser());
        });
    }
    
    /**
     * Vérifie si l'utilisateur est connecté
     * @return true si l'utilisateur est connecté, false sinon
     */
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
    
    /**
     * Récupère l'utilisateur actuel
     * @return LiveData contenant l'utilisateur actuel
     */
    public LiveData<FirebaseUser> getCurrentUser() {
        userLiveData.setValue(firebaseAuth.getCurrentUser());
        return userLiveData;
    }
    
    /**
     * Déconnecte l'utilisateur actuel
     */
    public void logout() {
        Log.d(TAG, "Déconnexion de l'utilisateur");
        firebaseAuth.signOut();
    }
    
    /**
     * Authentifie l'utilisateur avec Google
     * @param account le compte Google
     * @return LiveData contenant true si l'opération a réussi, false sinon
     */
    public LiveData<Boolean> signInWithGoogle(GoogleSignInAccount account) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        isLoading.setValue(true);
        
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Connexion avec Google réussie");
                        result.setValue(true);
                    } else {
                        Log.e(TAG, "Erreur lors de la connexion avec Google : " + task.getException().getMessage(), task.getException());
                        errorMessage.setValue("Erreur lors de la connexion avec Google : " + task.getException().getMessage());
                        result.setValue(false);
                    }
                });
        
        return result;
    }
    
    /**
     * Authentifie l'utilisateur avec Microsoft
     * @param activity l'activité appelante
     * @return LiveData contenant true si l'opération a réussi, false sinon
     */
    public LiveData<Boolean> signInWithMicrosoft(Activity activity) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        isLoading.setValue(true);
        
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
        
        firebaseAuth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener(authResult -> {
                    isLoading.setValue(false);
                    Log.d(TAG, "Connexion avec Microsoft réussie");
                    result.setValue(true);
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    Log.e(TAG, "Erreur lors de la connexion avec Microsoft : " + e.getMessage(), e);
                    errorMessage.setValue("Erreur lors de la connexion avec Microsoft : " + e.getMessage());
                    result.setValue(false);
                });
        
        return result;
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