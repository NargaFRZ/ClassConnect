package com.classconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.classconnect.model.Account;
import com.classconnect.model.BaseResponse;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UserIdRequest;
import com.classconnect.request.UtilsApi;
import com.classconnect.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private LinearLayout usersContainer;
    private ProgressBar progressBar;
    private TextView tvNoUnapprovedAccounts;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        usersContainer = findViewById(R.id.usersContainer);
        progressBar = findViewById(R.id.progressBar);
        tvNoUnapprovedAccounts = findViewById(R.id.tvNoUnapprovedAccounts);

        mApiService = UtilsApi.getApiService();

        fetchUnapprovedUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void fetchUnapprovedUsers() {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getUnapprovedUsers().enqueue(new Callback<BaseResponse<List<Account>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Account>>> call, Response<BaseResponse<List<Account>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Account>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displayUsers(baseResponse.payload);
                    } else {
                        Toast.makeText(AdminActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Account>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AdminActivity", "Error fetching users", t);
            }
        });
    }

    private void displayUsers(List<Account> users) {
        usersContainer.removeAllViews();
        if (users.isEmpty()) {
            tvNoUnapprovedAccounts.setVisibility(View.VISIBLE);
        } else {
            tvNoUnapprovedAccounts.setVisibility(View.GONE);
            for (Account user : users) {
                View userView = getLayoutInflater().inflate(R.layout.item_user, usersContainer, false);

                TextView txtUsername = userView.findViewById(R.id.txtUsername);
                Button btnApprove = userView.findViewById(R.id.btnApprove);
                Button btnDelete = userView.findViewById(R.id.btnDelete);

                txtUsername.setText(user.username);

                btnApprove.setOnClickListener(v -> approveUser(user.user_id.toString()));
                btnDelete.setOnClickListener(v -> deleteUser(user.user_id.toString()));

                usersContainer.addView(userView);
            }
        }
    }

    private void approveUser(String userId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.approveUser(new UserIdRequest(userId)).enqueue(new Callback<BaseResponse<Account>>() {
            @Override
            public void onResponse(Call<BaseResponse<Account>> call, Response<BaseResponse<Account>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Account> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(AdminActivity.this, "User approved successfully", Toast.LENGTH_SHORT).show();
                        fetchUnapprovedUsers();
                    } else {
                        Toast.makeText(AdminActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to approve user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Account>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AdminActivity", "Error approving user", t);
            }
        });
    }

    private void deleteUser(String userId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.deleteUser(userId).enqueue(new Callback<BaseResponse<Account>>() {
            @Override
            public void onResponse(Call<BaseResponse<Account>> call, Response<BaseResponse<Account>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Account> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(AdminActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        fetchUnapprovedUsers();
                    } else {
                        Toast.makeText(AdminActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Account>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AdminActivity", "Error deleting user", t);
            }
        });
    }
}
