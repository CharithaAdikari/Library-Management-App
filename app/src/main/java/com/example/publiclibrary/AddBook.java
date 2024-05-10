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

public class AddBook extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_book);

        EditText bookName = findViewById(R.id.txtbname);
        EditText bookAuthor = findViewById(R.id.txtbauthor);
        EditText bookPublication = findViewById(R.id.txtbpublication);
        EditText bookQuantity = findViewById(R.id.txtquntity);
        Button addBook  = findViewById(R.id.btnadd);

        // add book button click listener
        addBook.setOnClickListener(v -> {
            String name = bookName.getText().toString().trim();
            String author = bookAuthor.getText().toString().trim();
            String publication = bookPublication.getText().toString().trim();
            String quantityStr = bookQuantity.getText().toString().trim();

            if (name.isEmpty() || author.isEmpty() || publication.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);

            boolean available = quantity > 0;


            try(DbHelper dbHelper = new DbHelper(AddBook.this)) {
                dbHelper.addBook(name, author, publication, quantity, available);
                Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show();
                bookName.setText("");
                bookAuthor.setText("");
                bookPublication.setText("");
                bookQuantity.setText("");

            } catch (Exception e) {
                Log.e("AddBook", "Error adding book", e);
                Toast.makeText(this, "Error adding book", Toast.LENGTH_SHORT).show();
            }

        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}