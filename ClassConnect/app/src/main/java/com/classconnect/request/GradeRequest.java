package com.classconnect.request;

public class GradeRequest {
    private int submission_id;
    private float score;
    private String feedback;
    public GradeRequest(int submission_id, float score, String feedback) {
        this.submission_id = submission_id;
        this.score = score;
        this.feedback = feedback;
    }
}
