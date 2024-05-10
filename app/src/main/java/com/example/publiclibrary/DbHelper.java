package com.example.publiclibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "LDB";
    private static final int DB_VERSION = 1;
    private static final String USER_TABLE = "user";
    private static final String BOOK_TABLE = "book";
    private static final String TABLE_MEMBER = "members";

    // User table constants
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String CONFIRM_PASSWORD = "confirm_password";

    // Book table constants
    private static final String BOOK_ID = "bid";
    private static final String BOOK_NAME = "bname";
    private static final String BOOK_AUTHOR = "bauthor";
    private static final String BOOK_PUBLISHER = "bpublisher";
    private static final String BOOK_QUANTITY = "bquantity";
    private static final String BOOK_AVAILABLE = "bavailable";

    // Member Table Columns names
    private static final String MEMBER_ID = "id";
    private static final String MEMBER_NAME = "name";
    private static final String MEMBER_EMAIL = "email";
    private static final String MEMBER_PHONE = "phone";
    private static final String MEMBER_ADDRESS = "address";

    // Reserve table constants
    private static final String RESERVE_TABLE = "book_reserve";
    private static final String RESERVE_ID = "reserve_id";
    private static final String RESERVE_BOOK_ID = "book_id"; // Foreign key referencing BOOK_TABLE
    private static final String RESERVE_MEMBER_NAME = "member_name"; // Foreign key referencing MEMBER_TABLE


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT, "
                + EMAIL + " TEXT, "
                + PASSWORD + " TEXT, "
                + CONFIRM_PASSWORD + " TEXT"
                + ")";
        db.execSQL(createUserTableQuery);

        String createBookTableQuery = "CREATE TABLE IF NOT EXISTS " + BOOK_TABLE + "("
                + BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BOOK_NAME + " TEXT, "
                + BOOK_AUTHOR + " TEXT, "
                + BOOK_PUBLISHER + " TEXT, "
                + BOOK_QUANTITY + " INTEGER, "
                + BOOK_AVAILABLE + " BOOL"
                + ")";
        db.execSQL(createBookTableQuery);

        String CREATE_MEMBER_TABLE = "CREATE TABLE " + TABLE_MEMBER + "("
                + MEMBER_ID + " INTEGER PRIMARY KEY,"
                + MEMBER_NAME + " TEXT,"
                + MEMBER_EMAIL + " TEXT,"
                + MEMBER_PHONE + " TEXT,"
                + MEMBER_ADDRESS + " TEXT"
                + ")";
        db.execSQL(CREATE_MEMBER_TABLE);

        String createReserveTableQuery = "CREATE TABLE IF NOT EXISTS " + RESERVE_TABLE + "("
                + RESERVE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RESERVE_BOOK_ID + " INTEGER, "
                + RESERVE_MEMBER_NAME + " TEXT, "
                + "FOREIGN KEY(" + RESERVE_BOOK_ID + ") REFERENCES " + BOOK_TABLE + "(" + BOOK_ID + "), "
                + "FOREIGN KEY(" + RESERVE_MEMBER_NAME + ") REFERENCES " + TABLE_MEMBER + "(" + MEMBER_NAME + ")"
                + ")";
        db.execSQL(createReserveTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
        onCreate(db);
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isLoggedIn = false;

        try {
            String selectQuery = "SELECT * FROM " + USER_TABLE + " WHERE " + EMAIL + " = ? AND " + PASSWORD + " = ?";
            cursor = db.rawQuery(selectQuery, new String[]{email, password});
            if (cursor != null && cursor.getCount() > 0) {
                isLoggedIn = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return isLoggedIn;
    }

    public void registerUser(String name, String email, String password, String confirmPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(EMAIL, email);
        values.put(PASSWORD, password);
        values.put(CONFIRM_PASSWORD, confirmPassword);
        db.insert(USER_TABLE, null, values);
        db.close();
    }

    public static String getBookDetails(String detailType) {
        switch (detailType) {
            case "BOOK_ID":
                return BOOK_ID;
            case "BOOK_NAME":
                return BOOK_NAME;
            case "BOOK_AUTHOR":
                return BOOK_AUTHOR;
            case "BOOK_PUBLISHER":
                return BOOK_PUBLISHER;
            case "BOOK_QUANTITY":
                return BOOK_QUANTITY;
            case "BOOK_AVAILABLE":
                return BOOK_AVAILABLE;
            default:
                return null;
        }
    }

    public static String getMemberDetails(String detailType) {
        switch (detailType) {
            case "MEMBER_ID":
                return MEMBER_ID;
            case "MEMBER_NAME":
                return MEMBER_NAME;
            case "MEMBER_EMAIL":
                return MEMBER_EMAIL;
            case "MEMBER_PHONE":
                return MEMBER_PHONE;
            case "MEMBER_ADDRESS":
                return MEMBER_ADDRESS;
            default:
                return null;
        }
    }

    public void addBook(String bookName, String bookAuthor, String bookPublisher, int bookQuantity, boolean bookAvailable) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BOOK_NAME, bookName);
        values.put(BOOK_AUTHOR, bookAuthor);
        values.put(BOOK_PUBLISHER, bookPublisher);
        values.put(BOOK_QUANTITY, bookQuantity);
        values.put(BOOK_AVAILABLE, bookAvailable);

        long result = db.insert(BOOK_TABLE, null, values);
        if (result == -1) {
            Log.d("DbHelper", "Failed to add book");
        } else {
            Log.d("DbHelper", "Book added successfully");
        }
        db.close();
    }

    public void updateBook(int bookId, String bookName, String bookAuthor, String bookPublisher, int bookQuantity, boolean bookAvailable) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BOOK_NAME, bookName);
        values.put(BOOK_AUTHOR, bookAuthor);
        values.put(BOOK_PUBLISHER, bookPublisher);
        values.put(BOOK_QUANTITY, bookQuantity);
        values.put(BOOK_AVAILABLE, bookAvailable);

        String selection = BOOK_ID + " = ?";
        String[] selectionArgs = {String.valueOf(bookId)};

        int result = db.update(BOOK_TABLE, values, selection, selectionArgs);
        if (result > 0) {
            Log.d("DbHelper", "Book updated successfully");
        } else {
            Log.d("DbHelper", "Failed to update book");
        }
        db.close();
    }

    public void deleteBook(int bookId) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = BOOK_ID + " = ?";
        String[] selectionArgs = {String.valueOf(bookId)};

        int result = db.delete(BOOK_TABLE, selection, selectionArgs);
        if (result > 0) {
            Log.d("DbHelper", "Book deleted successfully");
        } else {
            Log.d("DbHelper", "Failed to delete book");
        }
        db.close();
    }

    public Cursor getBookByName(String book_name) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {BOOK_ID, BOOK_NAME, BOOK_AUTHOR, BOOK_PUBLISHER, BOOK_QUANTITY, BOOK_AVAILABLE};
        String selection = BOOK_NAME + " = ?";
        String[] selectionArgs = {book_name};
        return db.query(BOOK_TABLE, projection, selection, selectionArgs, null, null, null);
    }

    public Cursor getAllBook() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + BOOK_TABLE;
        return db.rawQuery(selectQuery, null);
    }

    public void addMember(String name, String email, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MEMBER_NAME, name);
        values.put(MEMBER_EMAIL, email);
        values.put(MEMBER_PHONE, phone);
        values.put(MEMBER_ADDRESS, address);

        // Inserting Row
        db.insert(TABLE_MEMBER, null, values);
        db.close();
    }

    public Cursor getAllMembers() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_MEMBER;
        return db.rawQuery(selectQuery, null);
    }
    public Cursor getMemberByName(String memberName) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {MEMBER_ID, MEMBER_NAME, MEMBER_EMAIL, MEMBER_PHONE, MEMBER_ADDRESS};
        String selection = MEMBER_NAME + " = ?";
        String[] selectionArgs = {memberName};
        return db.query(TABLE_MEMBER, projection, selection, selectionArgs, null, null, null);
    }

    public void updateMember(int memberId, String name, String email, String phone, String address) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MEMBER_NAME, name);
        values.put(MEMBER_EMAIL, email);
        values.put(MEMBER_PHONE, phone);
        values.put(MEMBER_ADDRESS, address);

        String selection = MEMBER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(memberId)};

        int result = db.update(TABLE_MEMBER, values, selection, selectionArgs);
        if (result > 0) {
            Log.d("DbHelper", "Member updated successfully");
        } else {
            Log.d("DbHelper", "Failed to update member");
        }
        db.close();
    }

    public void deleteMember(int memberId) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = MEMBER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(memberId)};

        int result = db.delete(TABLE_MEMBER, selection, selectionArgs);
        if (result > 0) {
            Log.d("DbHelper", "Member deleted successfully");
        } else {
            Log.d("DbHelper", "Failed to delete member");
        }
        db.close();
    }


    public void reserveBook(int bookId, String memberName) {
        SQLiteDatabase db = getWritableDatabase();

        // Decrement book quantity
        Cursor cursor = db.query(BOOK_TABLE, new String[]{BOOK_QUANTITY}, BOOK_ID + "=?", new String[]{String.valueOf(bookId)}, null, null, null);
        int bookQuantity = 0;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(BOOK_QUANTITY);
            if (columnIndex >= 0) { // Check if column exists
                bookQuantity = cursor.getInt(columnIndex);
            }
            cursor.close();
        }
        if (bookQuantity > 0) {
            ContentValues bookValues = new ContentValues();
            bookValues.put(BOOK_QUANTITY, bookQuantity - 1);
            db.update(BOOK_TABLE, bookValues, BOOK_ID + "=?", new String[]{String.valueOf(bookId)});

            // Insert into book reserve table
            ContentValues reserveValues = new ContentValues();
            reserveValues.put(RESERVE_BOOK_ID, bookId);
            reserveValues.put(RESERVE_MEMBER_NAME, memberName);
            db.insert(RESERVE_TABLE, null, reserveValues);
        }

        db.close();
    }

    public Cursor getAllReservedBooks() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT " +
                BOOK_TABLE + "." + BOOK_NAME + ", " +
                BOOK_TABLE + "." + BOOK_AUTHOR + ", " +
                BOOK_TABLE + "." + BOOK_PUBLISHER + ", " +
                RESERVE_TABLE + "." + RESERVE_MEMBER_NAME +
                " FROM " + BOOK_TABLE +
                " INNER JOIN " + RESERVE_TABLE +
                " ON " + BOOK_TABLE + "." + BOOK_ID + " = " + RESERVE_TABLE + "." + RESERVE_BOOK_ID;
        return db.rawQuery(selectQuery, null);
    }


    public static String getReservedBookDetails(String reservedBy) {
        switch (reservedBy) {
            case "RESERVE_ID":
                return RESERVE_ID;
            case "RESERVE_BOOK_ID":
                return RESERVE_BOOK_ID;
            case "RESERVE_MEMBER_NAME":
                return RESERVE_MEMBER_NAME;
            default:
                return null;
        }
    }// Inside DbHelper class
    public void returnBook(String bookName) {
        SQLiteDatabase db = getWritableDatabase();

        // Delete reservation entry
        db.delete(RESERVE_TABLE, RESERVE_BOOK_ID + " = (SELECT " + BOOK_ID + " FROM " + BOOK_TABLE +
                " WHERE " + BOOK_NAME + " = ?)", new String[]{bookName});

        // Increase book quantity
        db.execSQL("UPDATE " + BOOK_TABLE + " SET " + BOOK_QUANTITY + " = " + BOOK_QUANTITY + " + 1" +
                " WHERE " + BOOK_NAME + " = ?", new String[]{bookName});

        db.close();
    }

    public List<String> getBookSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {BOOK_NAME};
        String selection = BOOK_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%"};

        Cursor cursor = db.query(BOOK_TABLE, projection, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(BOOK_NAME);
                if (columnIndex != -1) {
                    String bookName = cursor.getString(columnIndex);
                    suggestions.add(bookName);
                }
            }
            cursor.close();
        }
        db.close();
        return suggestions;
    }





}
