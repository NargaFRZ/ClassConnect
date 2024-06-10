package com.classconnect.model;

public class Submission {
    public int submission_id;
    public int assignment_id;
    public String student_id;
    public String submission;
    public String submitted_date;

    public String student_name;

    public Submission(int submission_id, int assignment_id, String student_id, String submission, String submitted_date) {
        this.submission_id = submission_id;
        this.assignment_id = assignment_id;
        this.student_id = student_id;
        this.submission = submission;
        this.submitted_date = submitted_date;

    }
}
