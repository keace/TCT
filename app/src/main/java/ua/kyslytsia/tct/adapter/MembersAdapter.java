package ua.kyslytsia.tct.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ua.kyslytsia.tct.MainActivity;
import ua.kyslytsia.tct.R;
import ua.kyslytsia.tct.database.Contract;
import ua.kyslytsia.tct.database.DbHelper;
import ua.kyslytsia.tct.mocks.Person;

public class MembersAdapter extends CursorAdapter {

    //int competitionId; // from Intent

    public MembersAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_member, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        DbHelper dbHelper = MainActivity.dbHelper;

        TextView team = (TextView) view.findViewById(R.id.textViewMembersTeam);
        team.setText(dbHelper.findTeamNameById(cursor.getInt(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_TEAM_ID))));
        // findTeamNameById from getTeam_id from Members

        TextView number = (TextView) view.findViewById(R.id.textViewMembersNumber);
        number.setText(cursor.getString(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_START_NUMBER)));
        // getNumber from Members

        TextView name = (TextView) view.findViewById(R.id.textViewMembersName);
        Person person = dbHelper.findPersonById(cursor.getInt(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_PERSON_ID)));
        StringBuffer sb = new StringBuffer();
        sb.append(person.getSurName()).append(" ").append(person.getFirstName());
        name.setText(sb.toString());
        sb.delete(0, sb.length());
        // getFirstName getSurName from Members or Person

//        TextView time = (TextView) view.findViewById(R.id.textViewMembersTime);
//        time.setText(cursor.getInt(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_TIME)));
        // getTime from Members
    }
}
