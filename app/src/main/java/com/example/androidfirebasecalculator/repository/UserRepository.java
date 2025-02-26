package com.example.androidfirebasecalculator.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import android.app.Activity;

/**
 * Repository pour gérer l'authentification des utilisateurs
 */
public class UserRepository {
    private static final String TAG = "UserRepository";

    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        
        // Observer les changements d'état de l'authentification
        firebaseAuth.addAuthStateListener(auth -> {
            userLiveData.setValue(auth.getCurrentUser());
        });
    }

    /**
     * Vérifie si un utilisateur est connecté
     * @return true si un utilisateur est connecté, false sinon
     */
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    /**
     * Récupère l'utilisateur actuellement connecté
     * @return l'utilisateur actuel ou null s'il n'est pas connecté
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
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
     * @return une tâche pour suivre l'opération
     */
    public Task<AuthResult> firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        return firebaseAuth.signInWithCredential(credential);
    }

    /**
     * Authentifie l'utilisateur avec Microsoft
     * @param activity l'activité appelante
     * @return une tâche pour suivre l'opération
     */
    public Task<AuthResult> signInWithMicrosoft(Activity activity) {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
        return firebaseAuth.startActivityForSignInWithProvider(activity, provider.build());
    }

    /**
     * Récupère l'état de chargement
     * @return LiveData contenant l'état de chargement
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Récupère les messages d'erreur
     * @return LiveData contenant les messages d'erreur
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
} 