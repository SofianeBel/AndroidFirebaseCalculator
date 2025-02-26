package com.example.androidfirebasecalculator;
// Remplacez cette ligne


// Par le package correct de votre projet, probablement quelque chose comme:
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidfirebasecalculator.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private static final int RC_SIGN_IN_FIREBASE_UI = 9002;
    
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    
    private MaterialButton googleSignInButton;
    private MaterialButton githubSignInButton;
    private MaterialButton microsoftSignInButton;
    private MaterialButton phoneSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
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
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, go to main activity
            goToMainActivity();
        }
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
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
        
        mAuth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(authResult -> {
                    // Sign-in successful
                    goToMainActivity();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(LoginActivity.this, "Erreur de connexion Microsoft: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
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
                firebaseAuthWithGoogle(account.getIdToken());
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
    
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        goToMainActivity();
                    } else {
                        // Sign in fails
                        Toast.makeText(LoginActivity.this, "Authentification échouée.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Ceci ferme l'activité de connexion
    }
}