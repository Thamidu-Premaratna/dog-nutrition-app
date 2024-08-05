package com.app.dognutrition.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteUtils extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dog_nutrition.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "SQLiteUtils";

    public SQLiteUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "Creating database and users table...");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, password TEXT, contact TEXT, address TEXT, payment_method TEXT)");
        Log.d(TAG, "Users table created.");

        // Insert default user (admin)
        sqLiteDatabase.execSQL("INSERT INTO users (name, email, password, contact, address, payment_method) VALUES ('Admin', 'admin@gmail.com', 'admin123', '1234567890', '123 Admin Street', 'Cash')");
        Log.d(TAG, "Default user inserted.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        onCreate(sqLiteDatabase);
    }

    public SQLiteDatabase getConnection() {
        Log.d(TAG, "Getting writable database connection...");
        return getWritableDatabase();
    }

    // ------------------- CRUD Operations for the users table -------------------
    // Check if email exists
    public Boolean checkEmail(String email) {
        Log.d(TAG, "Checking if email exists: " + email);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Authenticate user
    public Boolean authenticateUser(String email, String password) {
        Log.d(TAG, "Authenticating user with email: " + email);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        boolean isAuthenticated = cursor.getCount() > 0;
        cursor.close();
        return isAuthenticated;
    }

    // Register user
    public Boolean insertUser(String name, String email, String password) {
        Log.d(TAG, "Inserting new user with email: " + email);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO users (name, email, password) VALUES (?, ?, ?)", new Object[]{name, email, password});
        return true;
    }

    // Get user by email
    public Cursor getUserByEmail(String email) {
        Log.d(TAG, "Getting user by email: " + email);
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
    }

    // ------------------- CRUD Operations for the managing user profile -------------------
    // Update user profile
    public Boolean updateUserProfile(String name, String email, String contact, String address, String paymentMethod) {
        Log.d(TAG, "Updating user profile for email: " + email);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE users SET name = ?, contact = ?, address = ?, payment_method = ? WHERE email = ?", new Object[]{name, contact, address, paymentMethod, email});
        return true;
    }
}