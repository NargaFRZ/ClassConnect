package com.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.model.BaseResponse;
import com.classconnect.model.Submission;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UtilsApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassSubmissionActivity extends AppCompatActivity {

    private LinearLayout submissionsContainer;
    private ProgressBar progressBar;
    private BaseApiService mApiService;
    private String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_submission);

        submissionsContainer = findViewById(R.id.submissionsContainer);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();
        classId = getIntent().getStringExtra("classId");

        fetchSubmissionsByClass(classId);
    }

    private void fetchSubmissionsByClass(String classId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getSubmissionsByClass(classId).enqueue(new Callback<BaseResponse<List<Submission>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Submission>>> call, Response<BaseResponse<List<Submission>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Submission>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displaySubmissions(baseResponse.payload);
                    } else {
                        Toast.makeText(ClassSubmissionActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClassSubmissionActivity.this, "Failed to fetch submissions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Submission>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ClassSubmissionActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ClassSubmissionActivity", "Error fetching submissions", t);
            }
        });
    }

    private void displaySubmissions(List<Submission> submissions) {
        submissionsContainer.removeAllViews();
        for (Submission submission : submissions) {
            View submissionView = getLayoutInflater().inflate(R.layout.item_submission, submissionsContainer, false);

            TextView txtAssignmentId = submissionView.findViewById(R.id.txtAssignmentId);
            TextView txtStudentName = submissionView.findViewById(R.id.txtStudentName);
            TextView txtSubmittedDate = submissionView.findViewById(R.id.txtSubmittedDate);

            txtAssignmentId.setText("Assignment ID: " + submission.assignment_id);
            txtStudentName.setText("Student: " + submission.student_name);
            txtSubmittedDate.setText("Submitted on: " + submission.submitted_date);

            submissionView.setOnClickListener(v -> {
                Intent intent = new Intent(ClassSubmissionActivity.this, SubmissionDetailsActivity.class);
                intent.putExtra("submissionId", submission.submission_id);
                startActivity(intent);
            });

            submissionsContainer.addView(submissionView);
        }
    }
}
