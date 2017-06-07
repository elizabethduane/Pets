package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class PetContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PetContract() {}

    /* Inner class that defines the table contents */
    public static class PetEntry implements BaseColumns {
        public static final String TABLE_NAME = "pets";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BREED = "breed";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT = "weight";

        /**
         * Possible values for the gender.
         */
        public static final int UNKNOW = 0;
        public static final int MALE = 1;
        public static final int FEMALE = 2;

    }
}
