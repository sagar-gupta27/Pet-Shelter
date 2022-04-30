package com.example.petsshelter.Database;

import static android.app.DownloadManager.COLUMN_ID;
import static android.provider.BaseColumns._ID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.petsshelter.Database.PetContract.BaseContract;
import androidx.annotation.Nullable;

public class PetDbHelperClass extends SQLiteOpenHelper {
    public static final String LOG_TAG = PetDbHelperClass.class.getSimpleName();

    public static  final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME ="PetShelter.db";


    public PetDbHelperClass(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + BaseContract.TABLE_NAME + " ("
                + BaseContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BaseContract.COLUMN_NAME + " TEXT NOT NULL,"
                + BaseContract.COLUMN_BREED + " TEXT,"
                + BaseContract.COLUMN_GENDER + " INTEGER NOT NULL,"
                + BaseContract.COLUMN_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

       sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
