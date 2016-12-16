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

import ua.kyslytsia.tct.adapter.AddStageCursorAdapter;
import ua.kyslytsia.tct.database.ContentProvider;
import ua.kyslytsia.tct.database.Contract;

public class AddStageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String LOG = "LOG! Add Stage";

    private long mCompetitionId;
    private long mDistanceId;
    private int mLastElementPosition;
    private ListView mListViewAddStage;
    private AddStageCursorAdapter mAdapterAddStage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stage);
        initToolbar();

        mCompetitionId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);
        mDistanceId = PreferenceManager.getDefaultSharedPreferences(this).getLong(Contract.CompetitionEntry.COLUMN_DISTANCE_ID, 0);
        mLastElementPosition = getIntent().getIntExtra(Contract.StageOnCompetitionEntry.COLUMN_POSITION, 0);
        Log.d(LOG, "Get Ids from SharedPreferences: mCompetitionId = " + mCompetitionId +
                ", mDistanceId = " + mDistanceId +
                ". Get mLastElementPosition from intent = " + mLastElementPosition);

        initAddStageListView();
        initButtons();
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
        mListViewAddStage = (ListView) findViewById(R.id.listViewAddStage);
        mAdapterAddStage = new AddStageCursorAdapter(this, null, Contract.ADD_STAGE_LOADER_ID);
        mListViewAddStage.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mListViewAddStage.setAdapter(mAdapterAddStage);

        mListViewAddStage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO show dialog with info about stage
                Log.i(LOG, "Long click for id = " + id);
                return false;
            }
        });
    }

    private void initButtons() {
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
        long[] stageIdsToAdd = mListViewAddStage.getCheckedItemIds();
        Log.d(LOG, "Count of items to add = " + stageIdsToAdd.length);
        for (int i = 0; i < stageIdsToAdd.length; i++) {
            Log.d(LOG, "TRY TO ADD compId = " + mCompetitionId + ", stageId = " + stageIdsToAdd[i]);
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, mCompetitionId);
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_STAGE_ID, stageIdsToAdd[i]);
            cv.put(Contract.StageOnCompetitionEntry.COLUMN_POSITION, mLastElementPosition +i+1);
            getContentResolver().insert(ContentProvider.STAGE_ON_COMPETITION_CONTENT_URI, cv);
        }
    }

    public boolean isComplexDistance(long distanceId) {
        long complexDistanceId = PreferenceManager.getDefaultSharedPreferences(this).getLong(getString(R.string.bike_distance_complex), 0);
        return distanceId == complexDistanceId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selectionAddStage;
        String[] selectionArgsAddStage;
        Log.i(LOG, "Distance id = " + mDistanceId);
        if (isComplexDistance(mDistanceId)) {
            Log.i(LOG, "Complex distance");

            String figureRideId = String.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getLong(getString(R.string.bike_distance_figure_ride), 0));
            String crossId = String.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getLong(getString(R.string.bike_distance_cross), 0));
            String trialId = String.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getLong(getString(R.string.bike_distance_trial), 0));

            selectionAddStage = Contract.StageEntry.COLUMN_DISTANCE_ID + " IN (?, ?, ?)";
            selectionArgsAddStage = new String[]{
                    figureRideId,
                    crossId,
                    trialId};
        } else {
            Log.i(LOG, "Not complex distance");
            selectionAddStage = Contract.StageEntry.COLUMN_DISTANCE_ID + "=?";
            selectionArgsAddStage = new String[] {
                    String.valueOf(mDistanceId)};
        }
        return new CursorLoader(this, ContentProvider.STAGE_CONTENT_URI, null, selectionAddStage, selectionArgsAddStage, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapterAddStage.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapterAddStage.swapCursor(null);
    }
}