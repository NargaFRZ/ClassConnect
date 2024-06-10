package com.classconnect;

import android.content.Context;
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

public class AllClassesActivity extends AppCompatActivity {

    private LinearLayout classesContainer;
    private ProgressBar progressBar;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_classes);

        classesContainer = findViewById(R.id.classesContainer);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();

        fetchAllClasses();
    }

    private void fetchAllClasses() {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getAllClasses().enqueue(new Callback<BaseResponse<List<Classes>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Classes>>> call, Response<BaseResponse<List<Classes>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Classes>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displayClasses(baseResponse.payload);
                    } else {
                        Toast.makeText(AllClassesActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AllClassesActivity.this, "Failed to fetch classes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Classes>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AllClassesActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AllClassesActivity", "Error fetching classes", t);
            }
        });
    }

    private void displayClasses(List<Classes> classes) {
        classesContainer.removeAllViews();
        for (Classes classItem : classes) {
            View classView = getLayoutInflater().inflate(R.layout.item_class, classesContainer, false);

            TextView txtClassName = classView.findViewById(R.id.txtClassName);
            TextView txtClassDescription = classView.findViewById(R.id.txtClassDescription);

            txtClassName.setText(classItem.name);
            txtClassDescription.setText("Description : " + classItem.description);

            classView.setOnClickListener(v -> {
                Intent intent = new Intent(AllClassesActivity.this, ClassDetailsActivity.class);
                intent.putExtra("classId", classItem.class_id.toString());
                startActivity(intent);
            });

            classesContainer.addView(classView);
        }
    }
}
