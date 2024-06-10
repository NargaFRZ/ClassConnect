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

import com.classconnect.model.Account;
import com.classconnect.model.BaseResponse;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.UtilsApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetMessagesActivity extends AppCompatActivity {

    private LinearLayout sendersContainer;
    private ProgressBar progressBar;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_messages);

        sendersContainer = findViewById(R.id.sendersContainer);
        progressBar = findViewById(R.id.progressBar);

        mApiService = UtilsApi.getApiService();

        fetchMessageSenders();
    }

    private void fetchMessageSenders() {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getMessageSenders(LoginActivity.loggedAccount.user_id.toString()).enqueue(new Callback<BaseResponse<List<Account>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Account>>> call, Response<BaseResponse<List<Account>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Account>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displaySenders(baseResponse.payload);
                    } else {
                        Toast.makeText(GetMessagesActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GetMessagesActivity.this, "Failed to fetch senders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Account>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(GetMessagesActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("GetMessagesActivity", "Error fetching senders", t);
            }
        });
    }

    private void displaySenders(List<Account> senders) {
        sendersContainer.removeAllViews();
        for (Account sender : senders) {
            View senderView = getLayoutInflater().inflate(R.layout.item_sender, sendersContainer, false);

            TextView txtSenderName = senderView.findViewById(R.id.txtSenderName);
            txtSenderName.setText(sender.name);

            senderView.setOnClickListener(v -> {
                Intent intent = new Intent(GetMessagesActivity.this, MessageActivity.class);
                intent.putExtra("memberId", sender.user_id.toString());
                intent.putExtra("memberName", sender.name);
                startActivity(intent);
            });

            sendersContainer.addView(senderView);
        }
    }
}
