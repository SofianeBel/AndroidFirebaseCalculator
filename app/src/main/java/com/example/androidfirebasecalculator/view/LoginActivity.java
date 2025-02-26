package com.example.androidfirebasecalculator.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidfirebasecalculator.R;
import com.example.androidfirebasecalculator.viewmodel.LoginViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private static final int RC_SIGN_IN_FIREBASE_UI = 9002;
    
    private GoogleSignInClient mGoogleSignInClient;
    private LoginViewModel viewModel;
    
    private MaterialButton googleSignInButton;
    private MaterialButton githubSignInButton;
    private MaterialButton microsoftSignInButton;
    private MaterialButton phoneSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialiser le ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        
        // Vérifier si l'utilisateur est déjà connecté
        if (viewModel.isUserLoggedIn()) {
            goToMainActivity();
            return;
        }
        
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        
        // Initialize buttons
        googleSignInButton = findViewById(R.id.googleSignInButton);
        githubSignInButton = findViewById(R.id.githubSignInButton);
        microsoftSignInButton = findViewById(R.id.microsoftSignInButton);
        phoneSignInButton = findViewById(R.id.phoneSignInButton);
        
        // Set click listeners
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        githubSignInButton.setOnClickListener(v -> signInWithGithub());
        microsoftSignInButton.setOnClickListener(v -> signInWithMicrosoft());
        phoneSignInButton.setOnClickListener(v -> signInWithPhone());
        
        // Observer les changements dans le ViewModel
        observeViewModel();
    }
    
    private void observeViewModel() {
        // Observer l'état de chargement
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Vous pouvez ajouter un indicateur de chargement ici si nécessaire
            setButtonsEnabled(!isLoading);
        });
        
        // Observer les messages d'erreur
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observer l'utilisateur actuel
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                goToMainActivity();
            }
        });
    }
    
    private void setButtonsEnabled(boolean enabled) {
        googleSignInButton.setEnabled(enabled);
        githubSignInButton.setEnabled(enabled);
        microsoftSignInButton.setEnabled(enabled);
        phoneSignInButton.setEnabled(enabled);
    }
    
    // Google Sign In
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }
    
    // GitHub Sign In
    private void signInWithGithub() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GitHubBuilder().build());
        
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN_FIREBASE_UI);
    }
    
    // Microsoft Sign In
    private void signInWithMicrosoft() {
        viewModel.signInWithMicrosoft(this).observe(this, success -> {
            if (success) {
                goToMainActivity();
            }
        });
    }
    
    // Phone Sign In
    private void signInWithPhone() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build());
        
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN_FIREBASE_UI);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(this, "Erreur de connexion Google: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RC_SIGN_IN_FIREBASE_UI) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                goToMainActivity();
            } else {
                // Sign in failed
                if (response != null) {
                    Toast.makeText(this, "Erreur de connexion: " + response.getError().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        viewModel.signInWithGoogle(account).observe(this, success -> {
            if (success) {
                goToMainActivity();
            }
        });
    }
    
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
} 