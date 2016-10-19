package ua.kyslytsia.tct;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import ua.kyslytsia.tct.adapter.StageOnCompetitionCursorAdapter;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;

public class StagesOnCompetitionActivity extends AppCompatActivity {

    ListView listView;
    int competitionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_on_competition);

        competitionId = getIntent().getIntExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, 0);

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSoc);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addStageIntent = new Intent(StagesOnCompetitionActivity.this, AddStageActivity.class);
                    addStageIntent.putExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId);
                    startActivity(addStageIntent);
                    /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                            */
                }
            });
        }

        listView = (ListView) findViewById(R.id.listViewStageOnCompetition);
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.StageOnCompetitionEntry.TABLE_NAME + " WHERE " + Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID + " = " + competitionId, null);
        StageOnCompetitionCursorAdapter adapter = new StageOnCompetitionCursorAdapter(this, c, true);
        listView.setAdapter(adapter);

        Button buttonToMembers = (Button) findViewById(R.id.buttonSoCToMembers);
        buttonToMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StagesOnCompetitionActivity.this, MembersActivity.class);
                intent.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intent);
            }
        });

        Button buttonAddStages = (Button) findViewById(R.id.buttonSoCAddStage);
        buttonAddStages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StagesOnCompetitionActivity.this, AddStageActivity.class);
                intent.putExtra(Contract.StageOnCompetitionEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intent);
            }
        });
    }
}
