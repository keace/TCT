package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class NewCompetitionActivity extends AppCompatActivity {

    private EditText name, place, rank, penalty;
    private Spinner type, distance;
    private Button buttonSaveToStage, buttonSaveToMembers;

    long selectedTypeId, selectedDistanceId;

    ContentValues cv;

    static SimpleCursorAdapter adapterDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_competition);

        name = (EditText) findViewById(R.id.newCompetitionName);
        place = (EditText) findViewById(R.id.newCompetitionPlace);
        rank = (EditText) findViewById(R.id.newDistanceRank);
        penalty = (EditText) findViewById(R.id.newCompetitionPenalty);

        type = (Spinner) findViewById(R.id.newCompetitionTypeSpinner);
        distance = (Spinner) findViewById(R.id.newCompetitionDistanceSpinner);

        buttonSaveToStage = (Button) findViewById(R.id.buttonNewCompetitionSaveToStage);
        buttonSaveToMembers = (Button) findViewById(R.id.buttonNewCompetitionSaveToMembers);

        // spinner Type
        Cursor cursorType = DbHelper.sqLiteDatabase.query(Contract.TypeEntry.TABLE_NAME, null, null, null, null, null, null);
        String[] fromType = new String[]{Contract.TypeEntry.COLUMN_NAME};
        int[] toType = new int[]{R.id.textViewItemType};
        final SimpleCursorAdapter adapterType = new SimpleCursorAdapter(this, R.layout.item_type, cursorType, fromType, toType, 1);

        type.setAdapter(adapterType);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected TYPE id = " + id, Toast.LENGTH_SHORT).show();
                selectedTypeId = id;
                adapterDistance.notifyDataSetChanged();
                //distance.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "TYPE NOT SELECTED", Toast.LENGTH_SHORT).show();
            }
        });
            // Тут падает
        // spinner Distance

        String selection = Contract.DistanceEntry.COLUMN_TYPE_ID + " = ?";
        //String[] selectionArgs = new String[]{String.valueOf(selectedTypeId)};
        String[] selectionArgs = new String[]{"1"};
        Cursor cursorDistance = DbHelper.sqLiteDatabase.query(Contract.DistanceEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        String[] fromDistance = new String[]{Contract.DistanceEntry.COLUMN_DISTANCE_NAME};
        int[] toDistance = new int[]{R.id.textViewItemDistance};
        adapterDistance = new SimpleCursorAdapter(this, R.id.textViewItemDistance, null, null, null, 1);

/*        adapterDistance.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                String selection = Contract.DistanceEntry.COLUMN_TYPE_ID + " = ?";
                String[] selectionArgs = new String[]{String.valueOf(selectedTypeId)};
                Toast.makeText(NewCompetitionActivity.this, "TYPE_ID = " + String.valueOf(selectedTypeId), Toast.LENGTH_SHORT).show();
                Cursor cursorDistance = DbHelper.sqLiteDatabase.query(Contract.DistanceEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                String[] fromDistance = new String[]{Contract.DistanceEntry.COLUMN_DISTANCE_NAME};
                int[] toDistance = new int[]{R.id.textViewItemDistance};
                adapterDistance = new SimpleCursorAdapter(NewCompetitionActivity.this, R.id.textViewItemDistance, cursorDistance, fromDistance, toDistance, 1);
                distance.setAdapter(adapterDistance);
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        });
*/
        distance.setAdapter(adapterDistance);

        distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected DISTANCE id = " + id, Toast.LENGTH_SHORT).show();
                selectedDistanceId = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "DISTANCE NOT SELECTED", Toast.LENGTH_SHORT).show();
            }
        });

        // Button handle
        buttonSaveToMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long inserted;

                // required not null fields handling
                if (name.getText().toString().trim().equals("")){
                    name.setError("Название обязательно!");
                } else if (rank.getText().toString().trim().equals("")) {
                    place.setError("Класс соревнований обязателен!");
                } else if (penalty.getText().toString().trim().equals("")){
                    penalty.setError("Стоимость штрафа обязательна!");
                }

                //inserting to DB
                else {
                    cv.put(Contract.CompetitionEntry.COLUMN_NAME, name.getText().toString());
                    cv.put(Contract.CompetitionEntry.COLUMN_PLACE, place.getText().toString());
                    cv.put(Contract.CompetitionEntry.COLUMN_TYPE_ID, selectedTypeId);
                    cv.put(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, selectedDistanceId);
                    cv.put(Contract.CompetitionEntry.COLUMN_RANK, rank.getText().toString());
                    cv.put(Contract.CompetitionEntry.COLUMN_PENALTY_TIME, Integer.valueOf(penalty.getText().toString()));
                    cv.put(Contract.CompetitionEntry.COLUMN_IS_CLOSED, Contract.COMPETITION_OPENED);
                    inserted = DbHelper.sqLiteDatabase.insert(Contract.CompetitionEntry.TABLE_NAME, null, cv);
                    if (inserted == -1) {
                        Toast.makeText(getApplicationContext(), "Error! New Competition not created!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intentToMainActivity = new Intent(NewCompetitionActivity.this, MainActivity.class);
                        startActivity(intentToMainActivity);
                    }
                    cv.clear();
                }

            }
        });

        buttonSaveToStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Не работает", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void makeNewCompetition() {
//        long inserted;
//
//        // required not null fields handling
//        if (name.getText().toString().trim().equals("")){
//            name.setError("Название обязательно!");
//        } else if (rank.getText().toString().trim().equals("")) {
//            place.setError("Класс соревнований обязателен!");
//        } else if (penalty.getText().toString().trim().equals("")){
//            penalty.setError("Стоимость штрафа обязательна!");
//        }
//
//        //inserting to DB
//        else {
//        cv.put(Contract.CompetitionEntry.COLUMN_NAME, name.getText().toString());
//        cv.put(Contract.CompetitionEntry.COLUMN_PLACE, place.getText().toString());
//        cv.put(Contract.CompetitionEntry.COLUMN_TYPE_ID, selectedTypeId);
//        cv.put(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, selectedDistanceId);
//        cv.put(Contract.CompetitionEntry.COLUMN_RANK, rank.getText().toString());
//        cv.put(Contract.CompetitionEntry.COLUMN_PENALTY_TIME, Integer.valueOf(penalty.getText().toString()));
//        cv.put(Contract.CompetitionEntry.COLUMN_IS_CLOSED, Contract.COMPETITION_OPENED);
//        inserted = DbHelper.sqLiteDatabase.insert(Contract.CompetitionEntry.TABLE_NAME, null, cv);
//            if (inserted == -1) {
//                Toast.makeText(getApplicationContext(), "Error! New Competition not created!", Toast.LENGTH_SHORT).show();
//            }
//        cv.clear();
//        }
//    }
}