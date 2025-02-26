package com.example.androidfirebasecalculator.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidfirebasecalculator.model.Calculation;
import com.example.androidfirebasecalculator.repository.CalculationRepository;
import com.example.androidfirebasecalculator.repository.UserRepository;

import java.util.List;

/**
 * ViewModel pour l'historique des calculs
 */
public class HistoryViewModel extends ViewModel {
    private final CalculationRepository calculationRepository;
    private final UserRepository userRepository;
    
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private LiveData<List<Calculation>> calculations;
    
    public HistoryViewModel() {
        calculationRepository = new CalculationRepository();
        userRepository = new UserRepository();
        
        // Charger les calculs si l'utilisateur est connecté
        if (isUserLoggedIn()) {
            loadCalculations();
        }
    }
    
    /**
     * Charge les calculs de l'utilisateur
     */
    public void loadCalculations() {
        isLoading.setValue(true);
        
        String userId = userRepository.getCurrentUser().getUid();
        calculations = calculationRepository.getCalculationsForUser(userId);
        
        isLoading.setValue(false);
    }
    
    /**
     * Retourne les calculs
     */
    public LiveData<List<Calculation>> getCalculations() {
        if (calculations == null) {
            calculations = new MutableLiveData<>();
        }
        return calculations;
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
    
    /**
     * Vérifie si l'utilisateur est connecté
     */
    public boolean isUserLoggedIn() {
        return userRepository.isUserLoggedIn();
    }
    
    /**
     * Déconnecte l'utilisateur
     */
    public void logout() {
        userRepository.logout();
    }
} 