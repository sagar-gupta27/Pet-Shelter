package com.example.petsshelter.Database;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {
    public static final String CONTENT_AUTHORITY = "com.example.petsshelter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "petsshleter";
    private PetContract(){};


    public static abstract class BaseContract implements BaseColumns {

        //URI CONSTANTS

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final String _ID = BaseColumns._ID;
        public static final String TABLE_NAME="PETS";
        public static final String COLUMN_NAME ="NAME";
        public static final String COLUMN_BREED="BREED";
        public static final String COLUMN_WEIGHT="WEIGHT";
        public static final String COLUMN_GENDER="GENDER";

          //CONSTANTS FOR GENDER
        public static final int GENDER_MALE=1;
        public static final int GENDER_FEMALE=2;
        public static final int GENDER_UNKNOWN=0;

        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
    }
}
