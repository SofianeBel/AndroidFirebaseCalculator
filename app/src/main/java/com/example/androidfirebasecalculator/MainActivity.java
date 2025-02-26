package com.example.androidfirebasecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidfirebasecalculator.model.Calculation;
import com.example.androidfirebasecalculator.model.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvResult;
    private TextView tvOperation;

    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private Button btnAdd, btnSubtract, btnMultiply, btnDivide;
    private Button btnEquals, btnClear, btnDelete, btnDecimal;
    private Button btnHistory, btnSave, btnLogout;

    private String currentNumber = "";
    private String currentOperation = "";
    private double firstOperand = 0;
    private double secondOperand = 0;
    private boolean isOperationSelected = false;
    private boolean isCalculationComplete = false;

    private DatabaseReference calculationsRef;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##########");
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        calculationsRef = database.getReference("calculations");
        firebaseHelper = new FirebaseHelper();

        // Initialiser les vues
        initViews();
        
        // Initialiser les écouteurs d'événements
        setClickListeners();
        
        // Initialiser l'affichage
        resetCalculator();
    }

    private void initViews() {
        tvResult = findViewById(R.id.tvResult);
        tvOperation = findViewById(R.id.tvOperation);

        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);

        btnAdd = findViewById(R.id.btnAdd);
        btnSubtract = findViewById(R.id.btnSubtract);
        btnMultiply = findViewById(R.id.btnMultiply);
        btnDivide = findViewById(R.id.btnDivide);

        btnEquals = findViewById(R.id.btnEquals);
        btnClear = findViewById(R.id.btnClear);
        btnDelete = findViewById(R.id.btnDelete);
        btnDecimal = findViewById(R.id.btnDecimal);

        btnHistory = findViewById(R.id.btnHistory);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setClickListeners() {
        // Chiffres
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);

        // Opérations
        btnAdd.setOnClickListener(this);
        btnSubtract.setOnClickListener(this);
        btnMultiply.setOnClickListener(this);
        btnDivide.setOnClickListener(this);

        // Autres boutons
        btnEquals.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnDecimal.setOnClickListener(this);

        // Boutons spéciaux
        btnHistory.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // Si un calcul est terminé et qu'on appuie sur un chiffre, on réinitialise
        if (isCalculationComplete && (
                id == R.id.btn0 || id == R.id.btn1 || id == R.id.btn2 || id == R.id.btn3 ||
                id == R.id.btn4 || id == R.id.btn5 || id == R.id.btn6 || id == R.id.btn7 ||
                id == R.id.btn8 || id == R.id.btn9)) {
            resetCalculator();
        }

        // Gestion des chiffres
        if (id == R.id.btn0) appendNumber("0");
        else if (id == R.id.btn1) appendNumber("1");
        else if (id == R.id.btn2) appendNumber("2");
        else if (id == R.id.btn3) appendNumber("3");
        else if (id == R.id.btn4) appendNumber("4");
        else if (id == R.id.btn5) appendNumber("5");
        else if (id == R.id.btn6) appendNumber("6");
        else if (id == R.id.btn7) appendNumber("7");
        else if (id == R.id.btn8) appendNumber("8");
        else if (id == R.id.btn9) appendNumber("9");
        else if (id == R.id.btnDecimal) appendDecimal();

        // Gestion des opérations
        else if (id == R.id.btnAdd) setOperation("+");
        else if (id == R.id.btnSubtract) setOperation("-");
        else if (id == R.id.btnMultiply) setOperation("×");
        else if (id == R.id.btnDivide) setOperation("÷");

        // Gestion des autres boutons
        else if (id == R.id.btnEquals) calculateResult();
        else if (id == R.id.btnClear) resetCalculator();
        else if (id == R.id.btnDelete) deleteLastCharacter();
        else if (id == R.id.btnHistory) openHistory();
        else if (id == R.id.btnSave) saveCalculation();
        else if (id == R.id.btnLogout) logout();
    }

    private void appendNumber(String number) {
        if (isOperationSelected) {
            currentNumber = number;
            isOperationSelected = false;
        } else {
            // Éviter les zéros en tête
            if (currentNumber.equals("0") && !number.equals("0")) {
                currentNumber = number;
            } else if (!currentNumber.equals("0")) {
                currentNumber += number;
            }
        }
        updateDisplay();
    }

    private void appendDecimal() {
        if (isOperationSelected) {
            currentNumber = "0.";
            isOperationSelected = false;
        } else if (!currentNumber.contains(".")) {
            if (currentNumber.isEmpty()) {
                currentNumber = "0.";
            } else {
                currentNumber += ".";
            }
        }
        updateDisplay();
    }

    private void setOperation(String operation) {
        if (!currentNumber.isEmpty()) {
            if (!isOperationSelected && !currentOperation.isEmpty()) {
                calculateResult();
            }
            firstOperand = Double.parseDouble(currentNumber);
            currentOperation = operation;
            isOperationSelected = true;
            isCalculationComplete = false;
            updateDisplay();
        } else if (tvResult.getText().toString().equals("0") && operation.equals("-")) {
            // Permettre les nombres négatifs
            currentNumber = "-";
            updateDisplay();
        }
    }

    private void calculateResult() {
        if (!currentNumber.isEmpty() && !currentOperation.isEmpty() && !isCalculationComplete) {
            secondOperand = Double.parseDouble(currentNumber);
            double result = 0;

            switch (currentOperation) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "-":
                    result = firstOperand - secondOperand;
                    break;
                case "×":
                    result = firstOperand * secondOperand;
                    break;
                case "÷":
                    if (secondOperand != 0) {
                        result = firstOperand / secondOperand;
                    } else {
                        tvResult.setText("Erreur");
                        tvOperation.setText("Division par zéro");
                        isCalculationComplete = true;
                        return;
                    }
                    break;
            }

            String formattedResult = formatResult(result);
            tvResult.setText(formattedResult);
            tvOperation.setText(formatResult(firstOperand) + " " + currentOperation + " " + formatResult(secondOperand));
            currentNumber = formattedResult;
            currentOperation = "";
            isCalculationComplete = true;
        }
    }

    private String formatResult(double number) {
        if (number == (long) number) {
            return String.format("%d", (long) number);
        } else {
            return decimalFormat.format(number);
        }
    }

    private void resetCalculator() {
        currentNumber = "";
        currentOperation = "";
        firstOperand = 0;
        secondOperand = 0;
        isOperationSelected = false;
        isCalculationComplete = false;
        tvResult.setText("0");
        tvOperation.setText("");
    }

    private void deleteLastCharacter() {
        if (!currentNumber.isEmpty() && !isOperationSelected) {
            currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
            if (currentNumber.isEmpty() || currentNumber.equals("-")) {
                currentNumber = "";
                tvResult.setText("0");
            } else {
                updateDisplay();
            }
        }
    }

    private void updateDisplay() {
        if (!currentNumber.isEmpty()) {
            tvResult.setText(currentNumber);
        } else {
            tvResult.setText("0");
        }

        if (!currentOperation.isEmpty()) {
            tvOperation.setText(formatResult(firstOperand) + " " + currentOperation);
        } else {
            tvOperation.setText("");
        }
    }

    private void saveCalculation() {
        if (isCalculationComplete) {
            String expression = tvOperation.getText().toString();
            String result = tvResult.getText().toString();
            
            // Ajouter des logs pour déboguer
            Log.d("MainActivity", "Tentative d'enregistrement du calcul : " + expression + " = " + result);
            
            // Vérifier si l'utilisateur est connecté
            if (!firebaseHelper.isUserLoggedIn()) {
                Toast.makeText(MainActivity.this, "Vous devez être connecté pour enregistrer des calculs", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Erreur : Utilisateur non connecté");
                return;
            }
            
            try {
                // Créer un objet Calculation
                Calculation calculation = new Calculation(expression, result);
                
                // Enregistrer le calcul avec FirebaseHelper
                boolean saveInitiated = firebaseHelper.saveCalculation(
                    calculation,
                    aVoid -> {
                        Toast.makeText(MainActivity.this, R.string.calculation_saved, Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", "Calcul enregistré avec succès");
                    },
                    e -> {
                        Toast.makeText(MainActivity.this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("MainActivity", "Erreur lors de l'enregistrement du calcul : " + e.getMessage(), e);
                    }
                );
                
                if (!saveInitiated) {
                    Toast.makeText(MainActivity.this, "Impossible d'initier l'enregistrement", Toast.LENGTH_SHORT).show();
                    Log.e("MainActivity", "Impossible d'initier l'enregistrement");
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Exception lors de l'enregistrement : " + e.getMessage(), e);
                Toast.makeText(MainActivity.this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Effectuez un calcul complet avant de l'enregistrer", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "Erreur : Aucun calcul complet à enregistrer");
        }
    }

    private void openHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
    
    private void logout() {
        try {
            // Déconnecter l'utilisateur
            firebaseHelper.logout();
            
            // Afficher un message de confirmation
            Toast.makeText(this, "Vous avez été déconnecté", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "Utilisateur déconnecté avec succès");
            
            // Rediriger vers l'écran de connexion
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("MainActivity", "Erreur lors de la déconnexion : " + e.getMessage(), e);
            Toast.makeText(this, "Erreur lors de la déconnexion : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}