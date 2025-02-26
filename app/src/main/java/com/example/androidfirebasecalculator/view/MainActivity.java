package com.example.androidfirebasecalculator.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidfirebasecalculator.R;
import com.example.androidfirebasecalculator.viewmodel.CalculatorViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvResult;
    private TextView tvOperation;

    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private Button btnAdd, btnSubtract, btnMultiply, btnDivide;
    private Button btnEquals, btnClear, btnDelete, btnDecimal;
    private Button btnHistory, btnSave, btnLogout;

    private CalculatorViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser le ViewModel
        viewModel = new ViewModelProvider(this).get(CalculatorViewModel.class);
        
        // Vérifier si l'utilisateur est connecté
        if (!viewModel.isUserLoggedIn()) {
            // Rediriger vers l'écran de connexion
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialiser les vues
        initViews();
        
        // Initialiser les écouteurs d'événements
        setClickListeners();
        
        // Observer les changements dans le ViewModel
        observeViewModel();
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
    
    private void observeViewModel() {
        // Observer les changements d'affichage
        viewModel.getDisplayResult().observe(this, result -> {
            tvResult.setText(result);
        });
        
        viewModel.getDisplayOperation().observe(this, operation -> {
            tvOperation.setText(operation);
        });
        
        // Observer les messages d'erreur
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // Gestion des chiffres
        if (id == R.id.btn0) viewModel.appendNumber("0");
        else if (id == R.id.btn1) viewModel.appendNumber("1");
        else if (id == R.id.btn2) viewModel.appendNumber("2");
        else if (id == R.id.btn3) viewModel.appendNumber("3");
        else if (id == R.id.btn4) viewModel.appendNumber("4");
        else if (id == R.id.btn5) viewModel.appendNumber("5");
        else if (id == R.id.btn6) viewModel.appendNumber("6");
        else if (id == R.id.btn7) viewModel.appendNumber("7");
        else if (id == R.id.btn8) viewModel.appendNumber("8");
        else if (id == R.id.btn9) viewModel.appendNumber("9");
        else if (id == R.id.btnDecimal) viewModel.appendDecimal();

        // Gestion des opérations
        else if (id == R.id.btnAdd) viewModel.setOperation("+");
        else if (id == R.id.btnSubtract) viewModel.setOperation("-");
        else if (id == R.id.btnMultiply) viewModel.setOperation("×");
        else if (id == R.id.btnDivide) viewModel.setOperation("÷");

        // Gestion des autres boutons
        else if (id == R.id.btnEquals) viewModel.calculateResult();
        else if (id == R.id.btnClear) viewModel.resetCalculator();
        else if (id == R.id.btnDelete) viewModel.deleteLastCharacter();
        else if (id == R.id.btnHistory) openHistory();
        else if (id == R.id.btnSave) saveCalculation();
        else if (id == R.id.btnLogout) logout();
    }

    private void openHistory() {
        startActivity(new Intent(this, HistoryActivity.class));
    }

    private void saveCalculation() {
        viewModel.saveCalculation().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Calcul enregistré avec succès", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erreur lors de l'enregistrement du calcul", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        viewModel.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
} 