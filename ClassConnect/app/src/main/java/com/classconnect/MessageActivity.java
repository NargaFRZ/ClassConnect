package com.classconnect;

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

import com.classconnect.model.Message;
import com.classconnect.model.BaseResponse;
import com.classconnect.request.BaseApiService;
import com.classconnect.request.MessageRequest;
import com.classconnect.request.UtilsApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private LinearLayout messagesContainer;
    private ProgressBar progressBar;
    private EditText edtMessage;
    private Button btnSend;
    private BaseApiService mApiService;
    private String memberId;
    private String memberName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messagesContainer = findViewById(R.id.messagesContainer);
        progressBar = findViewById(R.id.progressBar);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        mApiService = UtilsApi.getApiService();
        memberId = getIntent().getStringExtra("memberId");
        memberName = getIntent().getStringExtra("memberName");

        fetchMessages(memberId);

        btnSend.setOnClickListener(v -> {
            String content = edtMessage.getText().toString();
            if (!content.isEmpty()) {
                sendMessage(LoginActivity.loggedAccount.user_id.toString(), memberId, content);
            } else {
                Toast.makeText(MessageActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMessages(String memberId) {
        progressBar.setVisibility(View.VISIBLE);
        mApiService.getMessages(LoginActivity.loggedAccount.user_id.toString(), memberId).enqueue(new Callback<BaseResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Message>>> call, Response<BaseResponse<List<Message>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<Message>> baseResponse = response.body();
                    if (baseResponse.success) {
                        displayMessages(baseResponse.payload);
                    } else {
                        Toast.makeText(MessageActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MessageActivity.this, "Failed to fetch messages", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Message>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MessageActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MessageActivity", "Error fetching messages", t);
            }
        });
    }

    private void sendMessage(String senderId, String receiverId, String content) {
        progressBar.setVisibility(View.VISIBLE);
        MessageRequest messageRequest = new MessageRequest(senderId, receiverId, content);
        mApiService.sendMessage(messageRequest).enqueue(new Callback<BaseResponse<Message>>() {
            @Override
            public void onResponse(Call<BaseResponse<Message>> call, Response<BaseResponse<Message>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Message> baseResponse = response.body();
                    if (baseResponse.success) {
                        Toast.makeText(MessageActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                        edtMessage.setText("");
                        fetchMessages(memberId); // Refresh messages
                    } else {
                        Toast.makeText(MessageActivity.this, baseResponse.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MessageActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Message>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MessageActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayMessages(List<Message> messages) {
        messagesContainer.removeAllViews();
        for (Message message : messages) {
            View messageView = getLayoutInflater().inflate(R.layout.item_message, messagesContainer, false);

            TextView txtMessageContent = messageView.findViewById(R.id.txtMessageContent);
            TextView txtMessageSender = messageView.findViewById(R.id.txtMessageSender);

            txtMessageContent.setText(message.content);
            txtMessageSender.setText(message.sender_name);

            messagesContainer.addView(messageView);
        }
    }
}
