package com.classconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

public class StudentActivity extends AppCompatActivity {

    private Button btnViewClasses, btnEnrolledClasses, btnViewUnsubmittedAssignments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        btnViewClasses = findViewById(R.id.btnViewClasses);
        btnEnrolledClasses = findViewById(R.id.btnEnrolledClasses);
        btnViewUnsubmittedAssignments = findViewById(R.id.btnViewUnsubmittedAssignments);

        btnViewClasses.setOnClickListener(v -> moveActivity(this, AllClassesActivity.class));
        btnEnrolledClasses.setOnClickListener(v -> moveActivity(this, EnrolledClassActivity.class));
        btnViewUnsubmittedAssignments.setOnClickListener(v -> moveActivity(this, AssignmentsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profile) {
            moveActivity(this, StudentProfileActivity.class);
            return true;
        } else if (id == R.id.messages) {
            moveActivity(this, GetMessagesActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }
}
