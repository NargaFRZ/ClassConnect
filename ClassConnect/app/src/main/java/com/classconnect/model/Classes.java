package com.classconnect.model;

import java.util.UUID;

public class Classes {
    public UUID class_id;
    public String name;
    public String description;
    public String enrollment_key;

    public Classes(UUID class_id, String name, String description, String enrollment_key) {
        this.class_id = class_id;
        this.name = name;
        this.description = description;
        this.enrollment_key = enrollment_key;
    }
}
