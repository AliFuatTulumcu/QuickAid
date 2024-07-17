package com.example.quickaid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 123;
    private static final String TAG = "MainActivity";
    private Button btnEmergency;
    private ImageButton btnUserProfile;
    private FusedLocationProviderClient fusedLocationClient;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEmergency = findViewById(R.id.btnEmergency);
        btnUserProfile = findViewById(R.id.btnUserProfile);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request permissions when the activity starts
        requestPermissions();

        // Fetch current user details
        fetchCurrentUserDetails();

        btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    sendHelpMessage();
                } else {
                    requestPermissions();
                }
            }
        });

        btnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileMenu(v);
            }
        });
    }

    private void showProfileMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        final int menuProfileId = R.id.menu_profile;
        final int menuSettingsId = R.id.menu_settings;
        final int menuLogoutId = R.id.menu_logout;

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == menuProfileId) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == menuSettingsId) {
                    // Handle settings action
                    return true;
                } else if (itemId == menuLogoutId) {
                    // Handle logout action
                    return true;
                } else {
                    return false;
                }
            }
        });
        popup.show();
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS
        }, PERMISSIONS_REQUEST_CODE);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                // Permissions granted, proceed with getting location
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestLocationUpdates() {
        if (checkPermissions()) {
            try {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Log.d(TAG, "Location obtained: " + location.toString());
                                } else {
                                    Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            requestPermissions();
        }
    }

    private void fetchCurrentUserDetails() {
        // Assume userId is obtained from SharedPreferences or Intent extras
        int userId = getIntent().getIntExtra("userId", -1);
        if (userId != -1) {
            new LoadUserTask().execute(userId);
        }
    }

    private class LoadUserTask extends AsyncTask<Integer, Void, User> {
        @Override
        protected User doInBackground(Integer... userIds) {
            UserDao userDao = AppDatabase.getDatabase(MainActivity.this).userDao();
            return userDao.getUserById(userIds[0]);
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                Log.d(TAG, "User obtained: " + user.toString());
            } else {
                Log.d(TAG, "User not found");
            }
            currentUser = user;
        }
    }

    private void sendHelpMessage() {
        if (checkPermissions()) {
            try {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null && currentUser != null) {
                                    Log.d(TAG, "Location and user details are available");
                                    String message = generateHelpMessage(location);
                                    sendSMSToEmergencyContacts(message);
                                } else {
                                    Log.d(TAG, "Location or user details are missing");
                                    Toast.makeText(MainActivity.this, "Unable to get location or user details", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            requestPermissions();
        }
    }

    private String generateHelpMessage(Location location) {
        String locationUrl = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
        return "Emergency! My location is: " + locationUrl +
                "\nName: " + currentUser.getName() +
                "\nSurname: " + currentUser.getSurname() +
                "\nBlood Type: " + currentUser.getBloodType() +
                "\nMedications: " + currentUser.getMedications() +
                "\nAllergies: " + currentUser.getAllergies() +
                "\nChronic Conditions: " + currentUser.getChronicConditions() +
                "\nEmergency Instructions: " + currentUser.getEmergencyInstructions() +
                "\nPhysician Contact: " + currentUser.getPhysicianContact() +
                "\nMedical History: " + currentUser.getMedicalHistory() +
                "\nVaccination Status: " + currentUser.getVaccinationStatus() +
                "\nInsurance Info: " + currentUser.getInsuranceInfo();
    }

    private void sendSMSToEmergencyContacts(String message) {
        new LoadEmergencyContactsTask().execute(currentUser.getId(), message);
    }

    private class LoadEmergencyContactsTask extends AsyncTask<Object, Void, List<EmergencyContact>> {
        private String message;

        @Override
        protected List<EmergencyContact> doInBackground(Object... params) {
            int userId = (int) params[0];
            message = (String) params[1];
            EmergencyContactDao contactDao = AppDatabase.getDatabase(MainActivity.this).emergencyContactDao();
            return contactDao.getEmergencyContactsForUser(userId);
        }

        @Override
        protected void onPostExecute(List<EmergencyContact> contacts) {
            for (EmergencyContact contact : contacts) {
                sendSMS(contact.getPhone(), message);
            }
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        if (checkPermissions()) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, "SMS sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "SMS failed, please try again later", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            requestPermissions();
        }
    }
}
