package com.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.*;
import com.classconnect.model.Assignment;
import com.classconnect.model.BaseResponse;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UtilsApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignmentsActivity extends AppCompatActivity {

    private LinearLayout unsubmittedAssignmentsContainer, submittedAssignmentsContainer;
    private ProgressBar progressBar;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);

        unsubmittedAssignmentsContainer = findViewById(R.id.unsubmittedAssignmentsContainer);
        submittedAssignmentsContainer = findViewById(R.id.submittedAssignmentsContainer);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();

        fetchUnsubmittedAssignments(LoginActivity.loggedAccount.user_id.toString());
        fetchSubmittedAssignments(LoginActivity.loggedAccount.user_id.toString());
    }

    private void fetchUnsubmittedAssignments(String studentId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getUnsubmittedAssignments(studentId).enqueue(new Callback<BaseResponse<List<Assignment>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Assignment>>> call, Response<BaseResponse<List<Assignment>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Assignment>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displayUnsubmittedAssignments(baseResponse.payload);
                    } else {
                        Toast.makeText(AssignmentsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AssignmentsActivity.this, "Failed to fetch unsubmitted assignments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Assignment>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignmentsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSubmittedAssignments(String studentId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getSubmittedAssignments(studentId).enqueue(new Callback<BaseResponse<List<Assignment>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Assignment>>> call, Response<BaseResponse<List<Assignment>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Assignment>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displaySubmittedAssignments(baseResponse.payload);
                    } else {
                        Toast.makeText(AssignmentsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AssignmentsActivity.this, "Failed to fetch submitted assignments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Assignment>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignmentsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUnsubmittedAssignments(List<Assignment> assignments) {
        unsubmittedAssignmentsContainer.removeAllViews();
        for (Assignment assignment : assignments) {
            View assignmentView = getLayoutInflater().inflate(R.layout.item_assignment, unsubmittedAssignmentsContainer, false);

            TextView txtAssignmentTitle = assignmentView.findViewById(R.id.txtAssignmentTitle);
            TextView txtAssignmentDescription = assignmentView.findViewById(R.id.txtAssignmentDescription);
            TextView txtDaysUntilDue = assignmentView.findViewById(R.id.txtDaysUntilDue);

            txtAssignmentTitle.setText(assignment.title);
            txtAssignmentDescription.setText(assignment.description);
            txtDaysUntilDue.setText("Due in " + assignment.days_until_due + " days");

            assignmentView.setOnClickListener(v -> {
                Intent intent = new Intent(AssignmentsActivity.this, AssignmentDetailsActivity.class);
                intent.putExtra("assignmentId", assignment.assignment_id);
                intent.putExtra("isSubmitted", false);
                startActivity(intent);
            });

            unsubmittedAssignmentsContainer.addView(assignmentView);
        }
    }

    private void displaySubmittedAssignments(List<Assignment> assignments) {
        submittedAssignmentsContainer.removeAllViews();
        for (Assignment assignment : assignments) {
            View assignmentView = getLayoutInflater().inflate(R.layout.item_assignment, submittedAssignmentsContainer, false);

            TextView txtAssignmentTitle = assignmentView.findViewById(R.id.txtAssignmentTitle);
            TextView txtAssignmentDescription = assignmentView.findViewById(R.id.txtAssignmentDescription);
            TextView txtSubmittedDate = assignmentView.findViewById(R.id.txtSubmittedDate);

            txtAssignmentTitle.setText(assignment.title);
            txtAssignmentDescription.setText(assignment.description);
            txtSubmittedDate.setText("Submitted on " + assignment.submitted_date);

            assignmentView.setOnClickListener(v -> {
                Intent intent = new Intent(AssignmentsActivity.this, AssignmentDetailsActivity.class);
                intent.putExtra("assignmentId", assignment.assignment_id);
                intent.putExtra("submissionId", assignment.submission_id);
                intent.putExtra("isSubmitted", true);
                startActivity(intent);
            });

            submittedAssignmentsContainer.addView(assignmentView);
        }
    }
}