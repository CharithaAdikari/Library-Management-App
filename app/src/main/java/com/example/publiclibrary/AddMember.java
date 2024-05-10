package com.example.publiclibrary;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddMember extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_member);

        EditText memberName = findViewById(R.id.txtmname);
        EditText memberEmail = findViewById(R.id.txtmemail);
        EditText memberPhone = findViewById(R.id.txtmphone);
        EditText memberAddress = findViewById(R.id.txtmaddress);
        Button addMember = findViewById(R.id.btnaddmember);

        // add member button click listener
        addMember.setOnClickListener(v -> {
            String name = memberName.getText().toString().trim();
            String email = memberEmail.getText().toString().trim();
            String phone = memberPhone.getText().toString().trim();
            String address = memberAddress.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try (DbHelper dbHelper = new DbHelper(AddMember.this)) {
                dbHelper.addMember(name, email, phone, address);
                Toast.makeText(this, "Member added successfully", Toast.LENGTH_SHORT).show();
                memberName.setText("");
                memberEmail.setText("");
                memberPhone.setText("");
                memberAddress.setText("");

            } catch (Exception e) {
                Log.e("AddMember", "Error adding member", e);
                Toast.makeText(this, "Error adding member", Toast.LENGTH_SHORT).show();
            }

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
