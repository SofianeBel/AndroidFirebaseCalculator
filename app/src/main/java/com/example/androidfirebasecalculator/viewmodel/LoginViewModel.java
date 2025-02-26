package com.example.androidfirebasecalculator.viewmodel;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidfirebasecalculator.repository.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel pour la gestion de l'authentification
 */
public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;
    
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    
    public LoginViewModel() {
        userRepository = new UserRepository();
        
        // Vérifier si l'utilisateur est déjà connecté
        FirebaseUser user = userRepository.getCurrentUser();
        if (user != null) {
            currentUser.setValue(user);
        }
    }
    
    /**
     * Authentifie l'utilisateur avec Google
     * @param account le compte Google
     * @return LiveData contenant true si l'opération a réussi, false sinon
     */
    public LiveData<Boolean> signInWithGoogle(GoogleSignInAccount account) {
        isLoading.setValue(true);
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        userRepository.firebaseAuthWithGoogle(account)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = userRepository.getCurrentUser();
                        currentUser.setValue(user);
                        result.setValue(true);
                    } else {
                        errorMessage.setValue("Échec de l'authentification Google: " + 
                                (task.getException() != null ? task.getException().getMessage() : "Erreur inconnue"));
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
        isLoading.setValue(true);
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        userRepository.signInWithMicrosoft(activity)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = userRepository.getCurrentUser();
                        currentUser.setValue(user);
                        result.setValue(true);
                    } else {
                        errorMessage.setValue("Échec de l'authentification Microsoft: " + 
                                (task.getException() != null ? task.getException().getMessage() : "Erreur inconnue"));
                        result.setValue(false);
                    }
                });
        
        return result;
    }
    
    /**
     * Vérifie si l'utilisateur est connecté
     * @return true si l'utilisateur est connecté, false sinon
     */
    public boolean isUserLoggedIn() {
        return userRepository.isUserLoggedIn();
    }
    
    /**
     * Récupère l'utilisateur actuel
     * @return LiveData contenant l'utilisateur actuel
     */
    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
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