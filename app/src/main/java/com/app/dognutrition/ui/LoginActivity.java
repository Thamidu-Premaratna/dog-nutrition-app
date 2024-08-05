package com.app.dognutrition.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.app.dognutrition.HomeActivity;
import com.app.dognutrition.R;
import com.app.dognutrition.utils.SQLiteUtils;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvRegister;
    private SQLiteUtils dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        // getting the data which is stored in shared preferences.
        SharedPreferences sharedpreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Getting the value of email from shared preferences if available else return null
        String email = sharedpreferences.getString("email", null);

        // check if the fields are not null then one current user is loggedIn therefore no need to re-login
        if (email != null) {
            System.out.println("LoginActivity: User already logged in");
            finish();
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
        }

        // Initialize SQLiteUtils
        dbHelper = new SQLiteUtils(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    etEmail.setError("Email is required");
                    etEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }
                System.out.println("LoginActivity: email: " + email + ", password: " + password);
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("LoginActivity: Connecting to MySQL database...");
                        SQLiteDatabase db = dbHelper.getConnection();

                        Boolean isAuthenticated = dbHelper.authenticateUser(email, password);
                        if(isAuthenticated) {
                            System.out.println("LoginActivity: Login successful");

                            // Save user details in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", email);
                            editor.apply();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    btnLogin.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    btnLogin.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // End current activity
                finish();
                // Start RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
