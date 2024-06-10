package com.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.model.Classes;
import com.classconnect.model.BaseResponse;
import com.classconnect.model.Member;
import com.classconnect.model.Roles;
import com.classconnect.model.TeacherClass;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.EnrollRequest;
import com.classconnect.request.TeacherRequest;
import com.classconnect.request.UtilsApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassDetailsActivity extends AppCompatActivity {

    private TextView txtClassName, txtClassDescription;
    private EditText edtEnrollmentKey;
    private Button btnEnroll, btnTeachClass, btnAddAssignment, btnViewAssignments, btnViewSubmissions;
    private LinearLayout membersContainer, teacherButtonsContainer;
    private ProgressBar progressBar;
    private BaseApiService mApiService;
    private String classId;
    private boolean isEnrolled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        txtClassName = findViewById(R.id.txtClassName);
        txtClassDescription = findViewById(R.id.txtClassDescription);
        edtEnrollmentKey = findViewById(R.id.edtEnrollmentKey);
        btnEnroll = findViewById(R.id.btnEnroll);
        btnTeachClass = findViewById(R.id.btnTeachClass);
        btnAddAssignment = findViewById(R.id.btnAddAssignment);
        btnViewAssignments = findViewById(R.id.btnViewAssignments);
        btnViewSubmissions = findViewById(R.id.btnViewSubmissions);
        membersContainer = findViewById(R.id.membersContainer);
        teacherButtonsContainer = findViewById(R.id.teacherButtonsContainer);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();
        classId = getIntent().getStringExtra("classId");

        fetchClassDetails(classId);
        fetchClassMembers(classId);

        btnEnroll.setOnClickListener(v -> {
            String enrollmentKey = edtEnrollmentKey.getText().toString();
            if (!enrollmentKey.isEmpty()) {
                enrollInClass(classId, LoginActivity.loggedAccount.user_id.toString(), enrollmentKey);
            } else {
                Toast.makeText(ClassDetailsActivity.this, "Please enter the enrollment key", Toast.LENGTH_SHORT).show();
            }
        });

        btnTeachClass.setOnClickListener(v -> {
            registerAsTeacher(classId, LoginActivity.loggedAccount.user_id.toString());
        });

        btnAddAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDetailsActivity.this, CreateAssignmentActivity.class);
            intent.putExtra("classId", classId);
            startActivity(intent);
        });

        btnViewAssignments.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDetailsActivity.this, ClassAssignmentActivity.class);
            intent.putExtra("classId", classId);
            startActivity(intent);
        });

        btnViewSubmissions.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDetailsActivity.this, ClassSubmissionActivity.class);
            intent.putExtra("classId", classId);
            startActivity(intent);
        });
    }

    private void fetchClassDetails(String classId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getClassById(classId).enqueue(new Callback<BaseResponse<Classes>>() {
            @Override
            public void onResponse(Call<BaseResponse<Classes>> call, Response<BaseResponse<Classes>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Classes> baseResponse = response.body();
                    if (baseResponse.success) {
                        Classes classItem = baseResponse.payload;
                        txtClassName.setText(classItem.name);
                        txtClassDescription.setText(classItem.description);
                    } else {
                        Toast.makeText(ClassDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClassDetailsActivity.this, "Failed to fetch class details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Classes>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ClassDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ClassDetailsActivity", "Error fetching class details", t);
            }
        });
    }

    private void fetchClassMembers(String classId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getClassMembers(classId).enqueue(new Callback<BaseResponse<List<Member>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Member>>> call, Response<BaseResponse<List<Member>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Member>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displayMembers(baseResponse.payload);
                        isEnrolled = false;

                        for (Member member : baseResponse.payload) {
                            Log.d("ClassDetailsActivity", "Member ID: " + member.member_id);
                            Log.d("ClassDetailsActivity", "Logged Account ID: " + LoginActivity.loggedAccount.user_id);
                            if (member.member_id.equals(LoginActivity.loggedAccount.user_id)) {
                                isEnrolled = true;
                                break;
                            }
                        }

                        Log.d("ClassDetailsActivity", "Is Enrolled: " + isEnrolled);
                        Log.d("ClassDetailsActivity", "Logged Account Role: " + LoginActivity.loggedAccount.role);

                        if (isEnrolled) {
                            btnEnroll.setVisibility(View.GONE);
                            edtEnrollmentKey.setVisibility(View.GONE);
                            btnViewAssignments.setVisibility(View.VISIBLE);
                            if (LoginActivity.loggedAccount.role.equals(Roles.teacher)) {
                                teacherButtonsContainer.setVisibility(View.VISIBLE);
                                btnTeachClass.setVisibility(View.GONE);
                                btnAddAssignment.setVisibility(View.VISIBLE);
                                btnViewSubmissions.setVisibility(View.VISIBLE);
                            }
                        } else if (LoginActivity.loggedAccount.role.equals(Roles.teacher)) {
                            btnEnroll.setVisibility(View.GONE);
                            edtEnrollmentKey.setVisibility(View.GONE);
                            teacherButtonsContainer.setVisibility(View.VISIBLE);
                            btnTeachClass.setVisibility(View.VISIBLE);
                            btnAddAssignment.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(ClassDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClassDetailsActivity.this, "Failed to fetch class members", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Member>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ClassDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ClassDetailsActivity", "Error fetching class members", t);
            }
        });
    }

    private void enrollInClass(String classId, String studentId, String enrollmentKey) {
        progressBar.setVisibility(View.VISIBLE);
        EnrollRequest enrollRequest = new EnrollRequest(classId, studentId, enrollmentKey);
        mApiService.enrollStudent(enrollRequest).enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Void> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(ClassDetailsActivity.this, "Enrolled successfully", Toast.LENGTH_SHORT).show();
                        fetchClassMembers(classId); // Refresh members list
                        btnEnroll.setVisibility(View.GONE);
                        edtEnrollmentKey.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(ClassDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClassDetailsActivity.this, "Failed to enroll in class", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ClassDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerAsTeacher(String classId, String teacherId) {
        progressBar.setVisibility(View.VISIBLE);
        TeacherRequest teacherRequest = new TeacherRequest(classId, teacherId);
        mApiService.registerAsClassTeacher(teacherRequest).enqueue(new Callback<BaseResponse<TeacherClass>>() {
            @Override
            public void onResponse(Call<BaseResponse<TeacherClass>> call, Response<BaseResponse<TeacherClass>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<TeacherClass> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(ClassDetailsActivity.this, "Registered as class teacher successfully", Toast.LENGTH_SHORT).show();
                        fetchClassMembers(classId);
                        btnTeachClass.setVisibility(View.GONE);
                        btnAddAssignment.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ClassDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClassDetailsActivity.this, "Failed to register as class teacher", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<TeacherClass>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ClassDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayMembers(List<Member> members) {
        membersContainer.removeAllViews();
        for (Member member : members) {
            View memberView = getLayoutInflater().inflate(R.layout.item_member, membersContainer, false);

            TextView txtMemberName = memberView.findViewById(R.id.txtMemberName);
            TextView txtMemberRole = memberView.findViewById(R.id.txtMemberRole);
            Button btnMessage = memberView.findViewById(R.id.btnMessage);

            txtMemberName.setText(member.name);
            txtMemberRole.setText(capitalizeFirstLetter(member.role.toString()));

            if (member.member_id.equals(LoginActivity.loggedAccount.user_id)) {
                btnMessage.setVisibility(View.GONE);
                txtMemberRole.setText(txtMemberRole.getText() + " (You)");
            } else {
                btnMessage.setOnClickListener(v -> {
                    Intent intent = new Intent(ClassDetailsActivity.this, MessageActivity.class);
                    intent.putExtra("memberId", member.member_id.toString());
                    intent.putExtra("memberName", member.name);
                    startActivity(intent);
                });
            }

            membersContainer.addView(memberView);
        }
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
