package com.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.model.BaseResponse;
import com.classconnect.model.Grades;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UtilsApi;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentProfileActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvRecentGrades;
    private Button btnSignOut;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvRecentGrades = findViewById(R.id.tvRecentGrades);
        btnSignOut = findViewById(R.id.btnSignOut);

        mApiService = UtilsApi.getApiService();

        if (LoginActivity.loggedAccount != null) {
            tvName.setText(LoginActivity.loggedAccount.name);
            tvUsername.setText(LoginActivity.loggedAccount.username);
            tvEmail.setText(LoginActivity.loggedAccount.email);
            fetchRecentGrades(LoginActivity.loggedAccount.user_id);
        } else {
            tvName.setText("N/A");
            tvUsername.setText("N/A");
            tvEmail.setText("N/A");
        }

        btnSignOut.setOnClickListener(v -> {
            LoginActivity.loggedAccount = null;
            startActivity(new Intent(StudentProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void fetchRecentGrades(UUID studentId) {
        mApiService.getRecentGrades(studentId.toString()).enqueue(new Callback<BaseResponse<List<Grades>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Grades>>> call, Response<BaseResponse<List<Grades>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Grades>> baseResponse = response.body();
                    if (baseResponse.success) {
                        List<Grades> recentGrades = baseResponse.payload;
                        StringBuilder gradesText = new StringBuilder();
                        for (Grades grade : recentGrades) {
                            gradesText.append("Assignment: ").append(grade.assignment_id)
                                    .append("\nScore: ").append(grade.score)
                                    .append("\nFeedback: ").append(grade.feedback)
                                    .append("\n\n");
                        }
                        tvRecentGrades.setText(gradesText.toString());
                    } else {
                        tvRecentGrades.setText("No recent grades available.");
                    }
                } else {
                    tvRecentGrades.setText("Failed to fetch recent grades.");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Grades>>> call, Throwable t) {
                tvRecentGrades.setText("An error occurred: " + t.getMessage());
            }
        });
    }

}
