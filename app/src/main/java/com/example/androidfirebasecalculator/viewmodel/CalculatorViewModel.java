package com.example.androidfirebasecalculator.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidfirebasecalculator.model.Calculation;
import com.example.androidfirebasecalculator.repository.CalculationRepository;
import com.example.androidfirebasecalculator.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * ViewModel pour la calculatrice
 */
public class CalculatorViewModel extends ViewModel {
    private static final String TAG = "CalculatorViewModel";
    
    private final CalculationRepository calculationRepository;
    private final UserRepository userRepository;
    
    // État de la calculatrice
    private final MutableLiveData<String> currentInput = new MutableLiveData<>("");
    private final MutableLiveData<String> currentOperation = new MutableLiveData<>("");
    private final MutableLiveData<Double> firstOperand = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> result = new MutableLiveData<>("0");
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    
    private boolean isNewCalculation = true;
    
    // Formatteur pour les nombres décimaux
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##########");
    
    public CalculatorViewModel() {
        calculationRepository = new CalculationRepository();
        userRepository = new UserRepository();
    }
    
    /**
     * Ajoute un chiffre au nombre actuel
     * @param number le chiffre à ajouter
     */
    public void appendNumber(String number) {
        if (isNewCalculation) {
            currentInput.setValue(number);
            isNewCalculation = false;
        } else {
            String current = currentInput.getValue();
            if (current == null || current.equals("0")) {
                currentInput.setValue(number);
            } else {
                currentInput.setValue(current + number);
            }
        }
        
        // Mettre à jour le résultat en temps réel
        result.setValue(currentInput.getValue());
    }
    
    /**
     * Ajoute un point décimal au nombre actuel
     */
    public void appendDecimal() {
        String current = currentInput.getValue();
        if (current == null || current.isEmpty()) {
            currentInput.setValue("0.");
        } else if (!current.contains(".")) {
            currentInput.setValue(current + ".");
        }
        
        // Mettre à jour le résultat en temps réel
        result.setValue(currentInput.getValue());
        isNewCalculation = false;
    }
    
    /**
     * Définit l'opération à effectuer
     * @param operation l'opération (+, -, ×, ÷)
     */
    public void setOperation(String operation) {
        try {
            if (currentInput.getValue() != null && !currentInput.getValue().isEmpty()) {
                if (currentOperation.getValue() != null && !currentOperation.getValue().isEmpty()) {
                    // Si une opération est déjà en cours, calculer le résultat intermédiaire
                    calculateResult();
                }
                
                firstOperand.setValue(Double.parseDouble(currentInput.getValue()));
                currentOperation.setValue(operation);
                currentInput.setValue("");
            } else if (result.getValue() != null && !result.getValue().isEmpty() && !result.getValue().equals("0")) {
                // Utiliser le résultat précédent comme premier opérande
                firstOperand.setValue(Double.parseDouble(result.getValue()));
                currentOperation.setValue(operation);
                currentInput.setValue("");
            }
        } catch (NumberFormatException e) {
            setError("Format de nombre invalide");
        }
    }
    
    /**
     * Calcule le résultat de l'opération
     */
    public void calculateResult() {
        try {
            if (currentOperation.getValue() == null || currentOperation.getValue().isEmpty()) {
                return;
            }
            
            if (currentInput.getValue() == null || currentInput.getValue().isEmpty()) {
                return;
            }
            
            double first = firstOperand.getValue();
            double second = Double.parseDouble(currentInput.getValue());
            double resultValue = 0;
            
            switch (currentOperation.getValue()) {
                case "+":
                    resultValue = first + second;
                    break;
                case "-":
                    resultValue = first - second;
                    break;
                case "*":
                    resultValue = first * second;
                    break;
                case "/":
                    if (second == 0) {
                        setError("Division par zéro");
                        return;
                    }
                    resultValue = first / second;
                    break;
            }
            
            // Formater le résultat
            String formattedResult = formatResult(resultValue);
            result.setValue(formattedResult);
            
            // Réinitialiser pour une nouvelle opération
            currentInput.setValue("");
            currentOperation.setValue("");
            isNewCalculation = true;
            isError.setValue(false);
            
        } catch (NumberFormatException e) {
            setError("Format de nombre invalide");
        } catch (Exception e) {
            setError("Erreur de calcul: " + e.getMessage());
        }
    }
    
    /**
     * Réinitialise la calculatrice
     */
    public void clear() {
        currentInput.setValue("");
        currentOperation.setValue("");
        firstOperand.setValue(0.0);
        result.setValue("0");
        isError.setValue(false);
        errorMessage.setValue("");
        isNewCalculation = true;
    }
    
    /**
     * Supprime le dernier caractère du nombre actuel
     */
    public void delete() {
        String current = currentInput.getValue();
        if (current != null && !current.isEmpty()) {
            currentInput.setValue(current.substring(0, current.length() - 1));
            // Mettre à jour le résultat en temps réel
            if (currentInput.getValue().isEmpty()) {
                result.setValue("0");
            } else {
                result.setValue(currentInput.getValue());
            }
        }
    }
    
    /**
     * Enregistre le calcul actuel dans Firebase
     * @return LiveData<Boolean> indiquant si l'opération a réussi
     */
    public LiveData<Boolean> saveCalculation() {
        MutableLiveData<Boolean> saveResult = new MutableLiveData<>();
        
        if (result.getValue() != null && !result.getValue().equals("0") && !isError.getValue()) {
            FirebaseUser user = userRepository.getCurrentUser();
            if (user != null) {
                String expression = buildExpression();
                String resultValue = this.result.getValue();
                
                Calculation calculation = new Calculation();
                calculation.setUserId(user.getUid());
                calculation.setExpression(expression);
                calculation.setResult(resultValue);
                calculation.setTimestamp(new Date());
                
                calculationRepository.saveCalculation(calculation)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            saveResult.setValue(true);
                        } else {
                            Log.e(TAG, "Erreur lors de l'enregistrement du calcul", task.getException());
                            setError("Erreur lors de l'enregistrement du calcul: " + 
                                    (task.getException() != null ? task.getException().getMessage() : "Erreur inconnue"));
                            saveResult.setValue(false);
                        }
                    });
            } else {
                saveResult.setValue(false);
                setError("Vous devez être connecté pour enregistrer un calcul");
            }
        } else {
            saveResult.setValue(false);
            if (isError.getValue()) {
                setError("Impossible d'enregistrer un calcul en erreur");
            } else {
                setError("Aucun calcul à enregistrer");
            }
        }
        
        return saveResult;
    }
    
    private String buildExpression() {
        StringBuilder expression = new StringBuilder();
        if (firstOperand.getValue() != null) {
            expression.append(formatResult(firstOperand.getValue()));
        }
        if (currentOperation.getValue() != null && !currentOperation.getValue().isEmpty()) {
            expression.append(" ").append(currentOperation.getValue()).append(" ");
        }
        if (currentInput.getValue() != null && !currentInput.getValue().isEmpty()) {
            expression.append(currentInput.getValue());
        }
        return expression.toString();
    }
    
    /**
     * Formate un nombre pour l'affichage
     */
    private String formatResult(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return decimalFormat.format(value);
        }
    }
    
    private void setError(String message) {
        isError.setValue(true);
        errorMessage.setValue(message);
        result.setValue("Error");
        isNewCalculation = true;
    }
    
    /**
     * Déconnecte l'utilisateur
     */
    public void logout() {
        userRepository.logout();
    }
    
    /**
     * Vérifie si l'utilisateur est connecté
     */
    public boolean isUserLoggedIn() {
        return userRepository.isUserLoggedIn();
    }
    
    // Getters pour les LiveData
    public LiveData<String> getCurrentInput() {
        return currentInput;
    }
    
    public LiveData<String> getCurrentOperation() {
        return currentOperation;
    }
    
    public LiveData<String> getResult() {
        return result;
    }
    
    public LiveData<Boolean> getIsError() {
        return isError;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Retourne le résultat à afficher
     * @return LiveData contenant le résultat formaté
     */
    public LiveData<String> getDisplayResult() {
        return result;
    }
    
    /**
     * Retourne l'opération à afficher
     * @return LiveData contenant l'opération formatée
     */
    public LiveData<String> getDisplayOperation() {
        MutableLiveData<String> displayOperation = new MutableLiveData<>();
        
        if (firstOperand.getValue() != null && currentOperation.getValue() != null && !currentOperation.getValue().isEmpty()) {
            String formattedOperand = formatResult(firstOperand.getValue());
            displayOperation.setValue(formattedOperand + " " + currentOperation.getValue());
        } else {
            displayOperation.setValue("");
        }
        
        return displayOperation;
    }
    
    /**
     * Réinitialise la calculatrice (alias pour clear())
     */
    public void resetCalculator() {
        clear();
    }
    
    /**
     * Supprime le dernier caractère (alias pour delete())
     */
    public void deleteLastCharacter() {
        delete();
    }
} 