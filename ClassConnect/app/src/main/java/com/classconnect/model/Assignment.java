package com.classconnect.model;

public class Assignment {
    public int assignment_id;
    public String class_id;
    public String title;
    public String description;
    public String assignment;
    public String due_date;
    public String submitted_date;
    public int days_until_due;

    public int submission_id;

    public Assignment(int assignment_id, String class_id, String title, String description, String assignment, String due_date, String submitted_date, int days_until_due) {
        this.assignment_id = assignment_id;
        this.class_id = class_id;
        this.title = title;
        this.description = description;
        this.assignment = assignment;
        this.due_date = due_date;
        this.submitted_date = submitted_date;
        this.days_until_due = days_until_due;
    }
}
