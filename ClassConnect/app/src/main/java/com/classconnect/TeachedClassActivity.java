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

public class TeachedClassActivity extends AppCompatActivity {

    private LinearLayout teachedClassesContainer;
    private ProgressBar progressBar;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teached_class);

        teachedClassesContainer = findViewById(R.id.teachedClassesContainer);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();

        fetchTeachedClasses(LoginActivity.loggedAccount.user_id.toString());
    }

    private void fetchTeachedClasses(String teacherId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getTeachedClasses(teacherId).enqueue(new Callback<BaseResponse<List<Classes>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Classes>>> call, Response<BaseResponse<List<Classes>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Classes>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displayTeachedClasses(baseResponse.payload);
                    } else {
                        Toast.makeText(TeachedClassActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TeachedClassActivity.this, "Failed to fetch teached classes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Classes>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TeachedClassActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TeachedClassActivity", "Error fetching teached classes", t);
            }
        });
    }

    private void displayTeachedClasses(List<Classes> classes) {
        teachedClassesContainer.removeAllViews();
        for (Classes classItem : classes) {
            View classView = getLayoutInflater().inflate(R.layout.item_class, teachedClassesContainer, false);

            TextView txtClassName = classView.findViewById(R.id.txtClassName);
            TextView txtClassDescription = classView.findViewById(R.id.txtClassDescription);

            txtClassName.setText(classItem.name);
            txtClassDescription.setText(classItem.description);

            classView.setOnClickListener(v -> {
                Intent intent = new Intent(TeachedClassActivity.this, ClassDetailsActivity.class);
                intent.putExtra("classId", classItem.class_id.toString());
                startActivity(intent);
            });

            teachedClassesContainer.addView(classView);
        }
    }
}
