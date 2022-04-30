package com.example.petsshelter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.ContentValues;

import android.content.CursorLoader;
import android.content.Intent;

import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.petsshelter.Database.PetContract;
import com.example.petsshelter.Database.cursorAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity implements  android.app.LoaderManager.LoaderCallbacks<Cursor> {


    public static final int PET_LOADER_ID = 0;
    cursorAdapter mCursorAdapter;
    private Loader<Cursor> loader;
    private Cursor d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView pets_list = findViewById(R.id.pets_list);
        View EmptyView = findViewById(R.id.empty_view);
        pets_list.setEmptyView(EmptyView);

        mCursorAdapter = new cursorAdapter(this, null, 0);
        pets_list.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(PET_LOADER_ID, null, this);

        pets_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currPetUri = ContentUris.withAppendedId(PetContract.BaseContract.CONTENT_URI, l);
                intent.setData(currPetUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PET_LOADER_ID, null, this);

    }

    private void insertDummyPet() {

        ContentValues values = new ContentValues();
        values.put(PetContract.BaseContract._ID, 1);
        values.put(PetContract.BaseContract.COLUMN_NAME, "toto");
        values.put(PetContract.BaseContract.COLUMN_BREED, "Bull Dog");
        values.put(PetContract.BaseContract.COLUMN_WEIGHT, 10);
        values.put(PetContract.BaseContract.COLUMN_GENDER, PetContract.BaseContract.GENDER_MALE);

        Uri newUri = getContentResolver().insert(PetContract.BaseContract.CONTENT_URI, values);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetContract.BaseContract.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyPet();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                PetContract.BaseContract._ID,
                PetContract.BaseContract.COLUMN_NAME,
        PetContract.BaseContract.COLUMN_BREED};

        return new CursorLoader(this,PetContract.BaseContract.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}


