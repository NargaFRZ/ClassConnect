package com.classconnect.request;

import java.util.UUID;

public class ClassRequest {
    public String name;
    public String description;
    public String enrollment_key;
    public String user_id;

    public ClassRequest(String name, String description, String enrollment_key, String user_id) {
        this.name = name;
        this.description = description;
        this.enrollment_key = enrollment_key;
        this.user_id = user_id;
    }
}
