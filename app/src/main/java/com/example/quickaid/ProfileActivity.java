package com.example.quickaid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName;
    private TextView tvUserSurname;
    private TextView tvUserPhone;
    private TextView tvUserEmail;
    private TextView tvUserAddress;
    private Button btnEditUserInfo;
    private LinearLayout llEmergencyContacts;
    private Button btnAddContact;
    private Button btnEditContact;
    private Button btnDeleteContact;

    private TextView tvBloodType;
    private TextView tvMedications;
    private TextView tvAllergies;
    private TextView tvChronicConditions;
    private TextView tvEmergencyInstructions;
    private TextView tvPhysicianContact;
    private TextView tvMedicalHistory;
    private TextView tvVaccinationStatus;
    private TextView tvInsuranceInfo;
    private Button btnEditHealthInfo;

    private ArrayList<EmergencyContact> emergencyContacts;
    private User currentUser;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserSurname = findViewById(R.id.tvUserSurname);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserAddress = findViewById(R.id.tvUserAddress);
        btnEditUserInfo = findViewById(R.id.btnEditUserInfo);
        llEmergencyContacts = findViewById(R.id.llEmergencyContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
        btnEditContact = findViewById(R.id.btnEditContact);
        btnDeleteContact = findViewById(R.id.btnDeleteContact);

        tvBloodType = findViewById(R.id.tvBloodType);
        tvMedications = findViewById(R.id.tvMedications);
        tvAllergies = findViewById(R.id.tvAllergies);
        tvChronicConditions = findViewById(R.id.tvChronicConditions);
        tvEmergencyInstructions = findViewById(R.id.tvEmergencyInstructions);
        tvPhysicianContact = findViewById(R.id.tvPhysicianContact);
        tvMedicalHistory = findViewById(R.id.tvMedicalHistory);
        tvVaccinationStatus = findViewById(R.id.tvVaccinationStatus);
        tvInsuranceInfo = findViewById(R.id.tvInsuranceInfo);
        btnEditHealthInfo = findViewById(R.id.btnEditHealthInfo);

        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("userId", -1);

        if (userId != -1) {
            new LoadUserInfoTask().execute(userId);
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnEditUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditUserInfoDialog();
            }
        });

        btnEditHealthInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditHealthInfoDialog();
            }
        });



        emergencyContacts = new ArrayList<>();
        loadEmergencyContacts();

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emergencyContacts.size() >= 4) {
                    showMaxContactsWarning();
                } else {
                    showAddContactDialog();
                }
            }
        });

        btnEditContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditContactDialog();
            }
        });

        btnDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteContactDialog();
            }
        });
    }
    private void loadEmergencyContacts() {
        new LoadEmergencyContactsTask().execute(userId);
    }

    private void loadUserInfo(User user) {
        if (user != null) {
            currentUser = user;
            tvUserName.setText("Name: " + currentUser.getName());
            tvUserSurname.setText("Surname: " + currentUser.getSurname());
            tvUserPhone.setText("Phone: " + currentUser.getPhone());
            tvUserEmail.setText("Email: " + currentUser.getEmail());
            tvUserAddress.setText("Address: " + currentUser.getAddress());

            tvBloodType.setText("Blood Type: " + currentUser.getBloodType());
            tvMedications.setText("Medications: " + currentUser.getMedications());
            tvAllergies.setText("Allergies: " + currentUser.getAllergies());
            tvChronicConditions.setText("Chronic Conditions: " + currentUser.getChronicConditions());
            tvEmergencyInstructions.setText("Emergency Instructions: " + currentUser.getEmergencyInstructions());
            tvPhysicianContact.setText("Physician Contact: " + currentUser.getPhysicianContact());
            tvMedicalHistory.setText("Medical History: " + currentUser.getMedicalHistory());
            tvVaccinationStatus.setText("Vaccination Status: " + currentUser.getVaccinationStatus());
            tvInsuranceInfo.setText("Insurance Info: " + currentUser.getInsuranceInfo());

            new LoadEmergencyContactsTask().execute(currentUser.getId());
        }
    }

    private class LoadUserInfoTask extends AsyncTask<Integer, Void, User> {

        @Override
        protected User doInBackground(Integer... userIds) {
            UserDao userDao = AppDatabase.getDatabase(ProfileActivity.this).userDao();
            return userDao.getUserById(userIds[0]);
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                loadUserInfo(user);
            } else {
                Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoadEmergencyContactsTask extends AsyncTask<Integer, Void, List<EmergencyContact>> {
        @Override
        protected List<EmergencyContact> doInBackground(Integer... userIds) {
            EmergencyContactDao contactDao = AppDatabase.getDatabase(ProfileActivity.this).emergencyContactDao();
            return contactDao.getEmergencyContactsForUser(userIds[0]);
        }

        @Override
        protected void onPostExecute(List<EmergencyContact> contacts) {
            emergencyContacts.clear();
            emergencyContacts.addAll(contacts);
            llEmergencyContacts.removeAllViews();
            for (EmergencyContact contact : emergencyContacts) {
                addContactView(contact);
            }
        }
    }


    private void addContactView(EmergencyContact contact) {
        View contactView = getLayoutInflater().inflate(R.layout.item_emergency_contact, llEmergencyContacts, false);

        TextView tvContactName = contactView.findViewById(R.id.tvContactName);
        TextView tvContactRelationship = contactView.findViewById(R.id.tvContactRelationship);
        TextView tvContactPhone = contactView.findViewById(R.id.tvContactPhone);

        tvContactName.setText(contact.getName());
        tvContactRelationship.setText(contact.getRelationship());
        tvContactPhone.setText(contact.getPhone());

        llEmergencyContacts.addView(contactView);
    }


    private void showEditUserInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit User Information");

        // Set up the input fields
        final EditText inputName = new EditText(this);
        final EditText inputSurname = new EditText(this);
        final EditText inputPhone = new EditText(this);
        final EditText inputEmail = new EditText(this);
        final EditText inputAddress = new EditText(this);

        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        inputSurname.setInputType(InputType.TYPE_CLASS_TEXT);
        inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputAddress.setInputType(InputType.TYPE_CLASS_TEXT);

        inputName.setText(tvUserName.getText().toString().replace("Name: ", ""));
        inputSurname.setText(tvUserSurname.getText().toString().replace("Surname: ", ""));
        inputPhone.setText(tvUserPhone.getText().toString().replace("Phone: ", ""));
        inputEmail.setText(tvUserEmail.getText().toString().replace("Email: ", ""));
        inputAddress.setText(tvUserAddress.getText().toString().replace("Address: ", ""));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputName);
        layout.addView(inputSurname);
        layout.addView(inputPhone);
        layout.addView(inputEmail);
        layout.addView(inputAddress);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvUserName.setText("Name: " + inputName.getText().toString());
                tvUserSurname.setText("Surname: " + inputSurname.getText().toString());
                tvUserPhone.setText("Phone: " + inputPhone.getText().toString());
                tvUserEmail.setText("Email: " + inputEmail.getText().toString());
                tvUserAddress.setText("Address: " + inputAddress.getText().toString());

                // Update the user object
                currentUser.setName(inputName.getText().toString());
                currentUser.setSurname(inputSurname.getText().toString());
                currentUser.setPhone(inputPhone.getText().toString());
                currentUser.setEmail(inputEmail.getText().toString());
                currentUser.setAddress(inputAddress.getText().toString());

                // Save the updated information to the database
                new UpdateUserInfoTask().execute(currentUser);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showEditHealthInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Health Information");

        // Set up the input fields
        final EditText inputBloodType = new EditText(this);
        final EditText inputMedications = new EditText(this);
        final EditText inputAllergies = new EditText(this);
        final EditText inputChronicConditions = new EditText(this);
        final EditText inputEmergencyInstructions = new EditText(this);
        final EditText inputPhysicianContact = new EditText(this);
        final EditText inputMedicalHistory = new EditText(this);
        final EditText inputVaccinationStatus = new EditText(this);
        final EditText inputInsuranceInfo = new EditText(this);

        inputBloodType.setInputType(InputType.TYPE_CLASS_TEXT);
        inputMedications.setInputType(InputType.TYPE_CLASS_TEXT);
        inputAllergies.setInputType(InputType.TYPE_CLASS_TEXT);
        inputChronicConditions.setInputType(InputType.TYPE_CLASS_TEXT);
        inputEmergencyInstructions.setInputType(InputType.TYPE_CLASS_TEXT);
        inputPhysicianContact.setInputType(InputType.TYPE_CLASS_TEXT);
        inputMedicalHistory.setInputType(InputType.TYPE_CLASS_TEXT);
        inputVaccinationStatus.setInputType(InputType.TYPE_CLASS_TEXT);
        inputInsuranceInfo.setInputType(InputType.TYPE_CLASS_TEXT);

        inputBloodType.setText(tvBloodType.getText().toString().replace("Blood Type: ", ""));
        inputMedications.setText(tvMedications.getText().toString().replace("Medications: ", ""));
        inputAllergies.setText(tvAllergies.getText().toString().replace("Allergies: ", ""));
        inputChronicConditions.setText(tvChronicConditions.getText().toString().replace("Chronic Conditions: ", ""));
        inputEmergencyInstructions.setText(tvEmergencyInstructions.getText().toString().replace("Emergency Instructions: ", ""));
        inputPhysicianContact.setText(tvPhysicianContact.getText().toString().replace("Physician Contact: ", ""));
        inputMedicalHistory.setText(tvMedicalHistory.getText().toString().replace("Medical History: ", ""));
        inputVaccinationStatus.setText(tvVaccinationStatus.getText().toString().replace("Vaccination Status: ", ""));
        inputInsuranceInfo.setText(tvInsuranceInfo.getText().toString().replace("Insurance Info: ", ""));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputBloodType);
        layout.addView(inputMedications);
        layout.addView(inputAllergies);
        layout.addView(inputChronicConditions);
        layout.addView(inputEmergencyInstructions);
        layout.addView(inputPhysicianContact);
        layout.addView(inputMedicalHistory);
        layout.addView(inputVaccinationStatus);
        layout.addView(inputInsuranceInfo);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvBloodType.setText("Blood Type: " + inputBloodType.getText().toString());
                tvMedications.setText("Medications: " + inputMedications.getText().toString());
                tvAllergies.setText("Allergies: " + inputAllergies.getText().toString());
                tvChronicConditions.setText("Chronic Conditions: " + inputChronicConditions.getText().toString());
                tvEmergencyInstructions.setText("Emergency Instructions: " + inputEmergencyInstructions.getText().toString());
                tvPhysicianContact.setText("Physician Contact: " + inputPhysicianContact.getText().toString());
                tvMedicalHistory.setText("Medical History: " + inputMedicalHistory.getText().toString());
                tvVaccinationStatus.setText("Vaccination Status: " + inputVaccinationStatus.getText().toString());
                tvInsuranceInfo.setText("Insurance Info: " + inputInsuranceInfo.getText().toString());

                // Update the user object
                currentUser.setBloodType(inputBloodType.getText().toString());
                currentUser.setMedications(inputMedications.getText().toString());
                currentUser.setAllergies(inputAllergies.getText().toString());
                currentUser.setChronicConditions(inputChronicConditions.getText().toString());
                currentUser.setEmergencyInstructions(inputEmergencyInstructions.getText().toString());
                currentUser.setPhysicianContact(inputPhysicianContact.getText().toString());
                currentUser.setMedicalHistory(inputMedicalHistory.getText().toString());
                currentUser.setVaccinationStatus(inputVaccinationStatus.getText().toString());
                currentUser.setInsuranceInfo(inputInsuranceInfo.getText().toString());

                // Save the updated health information to the database
                new UpdateUserInfoTask().execute(currentUser);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Emergency Contact");

        final EditText inputName = new EditText(this);
        final EditText inputRelationship = new EditText(this);
        final EditText inputPhone = new EditText(this);

        inputName.setHint("Name");
        inputRelationship.setHint("Relationship");
        inputPhone.setHint("Phone Number");

        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        inputRelationship.setInputType(InputType.TYPE_CLASS_TEXT);
        inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputName);
        layout.addView(inputRelationship);
        layout.addView(inputPhone);

        builder.setView(layout);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = inputName.getText().toString();
                String relationship = inputRelationship.getText().toString();
                String phone = inputPhone.getText().toString();

                if (!name.isEmpty() && !relationship.isEmpty() && !phone.isEmpty()) {
                    EmergencyContact contact = new EmergencyContact();
                    contact.setUserId(userId);
                    contact.setName(name);
                    contact.setRelationship(relationship);
                    contact.setPhone(phone);
                    new InsertEmergencyContactTask().execute(contact);
                } else {
                    Toast.makeText(ProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showEditContactDialog() {
        if (emergencyContacts.isEmpty()) {
            Toast.makeText(this, "No contacts to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] contactNames = new String[emergencyContacts.size()];
        for (int i = 0; i < emergencyContacts.size(); i++) {
            contactNames[i] = emergencyContacts.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Contact to Edit");

        builder.setItems(contactNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditContactDetailsDialog(which);
            }
        });

        builder.show();
    }

    private void showEditContactDetailsDialog(final int contactIndex) {
        final EmergencyContact contact = emergencyContacts.get(contactIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Emergency Contact");

        final EditText inputName = new EditText(this);
        final EditText inputRelationship = new EditText(this);
        final EditText inputPhone = new EditText(this);

        inputName.setHint("Name");
        inputRelationship.setHint("Relationship");
        inputPhone.setHint("Phone Number");

        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        inputRelationship.setInputType(InputType.TYPE_CLASS_TEXT);
        inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        inputName.setText(contact.getName());
        inputRelationship.setText(contact.getRelationship());
        inputPhone.setText(contact.getPhone());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputName);
        layout.addView(inputRelationship);
        layout.addView(inputPhone);

        builder.setView(layout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = inputName.getText().toString();
                String relationship = inputRelationship.getText().toString();
                String phone = inputPhone.getText().toString();

                if (!name.isEmpty() && !relationship.isEmpty() && !phone.isEmpty()) {
                    contact.setName(name);
                    contact.setRelationship(relationship);
                    contact.setPhone(phone);
                    new UpdateEmergencyContactTask().execute(contact);
                } else {
                    Toast.makeText(ProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showDeleteContactDialog() {
        if (emergencyContacts.isEmpty()) {
            Toast.makeText(this, "No contacts to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] contactNames = new String[emergencyContacts.size()];
        for (int i = 0; i < emergencyContacts.size(); i++) {
            contactNames[i] = emergencyContacts.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Contact to Delete");

        builder.setItems(contactNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EmergencyContact contact = emergencyContacts.remove(which);
                llEmergencyContacts.removeViewAt(which);
                new DeleteEmergencyContactTask().execute(contact);
            }
        });

        builder.show();
    }

    private void showMaxContactsWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("You can't have more than 4 emergency contacts.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private class UpdateUserInfoTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            UserDao userDao = AppDatabase.getDatabase(ProfileActivity.this).userDao();
            userDao.update(users[0]);
            return null;
        }
    }

    private class InsertEmergencyContactTask extends AsyncTask<EmergencyContact, Void, Void> {
        @Override
        protected Void doInBackground(EmergencyContact... contacts) {
            EmergencyContactDao contactDao = AppDatabase.getDatabase(ProfileActivity.this).emergencyContactDao();
            contactDao.insert(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new LoadEmergencyContactsTask().execute(userId);
        }
    }

    private class UpdateEmergencyContactTask extends AsyncTask<EmergencyContact, Void, Void> {
        @Override
        protected Void doInBackground(EmergencyContact... contacts) {
            EmergencyContactDao contactDao = AppDatabase.getDatabase(ProfileActivity.this).emergencyContactDao();
            contactDao.update(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new LoadEmergencyContactsTask().execute(userId);
        }
    }

    private class DeleteEmergencyContactTask extends AsyncTask<EmergencyContact, Void, Void> {
        @Override
        protected Void doInBackground(EmergencyContact... contacts) {
            EmergencyContactDao contactDao = AppDatabase.getDatabase(ProfileActivity.this).emergencyContactDao();
            contactDao.delete(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new LoadEmergencyContactsTask().execute(userId);
        }
    }
}
