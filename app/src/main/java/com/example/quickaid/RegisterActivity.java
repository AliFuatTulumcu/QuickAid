package com.example.quickaid;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private EditText etName;
    private EditText etSurname;
    private EditText etPhone;
    private EditText etAddress;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String name = etName.getText().toString();
                String surname = etSurname.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();

                if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !surname.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {
                    User user = new User();
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setName(name);
                    user.setSurname(surname);
                    user.setPhone(phone);
                    user.setAddress(address);

                    new RegisterTask(RegisterActivity.this, user).execute();
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static class RegisterTask extends AsyncTask<Void, Void, Boolean> {
        private final Context context;
        private final User user;

        RegisterTask(Context context, User user) {
            this.context = context;
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            UserDao userDao = AppDatabase.getDatabase(context).userDao();
            try {
                userDao.insert(user);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show();
                ((RegisterActivity) context).finish();
            } else {
                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
