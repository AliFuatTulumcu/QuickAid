package com.example.quickaid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                new LoginTask(LoginActivity.this, email, password).execute();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private static class LoginTask extends AsyncTask<Void, Void, User> {
        private Context context;
        private String email;
        private String password;

        LoginTask(Context context, String email, String password) {
            this.context = context;
            this.email = email;
            this.password = password;
        }

        @Override
        protected User doInBackground(Void... voids) {
            UserDao userDao = AppDatabase.getDatabase(context).userDao();
            return userDao.getUser(email, password);
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                SharedPreferences sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("userId", user.getId());
                editor.apply();

                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((LoginActivity) context).finish();
            } else {
                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
