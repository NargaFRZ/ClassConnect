package com.classconnect.request;

public class MessageRequest {
    public String sender_id;
    public String receiver_id;
    public String content;

    public MessageRequest(String sender_id, String receiver_id, String content) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content = content;
    }
}
