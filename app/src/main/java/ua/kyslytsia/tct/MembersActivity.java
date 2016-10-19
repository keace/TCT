package ua.kyslytsia.tct;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import ua.kyslytsia.tct.adapter.MembersAdapter;
import ua.kyslytsia.tct.database.Contract;

public class MembersActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        ListView listView = (ListView) findViewById(R.id.listViewMembers);

        final int competitionId = getIntent().getIntExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);

        SQLiteDatabase sqLiteDatabase = MainActivity.dbHelper.getReadableDatabase();

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.MemberEntry.TABLE_NAME + " WHERE " + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=?;", new String[]{String.valueOf(competitionId)});
        //Cursor c = sqLiteDatabase.query(Contract.MemberEntry.TABLE_NAME, null, null, null, null, null, null);
        MembersAdapter adapter = new MembersAdapter(this, c, true);

        listView.setAdapter(adapter);

        Button buttonToNewMember = (Button) findViewById(R.id.buttonMembersToNewMember);
        buttonToNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToNewMember = new Intent(MembersActivity.this, NewMemberActivity.class);
                intentToNewMember.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intentToNewMember);
            }
        });

        Button buttonToStages = (Button) findViewById(R.id.buttonMembersToStages);
        buttonToStages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToStages = new Intent(MembersActivity.this, StagesOnCompetitionActivity.class);
                intentToStages.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                startActivity(intentToStages);
            }
        });
    }
}
