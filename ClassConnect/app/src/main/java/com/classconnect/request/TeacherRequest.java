package com.classconnect.request;

public class TeacherRequest {
    public String class_id;
    public String teacher_id;

    public TeacherRequest(String class_id, String teacher_id) {
        this.class_id = class_id;
        this.teacher_id = teacher_id;
    }
}
