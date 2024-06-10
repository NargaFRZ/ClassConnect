package com.classconnect;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.classconnect.model.Assignment;
import com.classconnect.model.BaseResponse;
import com.classconnect.model.Submission;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UtilsApi;

import java.io.File;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignmentDetailsActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private TextView txtAssignmentTitle, txtAssignmentDescription, txtDueDate;
    private Button btnChooseFile, btnSubmitAssignment, btnEditAssignment, btnDeleteAssignment, btnEditSubmission, btnDeleteSubmission, btnViewPDF, btnViewSubmission;
    private ProgressBar progressBar;
    private BaseApiService mApiService;
    private Uri fileUri;
    private int assignmentId;
    private Integer submissionId;
    private boolean isSubmitted;
    private String pdfUrl; // Add this variable to hold the PDF URL
    private String submissionUrl; // Add this variable to hold the submission URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_details);

        txtAssignmentTitle = findViewById(R.id.txtAssignmentTitle);
        txtAssignmentDescription = findViewById(R.id.txtAssignmentDescription);
        txtDueDate = findViewById(R.id.txtDueDate);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnSubmitAssignment = findViewById(R.id.btnSubmitAssignment);
        btnEditAssignment = findViewById(R.id.btnEditAssignment);
        btnDeleteAssignment = findViewById(R.id.btnDeleteAssignment);
        btnEditSubmission = findViewById(R.id.btnEditSubmission);
        btnDeleteSubmission = findViewById(R.id.btnDeleteSubmission);
        btnViewPDF = findViewById(R.id.btnViewPDF);
        btnViewSubmission = findViewById(R.id.btnViewSubmission);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();
        assignmentId = getIntent().getIntExtra("assignmentId", -1);
        submissionId = getIntent().hasExtra("submissionId") ? getIntent().getIntExtra("submissionId", -1) : null;
        isSubmitted = getIntent().getBooleanExtra("isSubmitted", false);

        fetchAssignmentDetails(assignmentId);

        btnChooseFile.setOnClickListener(v -> openFileChooser());

        if (isSubmitted) {
            btnSubmitAssignment.setVisibility(View.GONE);
            btnEditSubmission.setVisibility(View.VISIBLE);
            btnDeleteSubmission.setVisibility(View.VISIBLE);
        } else {
            btnSubmitAssignment.setVisibility(View.VISIBLE);
            btnEditSubmission.setVisibility(View.GONE);
            btnDeleteSubmission.setVisibility(View.GONE);
        }

        btnSubmitAssignment.setOnClickListener(v -> submitAssignment());

        btnEditSubmission.setOnClickListener(v -> updateSubmission());

        btnDeleteSubmission.setOnClickListener(v -> deleteSubmission());

        btnEditAssignment.setOnClickListener(v -> editAssignment());

        btnDeleteAssignment.setOnClickListener(v -> deleteAssignment());

        btnViewPDF.setOnClickListener(v -> viewPDF()); // Set click listener for the view PDF button

        btnViewSubmission.setOnClickListener(v -> viewSubmission()); // Set click listener for the view submission button
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File selected: " + fileUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAssignmentDetails(int assignmentId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getAssignmentById(assignmentId).enqueue(new Callback<BaseResponse<List<Assignment>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Assignment>>> call, Response<BaseResponse<List<Assignment>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Assignment>> baseResponse = response.body();
                    if (baseResponse.success) {
                        Assignment assignment = baseResponse.payload.get(0);
                        txtAssignmentTitle.setText(assignment.title);
                        txtAssignmentDescription.setText(assignment.description);
                        txtDueDate.setText("Due on: " + assignment.due_date);
                        pdfUrl = assignment.assignment;

                        // Fetch submission details if assignment exists
                        fetchSubmissionDetails(assignment.assignment_id, LoginActivity.loggedAccount.user_id);
                    } else {
                        Toast.makeText(AssignmentDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AssignmentDetailsActivity.this, "Failed to fetch assignment details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Assignment>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignmentDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSubmissionDetails(int assignmentId, UUID studentId) {
        mApiService.getSubmissionByAssignmentAndStudent(assignmentId, studentId.toString()).enqueue(new Callback<BaseResponse<Submission>>() {
            @Override
            public void onResponse(Call<BaseResponse<Submission>> call, Response<BaseResponse<Submission>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Submission> baseResponse = response.body();
                    if (baseResponse.success) {
                        submissionId = baseResponse.payload.submission_id;
                        submissionUrl = baseResponse.payload.submission; // Set the submission URL
                        isSubmitted = true;
                        btnSubmitAssignment.setVisibility(View.GONE);
                        btnEditSubmission.setVisibility(View.VISIBLE);
                        btnDeleteSubmission.setVisibility(View.VISIBLE);
                        btnViewSubmission.setVisibility(View.VISIBLE); // Show view submission button
                    } else {
                        isSubmitted = false;
                        btnSubmitAssignment.setVisibility(View.VISIBLE);
                        btnEditSubmission.setVisibility(View.GONE);
                        btnDeleteSubmission.setVisibility(View.GONE);
                        btnViewSubmission.setVisibility(View.GONE); // Hide view submission button
                    }
                } else {
                    Toast.makeText(AssignmentDetailsActivity.this, "Failed to fetch submission details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Submission>> call, Throwable t) {
                Toast.makeText(AssignmentDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void viewPDF() {
        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            String formattedPdfUrl = pdfUrl.replace("\\", "/");

            String serverUrl = "http://192.168.8.124:8000/" + formattedPdfUrl.substring(formattedPdfUrl.lastIndexOf("/") + 1);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(serverUrl), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No application to view PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No PDF available for this assignment", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewSubmission() {
        if (submissionUrl != null && !submissionUrl.isEmpty()) {
            String formattedSubmissionUrl = submissionUrl.replace("\\", "/");

            String serverUrl = "http://192.168.8.124:8000/" + formattedSubmissionUrl.substring(formattedSubmissionUrl.lastIndexOf("/") + 1);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(serverUrl), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No application to view submission PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No submission available for this assignment", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitAssignment() {
        if (fileUri == null) {
            Toast.makeText(this, "Please choose a file", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = UtilsApi.getFilePath(this, fileUri);
        if (filePath == null) {
            Toast.makeText(this, "Unable to get the file path", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("submission", file.getName(), requestFile); // Use 'submission' here
        RequestBody assignmentIdPart = RequestBody.create(MultipartBody.FORM, String.valueOf(assignmentId));
        RequestBody studentIdPart = RequestBody.create(MultipartBody.FORM, LoginActivity.loggedAccount.user_id.toString());

        progressBar.setVisibility(View.VISIBLE);

        mApiService.createSubmission(assignmentIdPart, studentIdPart, body).enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Void> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(AssignmentDetailsActivity.this, "Submission created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AssignmentDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AssignmentDetailsActivity.this, "Failed to create submission", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignmentDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSubmission() {
        if (fileUri == null) {
            Toast.makeText(this, "Please choose a file", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = UtilsApi.getFilePath(this, fileUri);
        if (filePath == null) {
            Toast.makeText(this, "Unable to get the file path", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("submission", file.getName(), requestFile); // Use 'submission' here
        RequestBody assignmentIdPart = RequestBody.create(MultipartBody.FORM, String.valueOf(assignmentId));
        RequestBody studentIdPart = RequestBody.create(MultipartBody.FORM, LoginActivity.loggedAccount.user_id.toString());

        progressBar.setVisibility(View.VISIBLE);

        mApiService.updateSubmission(submissionId, assignmentIdPart, studentIdPart, body).enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Void> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(AssignmentDetailsActivity.this, "Submission updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AssignmentDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AssignmentDetailsActivity.this, "Failed to update submission", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignmentDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteSubmission() {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.deleteSubmission(submissionId).enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Void> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(AssignmentDetailsActivity.this, "Submission deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AssignmentDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AssignmentDetailsActivity.this, "Failed to delete submission", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignmentDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editAssignment() {
    }

    private void deleteAssignment() {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.deleteAssignment(assignmentId).enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Void> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(AssignmentDetailsActivity.this, "Assignment deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AssignmentDetailsActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AssignmentDetailsActivity.this, "Failed to delete assignment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignmentDetailsActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}