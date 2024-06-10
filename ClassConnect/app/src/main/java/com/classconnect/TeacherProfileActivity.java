package com.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherProfileActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnSignOut = findViewById(R.id.btnSignOut);

        // Assuming LoginActivity.loggedAccount is accessible
        if (LoginActivity.loggedAccount != null) {
            tvName.setText(LoginActivity.loggedAccount.name);
            tvUsername.setText(LoginActivity.loggedAccount.username);
            tvEmail.setText(LoginActivity.loggedAccount.email);
        } else {
            tvName.setText("N/A");
            tvUsername.setText("N/A");
            tvEmail.setText("N/A");
        }

        btnSignOut.setOnClickListener(v -> {
            LoginActivity.loggedAccount = null;
            startActivity(new Intent(TeacherProfileActivity.this, LoginActivity.class));
            finish();
        });
    }
}
