package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import ua.kyslytsia.tct.adapter.AddStageCursorAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class AddStageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView listView;
    private String LOG = "LOG ADD STAGE";
    //DbHelper dbHelper = MainActivity.dbHelper;
    long competitionId;
    long distanceId;
    int lastPosition;
    AddStageCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Добавить этапы");
        setSupportActionBar(toolbar);

        competitionId = getIntent().getLongExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);
        distanceId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, 0);
        Log.d(LOG, "Catch distanceId from SharedPreferences = " + distanceId);
        lastPosition = getIntent().getIntExtra(Contract.StageOnCompetitionEntry.COLUMN_POSITION, 1);
        Log.i(LOG, "Get competition_id from intent = " + competitionId);

        getSupportLoaderManager().initLoader(Contract.ADD_STAGE_LOADER_ID, null, this);
        listView = (ListView) findViewById(R.id.listViewAddStage);
        adapter = new AddStageCursorAdapter(this, null, Contract.ADD_STAGE_LOADER_ID);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AddStageActivity.this, "id = " + id, Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonAddStages = (Button) findViewById(R.id.buttonAddStages);
        buttonAddStages.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                long[] stageIds = listView.getCheckedItemIds();
                int length = stageIds.length;
                Log.i(LOG, "Count of items to add = " + length);
                for (int i = 0; i < stageIds.length; i++) {
                //for (long stageId : stageIds) {
                    //Log.d(LOG, "TRY TO ADD compId = " + competitionId + ", stageId = " + stageId);
                    Log.d(LOG, "TRY TO ADD compId = " + competitionId + ", stageId = " + stageIds[i]);
                    cv.put(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId);
                    cv.put(Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID, stageIds[i]);
                    cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, lastPosition+i+1);
                    //dbHelper.addStageOnCompetition(competitionId, stageIds[i], lastPosition+i);
                    getContentResolver().insert(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv);
                }
                Intent intent = new Intent(AddStageActivity.this, StagesOnCompetitionActivity.class);
                intent.putExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Contract.StageEntry.COLUMN_DISTANCE_ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(distanceId)};
        if (distanceId == Contract.DISTANCE_COMPLEX_ID) {
            selectionArgs = new String[] {String.valueOf(distanceId), String.valueOf(Contract.DISTANCE_FIGURE_ID), String.valueOf(Contract.DISTANCE_CROSS_ID), String.valueOf(Contract.DISTANCE_TRIAL_ID)};
        }
        CursorLoader cursorLoader = new CursorLoader(AddStageActivity.this, ContentProvider.STAGE_CONTENT_URI, null, selection, selectionArgs, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}