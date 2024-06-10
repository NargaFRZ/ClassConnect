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

import com.classconnect.model.Classes;
import com.classconnect.model.BaseResponse;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UtilsApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnrolledClassActivity extends AppCompatActivity {

    private LinearLayout enrolledClassesContainer;
    private ProgressBar progressBar;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_class);

        enrolledClassesContainer = findViewById(R.id.enrolledClassesContainer);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();

        fetchEnrolledClasses(LoginActivity.loggedAccount.user_id.toString());
    }

    private void fetchEnrolledClasses(String studentId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getStudentClasses(studentId).enqueue(new Callback<BaseResponse<List<Classes>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Classes>>> call, Response<BaseResponse<List<Classes>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Classes>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displayEnrolledClasses(baseResponse.payload);
                    } else {
                        Toast.makeText(EnrolledClassActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EnrolledClassActivity.this, "Failed to fetch enrolled classes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Classes>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EnrolledClassActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("EnrolledClassActivity", "Error fetching enrolled classes", t);
            }
        });
    }

    private void displayEnrolledClasses(List<Classes> classes) {
        enrolledClassesContainer.removeAllViews();
        for (Classes classItem : classes) {
            View classView = getLayoutInflater().inflate(R.layout.item_class, enrolledClassesContainer, false);

            TextView txtClassName = classView.findViewById(R.id.txtClassName);
            TextView txtClassDescription = classView.findViewById(R.id.txtClassDescription);

            txtClassName.setText(classItem.name);
            txtClassDescription.setText(classItem.description);

            classView.setOnClickListener(v -> {
                Intent intent = new Intent(EnrolledClassActivity.this, ClassDetailsActivity.class);
                intent.putExtra("classId", classItem.class_id.toString());
                startActivity(intent);
            });

            enrolledClassesContainer.addView(classView);
        }
    }
}
