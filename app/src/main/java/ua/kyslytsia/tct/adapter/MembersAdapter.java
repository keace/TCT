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

    public MembersAdapter(Context context, Cursor c, int flag) {
        super(context, c, flag);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_member, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView team = (TextView) view.findViewById(R.id.textViewMembersTeam);
        team.setText(cursor.getString(cursor.getColumnIndex(Contract.TEAM_NAME_ADAPTED)));

        TextView number = (TextView) view.findViewById(R.id.textViewMembersNumber);
        number.setText(cursor.getString(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_START_NUMBER)));

        TextView name = (TextView) view.findViewById(R.id.textViewMembersName);
        StringBuffer sb = new StringBuffer();
        sb.append(cursor.getString(cursor.getColumnIndex(Contract.PersonEntry.COLUMN_LASTNAME))).append(" ").append(cursor.getString(cursor.getColumnIndex(Contract.PersonEntry.COLUMN_FIRST_NAME)));
        name.setText(sb.toString());

//        TextView time = (TextView) view.findViewById(R.id.textViewMembersTime);
//        time.setText(cursor.getInt(cursor.getColumnIndex(Contract.MemberEntry.COLUMN_TIME)));
        // getTime from Members
    }
}
