package com.classconnect.model;

import java.util.UUID;

public class Member {
    public UUID member_id;
    public String role;
    public String name;

    public Member(UUID member_id, String role, String name) {
        this.member_id = member_id;
        this.role = role;
        this.name = name;
    }
}
