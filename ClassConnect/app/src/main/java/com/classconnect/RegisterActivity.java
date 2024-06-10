package com.classconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.model.Account;
import com.classconnect.model.BaseResponse;
import com.classconnect.model.Roles;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UtilsApi;
import com.classconnect.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtUsername, edtPassword, edtEmail;
    private Spinner spinnerRole;
    private Button btnRegister;
    private ProgressBar progressBar;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();
            String email = edtEmail.getText().toString();
            String roleString = spinnerRole.getSelectedItem().toString().toLowerCase();
            Roles role = Roles.valueOf(roleString);

            if (!name.isEmpty() && !username.isEmpty() && !password.isEmpty() && !email.isEmpty() && role != null) {
                progressBar.setVisibility(View.VISIBLE);
                Account account = new Account();
                account.name = name;
                account.username = username;
                account.password = password;
                account.email = email;
                account.role = role;
                register(account);
            } else {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        moveActivity(this, LoginActivity.class);
    }

    private void moveActivity(Context ctx, Class<?> cls){
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
        finish();
    }

    private void viewToast(Context ctx, String message){
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
    private void register(Account account) {
        mApiService.register(account).enqueue(new Callback<BaseResponse<Account>>() {
            @Override
            public void onResponse(Call<BaseResponse<Account>> call, Response<BaseResponse<Account>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Account> baseResponse = response.body();
                    Log.d("RegisterActivity", "Response received: " + baseResponse.toString());
                    if (baseResponse.success) {
                        Log.d("RegisterActivity", "Registration successful");
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = baseResponse.message;
                        if (message == null || message.isEmpty()) {
                            message = "Registration failed";
                        }
                        Log.d("RegisterActivity", "Registration failed: " + message);
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("RegisterActivity", "Response is not successful");
                    Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Account>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("RegisterActivity", "An error occurred: " + t.getMessage(), t);
                Toast.makeText(RegisterActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
