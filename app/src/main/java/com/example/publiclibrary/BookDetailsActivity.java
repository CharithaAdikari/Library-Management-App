package com.example.publiclibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BookDetailsActivity extends AppCompatActivity {

    private String bookName;
    private String bookAuthor;
    private String bookPublisher;
    private String member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Get book details from intent
        Intent intent = getIntent();
        bookName = intent.getStringExtra("bookName");
        bookAuthor = intent.getStringExtra("bookAuthor");
        bookPublisher = intent.getStringExtra("bookPublisher");
        member = intent.getStringExtra("member");

        // Display book details
        TextView txtBookName = findViewById(R.id.txtBookName);
        TextView txtBookAuthor = findViewById(R.id.txtBookAuthor);
        TextView txtBookPublisher = findViewById(R.id.txtBookPublisher);
        TextView txtMember = findViewById(R.id.txtBookQuantity);

        txtBookName.setText(bookName);
        txtBookAuthor.setText(bookAuthor);
        txtBookPublisher.setText(bookPublisher);
        txtMember.setText(member);

        // Button for returning the book
        Button btnReturnBook = findViewById(R.id.btnReturnBook);
        btnReturnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle return book action
                returnBook();
            }
        });
    }

    private void returnBook() {

        try(DbHelper dbHelper = new DbHelper(this)){
            dbHelper.returnBook(bookName);
            Toast.makeText(this, "Book returned successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
        catch (Exception e) {
            Log.e("ReturnActivity", "Error occurred while deleting member", e);
            Toast.makeText(this, "Error Return book", Toast.LENGTH_SHORT).show();
        }
    }
}
