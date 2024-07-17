package com.example.quickaid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
//formdan veriyi alıyor,
public class EmergencyContactAdapter extends ArrayAdapter<EmergencyContact> {
    public EmergencyContactAdapter(Context context, ArrayList<EmergencyContact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmergencyContact contact = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_emergency_contact, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvContactName);
        TextView tvRelationship = convertView.findViewById(R.id.tvContactRelationship);
        TextView tvPhone = convertView.findViewById(R.id.tvContactPhone);

        tvName.setText(contact.getName());
        tvRelationship.setText(contact.getRelationship());
        tvPhone.setText(contact.getPhone());

        return convertView;
    }
}
