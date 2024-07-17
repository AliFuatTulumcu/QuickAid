package com.example.quickaid;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private ListView userListView;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListView = findViewById(R.id.user_list_view);
        db = AppDatabase.getDatabase(getApplicationContext());

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<User> userList = db.userDao().getAllUsers();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<User> adapter = new ArrayAdapter<>(UserListActivity.this, android.R.layout.simple_list_item_1, userList);
                        userListView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }
}
