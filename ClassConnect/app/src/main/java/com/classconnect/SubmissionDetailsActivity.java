package com.classconnect;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.model.BaseResponse;
import com.classconnect.model.Submission;
import com.classconnect.model.Grades;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.GradeRequest;
import com.classconnect.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmissionDetailsActivity extends AppCompatActivity {

    private Button btnViewSubmission, btnGrade;
    private EditText edtScore, edtFeedback;
    private ProgressBar progressBar;
    private BaseApiService mApiService;
    private int submissionId;
    private Submission submission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_details);

        btnViewSubmission = findViewById(R.id.btnViewSubmission);
        btnGrade = findViewById(R.id.btnGrade);
        edtScore = findViewById(R.id.edtScore);
        edtFeedback = findViewById(R.id.edtFeedback);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();
        submissionId = getIntent().getIntExtra("submissionId", -1);

        fetchSubmissionDetails(submissionId);

        btnViewSubmission.setOnClickListener(v -> viewSubmission());
        btnGrade.setOnClickListener(v -> gradeSubmission());
    }

    private void fetchSubmissionDetails(int submissionId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getSubmissionById(submissionId).enqueue(new Callback<BaseResponse<Submission>>() {
            @Override
            public void onResponse(Call<BaseResponse<Submission>> call, Response<BaseResponse<Submission>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Submission> baseResponse = response.body();
                    if (baseResponse.success) {
                        submission = baseResponse.payload;
                    } else {
                        Toast.makeText(SubmissionDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SubmissionDetailsActivity.this, "Failed to fetch submission details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Submission>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SubmissionDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SubmissionDetails", "Error fetching submission details", t);
            }
        });
    }

    private void viewSubmission() {
        if (submission != null && submission.submission != null) {
            String formattedSubmissionUrl = submission.submission.replace("\\", "/");

            String serverUrl = "http://192.168.8.124:8000/" + formattedSubmissionUrl.substring(formattedSubmissionUrl.lastIndexOf("/") + 1);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(serverUrl), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No application to view PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No submission available", Toast.LENGTH_SHORT).show();
        }
    }

    private void gradeSubmission() {
        String scoreText = edtScore.getText().toString();
        String feedback = edtFeedback.getText().toString();

        if (scoreText.isEmpty()) {
            Toast.makeText(this, "Please enter a score", Toast.LENGTH_SHORT).show();
            return;
        }

        float score;
        try {
            score = Float.parseFloat(scoreText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid score", Toast.LENGTH_SHORT).show();
            return;
        }

        GradeRequest gradeRequest = new GradeRequest(submissionId, score, feedback);

        progressBar.setVisibility(View.VISIBLE);
        mApiService.gradeAssignment(gradeRequest).enqueue(new Callback<BaseResponse<Grades>>() {
            @Override
            public void onResponse(Call<BaseResponse<Grades>> call, Response<BaseResponse<Grades>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Grades> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(SubmissionDetailsActivity.this, "Submission graded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(SubmissionDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SubmissionDetailsActivity.this, "Failed to grade submission", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Grades>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SubmissionDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
