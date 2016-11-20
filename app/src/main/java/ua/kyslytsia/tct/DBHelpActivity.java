package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class DBHelpActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView gender, type, distance, stage, competition, person, judge, member, attempt, stageOnCompetition, StageOnAttempt;
    CursorAdapter adapterGender;
    SimpleCursorAdapter personAdapter;

    DbHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbhelp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("ДБ хелп!");
        setSupportActionBar(toolbar);

        dbHelper = new DbHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        // Gender Table Use
        gender = (ListView) findViewById(R.id.listViewGender);

        String[] fromGender = new String[]{Contract.GenderEntry._ID.toString(), Contract.GenderEntry.COLUMN_GENDER.toString()};
        int[] toGender = new int[]{R.id.textViewGenderId, R.id.textViewGenderName};

        Cursor cursorGender = sqLiteDatabase.query(Contract.GenderEntry.TABLE_NAME, null, null, null, null, null, null);
        adapterGender = new SimpleCursorAdapter(this, R.layout.item_gender, cursorGender, fromGender, toGender, 1);
        gender.setAdapter(adapterGender);

        // Type Table
        type = (ListView) findViewById(R.id.listViewType);

        // Distance Table
        distance = (ListView) findViewById(R.id.listViewDistance);
        String[] fromDistance = new String[] {Contract.DistanceEntry._ID, Contract.DistanceEntry.COLUMN_NAME, "type_name"};
        int[] toDistance = new int[]{R.id.textViewDistHelpId, R.id.textViewDistHelpName, R.id.textViewDistHelpTypeId};
//        Cursor cursorDistance = sqLiteDatabase.query(Contract.DistanceEntry.TABLE_NAME, null, null, null, null, null, null);
        Cursor cursorDistance = sqLiteDatabase.rawQuery("SELECT distance._id, distance.name, type.name AS type_name FROM " + Contract.DistanceEntry.TABLE_NAME + " INNER JOIN " + Contract.TypeEntry.TABLE_NAME + " ON distance." + Contract.DistanceEntry.COLUMN_TYPE_ID + " = type." + Contract.TypeEntry._ID, null);
        SimpleCursorAdapter adapterDistance = new SimpleCursorAdapter(this, R.layout.item_distance_help, cursorDistance, fromDistance, toDistance, 1);
        distance.setAdapter(adapterDistance);

        // StageOnCompetition table
        stage = (ListView) findViewById(R.id.listViewStage);

        // StageOnCompetition Table
//        stageOnCompetition = (ListView) findViewById(R.id.listViewStageOnCompetition);
//        String[] fromSOC = new String[] {Contract.StageOnCompetitionEntry._ID, Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID};
//        int[] toSOC = new int[] {R.id.textViewSOCId, R.id.textViewSOCCompId, R.id.textViewSOCStageId};
//        Cursor cursorSOC = sqLiteDatabase.query(Contract.StageOnCompetitionEntry.TABLE_NAME, null, null, null, null, null, null);
//        SimpleCursorAdapter adapterSOC = new SimpleCursorAdapter(this, R.layout.item_stage_on_comp, cursorSOC, fromSOC, toSOC, 1);
//        stageOnCompetition.setAdapter(adapterSOC);

        // Person Table w.ContentProvider

        person = (ListView) findViewById(R.id.listViewPerson);
        getSupportLoaderManager().initLoader(0, null, this);
        String[] fromPerson = new String[] {Contract.PersonEntry._ID, Contract.PersonEntry.COLUMN_FIRST_NAME, Contract.PersonEntry.COLUMN_LAST_NAME};
        int[] toPerson = new int[] {R.id.textView_personId, R.id.textView_personFirstName, R.id.textView_personLastName};
        personAdapter = new SimpleCursorAdapter(this, R.layout.item_person, null, fromPerson, toPerson, 0);
        person.setAdapter(personAdapter);

        Button buttonDeleteAll = (Button) findViewById(R.id.buttonDbHelpDeleteAll);
        buttonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DBHelpActivity.this);
                alertDialog.setTitle("Очистить все таблицы?");
                alertDialog.setNegativeButton("Отмена", null);
                alertDialog.setPositiveButton("Все очистить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.onUpgrade(sqLiteDatabase, 6, 7);
                    }
                });
                alertDialog.create();
                alertDialog.show();

            }
        });
    }

    public void addGender(View v) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.GenderEntry.COLUMN_GENDER, "Female");
        sqLiteDatabase.insert(Contract.GenderEntry.TABLE_NAME, null, cv);
    }

    public void deleteGender(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Contract.PersonEntry._ID, Contract.PersonEntry.COLUMN_FIRST_NAME, Contract.PersonEntry.COLUMN_LAST_NAME};
        CursorLoader cursorLoader = new CursorLoader(this, ContentProvider.PERSON_CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        personAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        personAdapter.swapCursor(null);
    }
}
