package ua.kyslytsia.tct;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import ua.kyslytsia.tct.adapter.AddStageCursorAdapter;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class AddStageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stage);

        ListView listView = (ListView) findViewById(R.id.listViewAddStage);
        Button buttonAddStages = (Button) findViewById(R.id.buttonAddStages);

        int competitionId = getIntent().getIntExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);

        DbHelper dbHelper = MainActivity.dbHelper;
        int distanceId = dbHelper.findCompetitionById(competitionId).getDistance_id();

        Cursor c = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Contract.StageEntry.TABLE_NAME + " WHERE " + Contract.StageEntry.COLUMN_DISTANCE_ID + "=" + distanceId, null);

        AddStageCursorAdapter adapter = new AddStageCursorAdapter(this, c, true);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
    }
}
