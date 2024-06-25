package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Recipe.db";
    public static final String TABLE_NAME = "recipe_table";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "CATEGORY";
    public static final String COL_4 = "DESCRIPTION";
    public static final String COL_5 = "RECIPE";

    // Users table columns
    public static final String USER_TABLE_NAME = "user_table";
    public static final String USER_COL_1 = "USER_ID";
    public static final String USER_COL_2 = "EMAIL";
    public static final String USER_COL_3 = "PASSWORD";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, CATEGORY TEXT, DESCRIPTION TEXT, RECIPE TEXT)");
        db.execSQL("CREATE TABLE " + USER_TABLE_NAME + " (USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, PASSWORD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(db);
    }

    public Cursor getRecipesByCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        if (category.isEmpty()) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        } else {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE CATEGORY = ?", new String[]{category});
        }
        return cursor;
    }

    public Cursor getRecipesByCategoryAndQuery(String category, String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        if (category.isEmpty()) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE NAME LIKE ? OR DESCRIPTION LIKE ?", new String[]{"%" + query + "%", "%" + query + "%"});
        } else {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE CATEGORY = ? AND (NAME LIKE ? OR DESCRIPTION LIKE ?)", new String[]{category, "%" + query + "%", "%" + query + "%"});
        }
        return cursor;
    }

    public void deleteRecipe(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID = ?", new String[]{String.valueOf(id)});
    }

    public boolean insertRecipe(String name, String category, String description, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, category);
        contentValues.put(COL_4, description);
        contentValues.put(COL_5, image);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // if result == -1, insertion failed, else it was successful
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE EMAIL = ? AND PASSWORD = ?", new String[]{email, password});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_2, email);
        contentValues.put(USER_COL_3, password);

        long result = db.insert(USER_TABLE_NAME, null, contentValues);
        return result != -1; // if result == -1, insertion failed, else it was successful
    }
}
