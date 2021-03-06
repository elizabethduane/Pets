/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    final static int ADD = 0;
    final static int EDIT = 1;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.

    // Create database helper
    private PetDbHelper mDbHelper;


    private int status;

    private String idPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();

        mDbHelper = new PetDbHelper(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null){

            this.setTitle("Edit Pet");

            //To edit
            idPet = bundle.get("id").toString();
            loadData(idPet);
            status = EDIT;

        }else{

            status = ADD;

        }
    }

    /**
     * Setup the dropdown spinner that allows the user to selec t the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.FEMALE; // Female
                    } else {
                        mGender = PetEntry.UNKNOW; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    private void insertPet(){
        //trim remove the blanck spaces
        String name = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();
        String sWeight = mWeightEditText.getText().toString().trim();

        int weight = Integer.parseInt(sWeight);

        // Create database helper
        PetDbHelper mDbHelper = new PetDbHelper(this);

        // Create and/or open a database to write from it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //create the object no be included
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_NAME, name);
        values.put(PetEntry.COLUMN_BREED, breed);
        values.put(PetEntry.COLUMN_GENDER, mGender);
        values.put(PetEntry.COLUMN_WEIGHT, weight);

        long newRowId = db.insert(PetEntry.TABLE_NAME, null, values);


       if (newRowId==-1){
           Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
       }else{
           Toast.makeText(this, "Pet saved", Toast.LENGTH_SHORT).show();

       }

    }

    private void loadData(String id){

        int gender;
        String sWeight, name, breed;

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String whereClause =  PetEntry._ID + "="+ id;

        Cursor cursor = db.query(PetEntry.TABLE_NAME,null,whereClause,null,null,null,null);

        if(cursor != null){

            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_NAME));
            mNameEditText.setText(name);

            breed = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_BREED));
            mBreedEditText.setText(breed);

            gender = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_GENDER));
            mGenderSpinner.setSelection(gender);

            sWeight = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_WEIGHT));
            mWeightEditText.setText(sWeight);

        }else{
            Toast.makeText(this, "Error with opening pet", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePet(String id){

        //trim remove the blanck spaces
        String name = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();
        String sWeight = mWeightEditText.getText().toString().trim();

        int weight = Integer.parseInt(sWeight);

        // Create and/or open a database to write from it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //create the object no be included
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_NAME, name);
        values.put(PetEntry.COLUMN_BREED, breed);
        values.put(PetEntry.COLUMN_GENDER, mGender);
        values.put(PetEntry.COLUMN_WEIGHT, weight);

        String whereClause =  PetEntry._ID + "="+ id;

        long newRowId = db.update(PetEntry.TABLE_NAME,values,whereClause,null);


        if (newRowId==-1){
            Toast.makeText(this, "Error with to update pet", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Pet updated", Toast.LENGTH_SHORT).show();

        }

    }

    private void deletePet(String id){

        // Create and/or open a database to write from it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String whereClause =  PetEntry._ID + "="+ id;

        long newRowId = db.delete(PetEntry.TABLE_NAME,whereClause,null);


        if (newRowId==-1){
            Toast.makeText(this, "Error on deleting pet", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Pet deleted", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save on database
                if(status == ADD)
                    insertPet();
                else
                    updatePet(idPet);

                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                deletePet(idPet);
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}