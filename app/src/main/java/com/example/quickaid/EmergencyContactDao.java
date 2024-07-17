package com.example.quickaid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface EmergencyContactDao {
    @Insert
    void insert(EmergencyContact emergencyContact);

    @Update
    void update(EmergencyContact emergencyContact);

    @Delete
    void delete(EmergencyContact emergencyContact);

    @Query("SELECT * FROM emergency_contacts WHERE user_id = :userId")
    List<EmergencyContact> getEmergencyContactsForUser(int userId);
}
