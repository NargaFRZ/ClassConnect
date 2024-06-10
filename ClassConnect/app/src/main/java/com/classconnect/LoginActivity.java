package com.classconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.model.Account;
import com.classconnect.model.BaseResponse;
import com.classconnect.model.Roles;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.LoginRequest;
import com.classconnect.request.UtilsApi;
import com.classconnect.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static Account loggedAccount;
    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();

        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();
            if (!username.isEmpty() && !password.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                LoginRequest loginRequest = new LoginRequest(username, password);
                login(loginRequest);
            } else {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            moveActivity(this, RegisterActivity.class);
            viewToast(this, "Register Account");
        });
    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    private void login(LoginRequest loginRequest) {
        mApiService.login(loginRequest).enqueue(new Callback<BaseResponse<Account>>() {
            @Override
            public void onResponse(Call<BaseResponse<Account>> call, Response<BaseResponse<Account>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Account> baseResponse = response.body();
                    Log.d("LoginActivity", "Response received: " + baseResponse.toString());
                    if (baseResponse.success) {
                        Log.d("LoginActivity", "Login successful");
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        loggedAccount = baseResponse.payload;
                        navigateBasedOnRole(loggedAccount.role);
                    } else {
                        String message = baseResponse.message;
                        if (message == null || message.isEmpty()) {
                            message = "Login failed";
                        }
                        Log.d("LoginActivity", "Login failed: " + message);
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("LoginActivity", "Response is not successful");
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Account>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("LoginActivity", "An error occurred: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateBasedOnRole(Roles role) {
        Intent intent;
        switch (role) {
            case student:
                intent = new Intent(this, StudentActivity.class);
                break;
            case teacher:
                intent = new Intent(this, TeacherActivity.class);
                break;
            case admin:
                intent = new Intent(this, AdminActivity.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + role);
        }
        startActivity(intent);
        finish();
    }
}
