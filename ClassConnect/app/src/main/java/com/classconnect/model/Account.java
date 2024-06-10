package com.classconnect.model;

import java.util.UUID;

public class Account {
    public UUID user_id;
    public String name;
    public String username;
    public String password;
    public String email;
    public boolean approved;
    public Roles role;

    public Account() {
    }

    public Account(UUID user_id, String name, String username, String password, String email, Roles role, boolean approved) {
        this.user_id = user_id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.approved = approved;
    }
}
