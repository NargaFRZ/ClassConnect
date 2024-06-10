package com.classconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.model.BaseResponse;
import com.classconnect.model.Classes;
import com.classconnect.request.ClassRequest;
import com.classconnect.request.UtilsApi;
import com.classconnect.request.BaseApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateClassActivity extends AppCompatActivity {

    private EditText edtClassName, edtClassDescription, edtEnrollmentKey;
    private Button btnCreateClass;
    private ProgressBar progressBar;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        edtClassName = findViewById(R.id.edtClassName);
        edtClassDescription = findViewById(R.id.edtClassDescription);
        edtEnrollmentKey = findViewById(R.id.edtEnrollmentKey);
        btnCreateClass = findViewById(R.id.btnCreateClass);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();

        btnCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtClassName.getText().toString();
                String description = edtClassDescription.getText().toString();
                String enrollmentKey = edtEnrollmentKey.getText().toString();

                if (!name.isEmpty() && !description.isEmpty() && !enrollmentKey.isEmpty()) {
                    String userId = LoginActivity.loggedAccount.user_id.toString();
                    createClass(name, description, enrollmentKey, userId);
                } else {
                    Toast.makeText(CreateClassActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createClass(String name, String description, String enrollmentKey, String userId) {
        progressBar.setVisibility(View.VISIBLE);

        ClassRequest classRequest = new ClassRequest(name, description, enrollmentKey, userId);

        mApiService.createClass(classRequest).enqueue(new Callback<BaseResponse<Classes>>() {
            @Override
            public void onResponse(Call<BaseResponse<Classes>> call, Response<BaseResponse<Classes>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Classes> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(CreateClassActivity.this, "Class created successfully", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity
                    } else {
                        Toast.makeText(CreateClassActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateClassActivity.this, "Failed to create class", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Classes>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CreateClassActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
