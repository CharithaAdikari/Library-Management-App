package com.example.publiclibrary;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditMember extends AppCompatActivity {

    private String memberName, memberEmail, memberPhone, memberAddress;
    private EditText memberNameEdit, memberEmailEdit, memberPhoneEdit, memberAddressEdit;
    private TextView memberIdView;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_member);

        memberNameEdit = findViewById(R.id.txtupdatemembername);
        memberEmailEdit = findViewById(R.id.txtupdatememberemail);
        memberPhoneEdit = findViewById(R.id.txtupdatememberphone);
        memberAddressEdit = findViewById(R.id.txtupdatememberaddress);
        memberIdView = findViewById(R.id.txtmemberid);
        dbHelper = new DbHelper(this);

        Button btnUpdate = findViewById(R.id.btnupdatemember);
        Button btnDelete = findViewById(R.id.btndeletemember);

        dbHelper = new DbHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            memberName = extras.getString("memberName");
        }
        retrieveDataFromDatabase();
        btnUpdate.setOnClickListener(v -> updateMember());
        btnDelete.setOnClickListener(v -> deleteMember());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void retrieveDataFromDatabase() {
        Cursor cursor = dbHelper.getMemberByName(memberName);
        if (cursor != null && cursor.moveToFirst()) {
            int memberIdIndex = cursor.getColumnIndex(DbHelper.getMemberDetails("MEMBER_ID"));
            int memberEmailIndex = cursor.getColumnIndex(DbHelper.getMemberDetails("MEMBER_EMAIL"));
            int memberPhoneIndex = cursor.getColumnIndex(DbHelper.getMemberDetails("MEMBER_PHONE"));
            int memberAddressIndex = cursor.getColumnIndex(DbHelper.getMemberDetails("MEMBER_ADDRESS"));

            memberIdView.setText(cursor.getString(memberIdIndex));
            memberEmail = cursor.getString(memberEmailIndex);
            memberPhone = cursor.getString(memberPhoneIndex);
            memberAddress = cursor.getString(memberAddressIndex);

            cursor.close();
        }

        memberNameEdit.setText(memberName);
        memberEmailEdit.setText(memberEmail);
        memberPhoneEdit.setText(memberPhone);
        memberAddressEdit.setText(memberAddress);
    }


    private void updateMember() {
        String name = memberNameEdit.getText().toString().trim();
        String email = memberEmailEdit.getText().toString().trim();
        String phone = memberPhoneEdit.getText().toString().trim();
        String address = memberAddressEdit.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int memberIdInt = Integer.parseInt(memberIdView.getText().toString());

        try (DbHelper dbHelper = new DbHelper(this)) {
            dbHelper.updateMember(memberIdInt, name, email, phone, address);
            Toast.makeText(this, "Member updated successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } catch (Exception e) {
            Log.e("EditMemberActivity", "Error occurred while updating member", e);
            Toast.makeText(this, "Error updating member", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMember() {
        int memberIdInt = Integer.parseInt(memberIdView.getText().toString());

        try (DbHelper dbHelper = new DbHelper(this)) {
            dbHelper.deleteMember(memberIdInt);
            Toast.makeText(this, "Member deleted successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
            finish(); // Close the activity after deletion
        } catch (Exception e) {
            Log.e("EditMemberActivity", "Error occurred while deleting member", e);
            Toast.makeText(this, "Error deleting member", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        memberNameEdit.setText("");
        memberEmailEdit.setText("");
        memberPhoneEdit.setText("");
        memberAddressEdit.setText("");
    }
}
