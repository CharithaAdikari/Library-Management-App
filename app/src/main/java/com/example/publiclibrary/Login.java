package com.example.publiclibrary;

import android.content.Intent;
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

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Declare EditTexts and Button
        EditText email = findViewById(R.id.txtloginemail);
        EditText password = findViewById(R.id.txtloginpassword);
        Button Login = findViewById(R.id.btnlogin);

        Login.setOnClickListener(v -> {
            // Get the text entered by the user in the EditTexts
            String Email = email.getText().toString().trim();
            String Password = password.getText().toString().trim();

            //check if email and password are not empty
            if(!Email.isEmpty() && !Password.isEmpty()){
                try (DbHelper dbHelper = new DbHelper(Login.this)) {
                    //pass email and password to the dbhelper
                    if(dbHelper.loginUser(Email, Password)){
                        Toast.makeText(Login.this, "Login successful !.", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(Login.this, Home.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        // if login is not successful, show an error message
                        Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    Log.e("LoginActivity", "DB Error occurred", e);
                }
            }
        });


        TextView Register = findViewById(R.id.btnswapregister);
        Register.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}