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

import com.classconnect.model.Assignment;
import com.classconnect.model.BaseResponse;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UtilsApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassAssignmentActivity extends AppCompatActivity {

    private LinearLayout assignmentsContainer;
    private ProgressBar progressBar;
    private BaseApiService mApiService;
    private String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_assignment);

        assignmentsContainer = findViewById(R.id.assignmentsContainer);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();
        classId = getIntent().getStringExtra("classId");

        fetchAssignmentsByClass(classId);
    }

    private void fetchAssignmentsByClass(String classId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getAssignmentByClass(classId).enqueue(new Callback<BaseResponse<List<Assignment>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Assignment>>> call, Response<BaseResponse<List<Assignment>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Assignment>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displayAssignments(baseResponse.payload);
                    } else {
                        Toast.makeText(ClassAssignmentActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClassAssignmentActivity.this, "Failed to fetch assignments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Assignment>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ClassAssignmentActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ClassAssignmentActivity", "Error fetching assignments", t);
            }
        });
    }

    private void displayAssignments(List<Assignment> assignments) {
        assignmentsContainer.removeAllViews();
        for (Assignment assignment : assignments) {
            View assignmentView = getLayoutInflater().inflate(R.layout.item_assignment, assignmentsContainer, false);

            TextView txtAssignmentTitle = assignmentView.findViewById(R.id.txtAssignmentTitle);
            TextView txtAssignmentDescription = assignmentView.findViewById(R.id.txtAssignmentDescription);
            TextView txtDaysUntilDue = assignmentView.findViewById(R.id.txtDaysUntilDue);
            TextView txtSubmittedDate = assignmentView.findViewById(R.id.txtSubmittedDate);

            txtAssignmentTitle.setText(assignment.title);
            txtAssignmentDescription.setText(assignment.description);

            txtDaysUntilDue.setVisibility(View.GONE);

            txtSubmittedDate.setVisibility(View.GONE);

            assignmentView.setOnClickListener(v -> {
                Intent intent = new Intent(ClassAssignmentActivity.this, AssignmentDetailsActivity.class);
                intent.putExtra("assignmentId", assignment.assignment_id);
                startActivity(intent);
            });

            assignmentsContainer.addView(assignmentView);
        }
    }
}
