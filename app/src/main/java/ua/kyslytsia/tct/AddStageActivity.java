package ua.kyslytsia.tct;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
    DbHelper dbHelper = MainActivity.dbHelper;
    int competitionId, distanceId;
    AddStageCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Добавить этапы");
        setSupportActionBar(toolbar);

        competitionId = getIntent().getIntExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);

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
                long[] stageIds = listView.getCheckedItemIds();
                for (long stageId : stageIds) {
                    Log.d(LOG, "TRY TO ADD compId = " + competitionId + ", stageId = " + stageId);
                    dbHelper.addStageOnCompetition(competitionId, (int) stageId);
                }
                Intent intent = new Intent(AddStageActivity.this, StagesOnCompetitionActivity.class);
                intent.putExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(AddStageActivity.this, ContentProvider.STAGE_CONTENT_URI, null, null, null, null);
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
