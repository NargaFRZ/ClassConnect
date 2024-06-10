package com.classconnect.request;

public class EnrollRequest {
    public String classId;
    public String studentId;
    public String enrollmentKey;

    public EnrollRequest(String classId, String studentId, String enrollmentKey) {
        this.classId = classId;
        this.studentId = studentId;
        this.enrollmentKey = enrollmentKey;
    }
}
