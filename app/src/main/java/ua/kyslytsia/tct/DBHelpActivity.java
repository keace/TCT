package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class DBHelpActivity extends AppCompatActivity {
    ListView gender, type, distance, stage, competition, person, judge, member, attempt, stageOnCompetition, StageOnAttempt;
    EditText genderEdit;
    Button addGender, deleteGender;
    CursorAdapter adapterGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbhelp);

        //Gender Table Use
        gender = (ListView) findViewById(R.id.listViewGender);
        genderEdit = (EditText) findViewById(R.id.editTextGenderAddDelete);
        addGender = (Button) findViewById(R.id.buttonGenderAdd);
        deleteGender = (Button) findViewById(R.id.buttonGenderDelete);

        //
        type = (ListView) findViewById(R.id.listViewType);
        distance = (ListView) findViewById(R.id.listViewDistance);
        stage = (ListView) findViewById(R.id.listViewStage);

        String[] fromGender = new String[]{Contract.GenderEntry._ID.toString(), Contract.GenderEntry.COLUMN_GENDER.toString()};
        int[] toGender = new int[]{R.id.textViewGenderId, R.id.textViewGenderName};

        Cursor cursorGender = DbHelper.sqLiteDatabase.query(Contract.GenderEntry.TABLE_NAME, null, null, null, null, null, null);
        adapterGender = new SimpleCursorAdapter(this, R.layout.item_gender, cursorGender, fromGender, toGender, 1);
        gender.setAdapter(adapterGender);
    }

    public void addGender(View v) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.GenderEntry.COLUMN_GENDER, "Female");
        DbHelper.sqLiteDatabase.insert(Contract.GenderEntry.TABLE_NAME, null, cv);
    }

    public void deleteGender(View v) {

    }
}
