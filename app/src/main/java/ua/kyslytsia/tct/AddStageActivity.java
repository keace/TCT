package ua.kyslytsia.tct;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;


import ua.kyslytsia.tct.adapter.AddStageCursorAdapter;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class AddStageActivity extends AppCompatActivity {

    ListView listView;
    private String LOG = "LOG ADD STAGE";
    DbHelper dbHelper = MainActivity.dbHelper;
    int competitionId, distanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stage);

        listView = (ListView) findViewById(R.id.listViewAddStage);
        Button buttonAddStages = (Button) findViewById(R.id.buttonAddStages);

        competitionId = getIntent().getIntExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);
        distanceId = dbHelper.findCompetitionById(competitionId).getDistance_id();

        Cursor c = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Contract.StageEntry.TABLE_NAME + " WHERE " + Contract.StageEntry.COLUMN_DISTANCE_ID + "=" + distanceId, null);

        AddStageCursorAdapter adapter = new AddStageCursorAdapter(this, c, true);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        buttonAddStages.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                long[] stageIds = listView.getCheckedItemIds();
                for (int i = 0; i < stageIds.length; i++){
                    Log.d(LOG, "TRY TO ADD compId = " + competitionId + ", stageId = " + stageIds[i]);
                    dbHelper.addStageOnCompetition(competitionId, (int) stageIds[i]);
                }
                Intent intent = new Intent(AddStageActivity.this, StagesOnCompetitionActivity.class);
                intent.putExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intent);
            }
        });
    }
}
