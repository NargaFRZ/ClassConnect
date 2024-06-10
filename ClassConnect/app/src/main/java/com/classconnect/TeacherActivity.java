package com.classconnect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherActivity extends AppCompatActivity {

    private Button btnCreateClass, btnViewClasses, btnTeachedClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        btnCreateClass = findViewById(R.id.btnCreateClass);
        btnCreateClass.setOnClickListener(v -> moveActivity(TeacherActivity.this, CreateClassActivity.class));

        btnViewClasses = findViewById(R.id.btnViewClasses);
        btnViewClasses.setOnClickListener(v -> moveActivity(TeacherActivity.this, AllClassesActivity.class));

        btnTeachedClass = findViewById(R.id.btnTeachClass);
        btnTeachedClass.setOnClickListener(v -> moveActivity(TeacherActivity.this, TeachedClassActivity.class));
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
            moveActivity(this, TeacherProfileActivity.class);
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
