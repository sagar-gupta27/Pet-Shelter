package com.example.petsshelter.Database;

import static com.example.petsshelter.Database.PetContract.BaseContract.CONTENT_LIST_TYPE;
import static com.example.petsshelter.Database.PetContract.CONTENT_AUTHORITY;
import static com.example.petsshelter.Database.PetContract.PATH_PETS;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PetProvider extends ContentProvider {


    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS+"/#",PET_ID);
    }
    private  PetDbHelperClass mDbHelper;
    @Override
    public boolean onCreate() {
      mDbHelper = new PetDbHelperClass(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String s1) {
        SQLiteDatabase db =mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match){
            case PETS:
                cursor =db.query(PetContract.BaseContract.TABLE_NAME,projection,selection,selectionArgs,null,null,s1);
                break;
            case PET_ID:
                selection= PetContract.BaseContract._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor= db.query(PetContract.BaseContract.TABLE_NAME,projection,selection,selectionArgs,null,null,s1);
                break;
            default:
                throw new IllegalArgumentException("cannot query unknown uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.BaseContract.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.BaseContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match =sUriMatcher.match(uri);
        switch (match)
        {
            case PETS:
                return insertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("Cannot insert the data "+ uri);
        }

    }

    private Uri insertPet(Uri uri, ContentValues values) {

        //check for name validity
        String name = values.getAsString(PetContract.BaseContract.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Check that the gender is valid
        Integer gender = values.getAsInteger(PetContract.BaseContract.COLUMN_GENDER);
        if (gender == null || !PetContract.BaseContract.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetContract.BaseContract.COLUMN_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
        PetDbHelperClass mDbHelper = new PetDbHelperClass(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
         long id = (int) db.insert(PetContract.BaseContract.TABLE_NAME,null,values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
      int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                rowsDeleted= database.delete(PetContract.BaseContract.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetContract.BaseContract._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PetContract.BaseContract.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
            return rowsDeleted;

    }
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                selection = PetContract.BaseContract._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(PetContract.BaseContract.COLUMN_NAME)) {
            String name = values.getAsString(PetContract.BaseContract.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetContract.BaseContract#COLUMN_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetContract.BaseContract.COLUMN_GENDER)) {
            Integer gender = values.getAsInteger(PetContract.BaseContract.COLUMN_GENDER);
            if (gender == null || !PetContract.BaseContract.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetContract.BaseContract#COLUMN_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetContract.BaseContract.COLUMN_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetContract.BaseContract.COLUMN_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

          int rowsUpdated = mDbHelper.getWritableDatabase().update(PetContract.BaseContract.TABLE_NAME,values,selection,selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
