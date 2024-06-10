package com.classconnect;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.model.BaseResponse;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UtilsApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAssignmentActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private EditText edtTitle, edtDescription, edtDueDate;
    private Button btnChooseFile, btnCreateAssignment;
    private ProgressBar progressBar;
    private BaseApiService mApiService;
    private Uri fileUri;
    private File selectedFile;
    private String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtDueDate = findViewById(R.id.edtDueDate);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnCreateAssignment = findViewById(R.id.btnCreateAssignment);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();
        classId = getIntent().getStringExtra("classId");

        btnChooseFile.setOnClickListener(v -> openFileChooser());

        btnCreateAssignment.setOnClickListener(v -> createAssignment());
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
            try {
                selectedFile = getFileFromUri(fileUri);
                Toast.makeText(this, "File selected: " + selectedFile.getName(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to select file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createAssignment() {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        String dueDate = edtDueDate.getText().toString();

        if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty() || selectedFile == null) {
            Toast.makeText(this, "Please fill in all fields and choose a file", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), selectedFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", selectedFile.getName(), requestFile);
        RequestBody classIdPart = RequestBody.create(MultipartBody.FORM, classId);
        RequestBody titlePart = RequestBody.create(MultipartBody.FORM, title);
        RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM, description);
        RequestBody dueDatePart = RequestBody.create(MultipartBody.FORM, dueDate);

        progressBar.setVisibility(View.VISIBLE);

        mApiService.createAssignment(classIdPart, titlePart, descriptionPart, dueDatePart, body).enqueue(new Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Void> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(CreateAssignmentActivity.this, "Assignment created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateAssignmentActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateAssignmentActivity.this, "Failed to create assignment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CreateAssignmentActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File getFileFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = getContentResolver();
        String fileName = getFileName(uri);
        File tempFile = new File(getCacheDir(), fileName);
        try (InputStream inputStream = contentResolver.openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        result = cursor.getString(displayNameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
