package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
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

import java.util.Arrays;

import ua.kyslytsia.tct.adapter.AddStageCursorAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;

public class AddStageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG = "LOG! Add Stage";
    ListView listViewAddStage;
    long competitionId;
    long distanceId;
    int lastElementPosition;
    AddStageCursorAdapter adapterAddStage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stage);
        initToolbar();

        competitionId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);
        distanceId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, 0);
        lastElementPosition = getIntent().getIntExtra(Contract.StageOnCompetitionEntry.COLUMN_POSITION, 1);
        Log.d(LOG, "Get Ids from SharedPreferences: competitionId = " + competitionId + ", distanceId = " + distanceId +
                ". Get lastElementPosition from intent = " + lastElementPosition);

        initAddStageListView();

        Button buttonAddStages = (Button) findViewById(R.id.buttonAddStages);
        if (buttonAddStages != null) {
            buttonAddStages.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addStageToCompetition();
                    Intent intent = new Intent(AddStageActivity.this, StagesOnCompetitionActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void addStageToCompetition() {
        ContentValues cv = new ContentValues();
        long[] stageIdsToAdd = listViewAddStage.getCheckedItemIds();
        Log.d(LOG, "Count of items to add = " + stageIdsToAdd.length);
        for (int i = 0; i < stageIdsToAdd.length; i++) {
            Log.d(LOG, "TRY TO ADD compId = " + competitionId + ", stageId = " + stageIdsToAdd[i]);
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId);
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID, stageIdsToAdd[i]);
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, lastElementPosition+i+1);
            getContentResolver().insert(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle(R.string.add_stage_activity_toolbar_subtitle);
        }
        setSupportActionBar(toolbar);
    }

    private void initAddStageListView() {
        getSupportLoaderManager().initLoader(Contract.ADD_STAGE_LOADER_ID, null, this);
        listViewAddStage = (ListView) findViewById(R.id.listViewAddStage);
        adapterAddStage = new AddStageCursorAdapter(this, null, Contract.ADD_STAGE_LOADER_ID);
        listViewAddStage.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listViewAddStage.setAdapter(adapterAddStage);

        listViewAddStage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO show dialog with info about stage
                Log.i(LOG, "Long click for id = " + id);
                return false;
            }
        });
    }

    public boolean isComplexDistance(long distanceId) {
        return distanceId == Contract.DISTANCE_COMPLEX_ID;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selectionAddStage;
        String[] selectionArgsAddStage;
        Log.i(LOG, "Distance id = " + distanceId);
        if (isComplexDistance(distanceId)) {
            Log.i(LOG, "Complex distance");
            selectionAddStage = Contract.StageEntry.COLUMN_DISTANCE_ID + " IN (?, ?, ?) AND (" +
                    Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "!=? OR " +
                    Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + " IS NULL)";
            selectionArgsAddStage = new String[]{
                    String.valueOf(Contract.DISTANCE_FIGURE_ID),
                    String.valueOf(Contract.DISTANCE_CROSS_ID),
                    String.valueOf(Contract.DISTANCE_TRIAL_ID),
                    String.valueOf(competitionId)};
        } else {
            Log.i(LOG, "Not complex distance");
            selectionAddStage = Contract.StageEntry.COLUMN_DISTANCE_ID + "=? AND (" +
                    Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + "!=? OR " +
                    Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + " IS NULL)";
            selectionArgsAddStage = new String[] {
                    String.valueOf(distanceId),
                    String.valueOf(competitionId)};
        }
        Log.i(LOG, "selectionArgsAddStage = " + Arrays.asList(selectionArgsAddStage));
        return new CursorLoader(this, ContentProvider.STAGE_CONTENT_URI, null, selectionAddStage, selectionArgsAddStage, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapterAddStage.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapterAddStage.swapCursor(null);
    }
}