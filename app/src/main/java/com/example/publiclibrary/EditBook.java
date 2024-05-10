package com.example.publiclibrary;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditBook extends AppCompatActivity {

    private String bookName, bookAuthor, bookPublisher, bookQuantity;
    private EditText bookNameEdit, bookAuthorEdit, bookPublisherEdit, bookQuantityEdit;
    private TextView bookIdView;
    private DbHelper dbhelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_book);


        bookNameEdit = findViewById(R.id.txtupdatebookname);
        bookAuthorEdit = findViewById(R.id.txtupdatebookauthor);
        bookPublisherEdit = findViewById(R.id.txtupdatepublication);
        bookQuantityEdit = findViewById(R.id.txtupdatequantity);
        bookIdView = findViewById(R.id.txtbookid);
        dbhelper = new DbHelper(this);

        Button btnUpdate = findViewById(R.id.btnupdate);
        Button btnDelete = findViewById(R.id.btndelete);

        dbhelper = new DbHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bookName = extras.getString("bookName");
        }
        retrieveDataFromDatabase();
        btnUpdate.setOnClickListener(v -> updateBook());
        btnDelete.setOnClickListener(v -> deleteBook());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void retrieveDataFromDatabase() {
        Cursor cursor = dbhelper.getBookByName(bookName);
        if (cursor != null && cursor.moveToFirst()) {
            int bookIdIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_ID"));
            int bookAuthorIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_AUTHOR"));
            int bookPublisherIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_PUBLISHER"));
            int bookQuantityIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_QUANTITY"));
            int bookAvailableIndex = cursor.getColumnIndex(DbHelper.getBookDetails("BOOK_AVAILABLE"));

            bookIdView.setText(cursor.getString(bookIdIndex));
            bookAuthor = cursor.getString(bookAuthorIndex);
            bookPublisher= cursor.getString(bookPublisherIndex);
            bookQuantity = cursor.getString(bookQuantityIndex);

            // Get availability from database and set the switch state accordingly
            boolean isAvailable = cursor.getInt(bookAvailableIndex) == 1; // Assuming availability is stored as boolean
            Switch switchAvailability = findViewById(R.id.switchavailability);
            switchAvailability.setChecked(isAvailable);

            cursor.close();
        }

        bookNameEdit.setText(bookName);
        bookAuthorEdit.setText(bookAuthor);
        bookPublisherEdit.setText(bookPublisher);
        bookQuantityEdit.setText(bookQuantity);
    }


    private void updateBook() {
        String name = bookNameEdit.getText().toString().trim();
        String author = bookAuthorEdit.getText().toString().trim();
        String publication = bookPublisherEdit.getText().toString().trim();
        String quantityStr = bookQuantityEdit.getText().toString().trim();

        // Assuming the switch ID is switchavailability
        Switch switchAvailability = findViewById(R.id.switchavailability);
        boolean isAvailable = switchAvailability.isChecked();

        if (name.isEmpty() || author.isEmpty() || publication.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        int bookIdInt = Integer.parseInt(bookIdView.getText().toString());

        try (DbHelper dbHelper = new DbHelper(this)) {
            dbHelper.updateBook(bookIdInt, name, author, publication, quantity, isAvailable);
            Toast.makeText(this, "Book updated successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } catch (Exception e) {
            Log.e("UpdateBookActivity", "Error occurred while updating book", e);
            Toast.makeText(this, "Error updating book", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteBook() {
        int bookIdInt = Integer.parseInt(bookIdView.getText().toString());

        try (DbHelper dbHelper = new DbHelper(this)) {
            dbHelper.deleteBook(bookIdInt);
            Toast.makeText(this, "Book deleted successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
            finish(); // Close the activity after deletion
        } catch (Exception e) {
            Log.e("EditBookActivity", "Error occurred while deleting book", e);
            Toast.makeText(this, "Error deleting book", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        bookNameEdit.setText("");
        bookAuthorEdit.setText("");
        bookPublisherEdit.setText("");
        bookQuantityEdit.setText("");
        Switch switchAvailability = findViewById(R.id.switchavailability);
        switchAvailability.setChecked(false); // Reset the switch to default state
    }





}