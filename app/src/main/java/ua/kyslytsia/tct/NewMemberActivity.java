package ua.kyslytsia.tct;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import ua.kyslytsia.tct.database.Contract;

public class NewMemberActivity extends AppCompatActivity {

    EditText lastName, firstName, middleName, birthday, startNumber, team;
    RadioGroup gender;
    int competitionId;
    public static final String LOG = "LOG! NewMemberActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        competitionId = getIntent().getIntExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, 0);

        lastName = (EditText) findViewById(R.id.editTextAddMemberLastName);
        firstName = (EditText) findViewById(R.id.editTextAddMemberFirstName);
        middleName = (EditText) findViewById(R.id.editTextAddMemberMiddleName);
        birthday = (EditText) findViewById(R.id.editTextAddMemberBirthday);
        startNumber = (EditText) findViewById(R.id.editTextAddMemberNumber);
        team = (EditText) findViewById(R.id.editTextAddMemberTeam);

        gender = (RadioGroup) findViewById(R.id.radioGroupAddMemberGender);

        final Button buttonAddNewMember = (Button) findViewById(R.id.buttonAddNewMember);
        buttonAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addNewMember()) {
                    Intent intentToMembersActivity = new Intent(NewMemberActivity.this, MembersActivity.class);
                    intentToMembersActivity.putExtra(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                    startActivity(intentToMembersActivity);
                } else {
                    Toast.makeText(getApplicationContext(), "Error! New Member not created!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean addNewMember() {

        int genderId = 1;
        long personId, teamId = 0;
        ContentValues cv = new ContentValues();

        switch (gender.getCheckedRadioButtonId()){
            case R.id.radioButtonAddMemberGenderM:
                genderId = Contract.GENDER_MALE;
                break;
            case R.id.radioButtonAddMemberGenderF:
                genderId = Contract.GENDER_FEMALE;
                break;
        }
        /* not null fields handling */
        if (lastName.getText().toString().trim().equals("")){
            lastName.setError("Фамилия обязательна!");
        } else if (firstName.getText().toString().trim().equals("")) {
            firstName.setError("Имя обязательно!");
        } else {
            SQLiteDatabase sqLiteDatabase = MainActivity.dbHelper.getWritableDatabase();

            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.PersonEntry.TABLE_NAME + " WHERE " + Contract.PersonEntry.COLUMN_LASTNAME + "=? AND " + Contract.PersonEntry.COLUMN_FIRST_NAME + "=?;", new String[] {lastName.getText().toString(), firstName.getText().toString()});
//            SQLiteStatement stmt = sqLiteDatabase.compileStatement("SELECT * FROM " + Contract.PersonEntry.TABLE_NAME + " WHERE " + Contract.PersonEntry.COLUMN_LASTNAME + "=? AND " + Contract.PersonEntry.COLUMN_FIRST_NAME + "=?;");
//            stmt.bindString(1, lastName.getText().toString());
//            stmt.bindString(2, firstName.getText().toString());

            if (c.getCount() == 0){
                Log.d(LOG, "c.getCount = " + c.getCount());
            /* New Person creating if not exists with some lastName and firstName*/
                cv.put(Contract.PersonEntry.COLUMN_LASTNAME, lastName.getText().toString());
                cv.put(Contract.PersonEntry.COLUMN_FIRST_NAME, firstName.getText().toString());
                cv.put(Contract.PersonEntry.COLUMN_MIDDLE_NAME, middleName.getText().toString());
                cv.put(Contract.PersonEntry.COLUMN_GENDER_ID, genderId);
                cv.put(Contract.PersonEntry.COLUMN_BIRTHDAY, birthday.getText().toString());
                personId = sqLiteDatabase.insert(Contract.PersonEntry.TABLE_NAME, null, cv);
                Log.d(LOG, "New Person Create. c.getCount = " + c.getCount() + "; newPersonId = " + personId);
                cv.clear();
            } else {
                Log.d(LOG, "Person exists. c.getCount = " + c.getCount());
                c.moveToFirst();
                personId = c.getInt(c.getColumnIndex(Contract.PersonEntry._ID));
                Log.d(LOG, "Person exists. PersonId = " + personId);
            }

            /* New Team Creating if user wrote it and if not exists*/
            if (!team.getText().toString().trim().equals("")) {
                c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.TeamEntry.TABLE_NAME + " WHERE " + Contract.TeamEntry.COLUMN_COMPETITION_ID + "=? AND " + Contract.TeamEntry.COLUMN_NAME + "=?;", new String[] {String.valueOf(competitionId), team.getText().toString()});
                Log.d(LOG, "Field team not clear, getting teamId");
                if (c.getCount() == 0) {
                    Log.d(LOG, "Creating new team");
                    cv.put(Contract.TeamEntry.COLUMN_COMPETITION_ID, competitionId);
                    cv.put(Contract.TeamEntry.COLUMN_NAME, team.getText().toString());
                    teamId = sqLiteDatabase.insert(Contract.TeamEntry.TABLE_NAME, null, cv);
                    cv.clear();
                    Log.d(LOG, "Created new team with teamId = " + teamId);
                } else {
                    Log.d(LOG, "Team already exists");
                    c.moveToFirst();
                    teamId = c.getInt(c.getColumnIndex(Contract.TeamEntry._ID));
                    Log.d(LOG, "Team with this name already exists with teamId = " + teamId);
                }
            }

            /* New Member Creating */
            c = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.MemberEntry.TABLE_NAME + " WHERE " + Contract.MemberEntry.COLUMN_COMPETITION_ID + "=? AND " + Contract.MemberEntry.COLUMN_PERSON_ID + "=?;", new String[]{String.valueOf(competitionId), String.valueOf(personId)});
            if (c.getCount() == 0) {
                cv.put(Contract.MemberEntry.COLUMN_COMPETITION_ID, competitionId);
                cv.put(Contract.MemberEntry.COLUMN_PERSON_ID, personId);
                if (teamId != 0) {
                    cv.put(Contract.MemberEntry.COLUMN_TEAM_ID, teamId);
                }
                if (!startNumber.getText().toString().trim().equals("")){
                    cv.put(Contract.MemberEntry.COLUMN_START_NUMBER, startNumber.getText().toString());
                }
                sqLiteDatabase.insert(Contract.MemberEntry.TABLE_NAME, null, cv);
                cv.clear();
                c.close();
                sqLiteDatabase.close();
            } else {
                Toast.makeText(getApplicationContext(), "Not created! Already exists", Toast.LENGTH_SHORT).show();
                Log.d(LOG, "Member already exists and c.getCount = " + c.getCount());
            }
        }
        return true;
    }
}
